package com.ass2.volumetrico.puntoventa.services.actions;

import com.detisa.fae.DataServer;
import com.detisa.fae.Exception_Exception;
import com.detisa.fae.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.FacturacionUtils;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.data.BoletosDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.ass2.volumetrico.puntoventa.data.VentaAditivosDAO;
import com.ass2.volumetrico.puntoventa.data.VentaAditivosVO;
import com.detisa.omicrom.integraciones.ImprimeIntegracion;
import com.detisa.omicrom.integraciones.ImprimeIntegracionFactory;
import com.detisa.omicrom.integraciones.monederos.omicrom.PuntosOmicromApi;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.utils.qr.QRFactory;
import com.ass2.volumetrico.puntoventa.utils.qr.QRGenerator;
import com.google.gson.JsonParser;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImprimeTransaccionAction extends BaseAction {

    public static final String RMI_PROPERTIES = "dposrmi.properties";

    public static final String RMI_CLIENTE = "dpos.rmi.balance.cliente.endpoint";
    public static final String RMI_TARJETA = "dpos.rmi.query.tarjeta.endpoint";

    public static final String SQL_CONSUMO = "com/detisa/omicrom/sql/SelectConsumo.sql";
    public static final String SQL_TRANSACCION = "[$]TR";

    public static final String PATTERN_CODIGO  = "([0-9]{19,22})|([A-Za-z]+?[0-9]+)";
    public static final String PATTERN_BOLETOS = "([0-9]{10,13}[|]?)+";
    public static final String PATTERN_TARJETA = "([0-9a-fA-F]{8})|([0-9]{16})";

    private DinamicVO<String, String> ticket;
    private final VendedoresVO ven;

    public ImprimeTransaccionAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        ticket = retrieveData(parameters.NVL(WS_PRMT_TRANSACCION));
        ven = retrieveNIPDespachador(parameters.NVL(WS_PRMT_AUTH), ticket.NVL("POSICION"));
    }//Constructor

    private BigDecimal getSaldoCorporativo(String cliente) {
        try {
            LogManager.info("Consultando saldo del cliente en Corporativo. Cliente ID = " + cliente);
            DataServer port = RMIClientFactory.getDataServerPort(VariablesDAO.getCorporativo("url_sync_data"), "DataServer", "DataServer", 10000);
            BigDecimal saldo = new BigDecimal(port.consultaSaldoCliente(String.valueOf(VariablesDAO.getIdFAE()), cliente));
            return "0".equals(ticket.NVL("ENVIADO")) ? saldo.subtract(new BigDecimal(ticket.NVL("PESOS"))).setScale(2, RoundingMode.HALF_EVEN): saldo;
        } catch (MalformedURLException | DetiPOSFault | Exception_Exception ex) {
            LogManager.info("Error consultando saldo del cliente en Corporativo. Cliente ID = " + cliente);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getSaldoLocal(String cliente) {
        try {
            LogManager.info("Consultando saldo del cliente localmente. Client ID = " + cliente);
            BaseVO baseVO = ClientesDAO.getClienteBalanceByID(cliente);
            return new BigDecimal(baseVO.NVL("SALDO"));
        } catch (DetiPOSFault ex) {
            LogManager.info("Error consultando saldo del cliente localmente. Client ID = " + cliente);
            LogManager.debug(ex);
            return BigDecimal.ZERO;
        }
    }

    private String getSaldoTarjetaLocal(String codigo) {
        try {
            LogManager.info("Consultando saldo de la tarjeta localmente. Tarjeta = " + codigo);
            DinamicVO<String, String> saldoTarjeta = ConsumosDAO.getSaldoTarjeta(codigo);
            BigDecimal permitido = new BigDecimal(saldoTarjeta.NVL("permitido"));
            BigDecimal saldo = new BigDecimal(saldoTarjeta.NVL("saldo"));
            if ("P".equals(saldoTarjeta.NVL("tipo"))) {
                return "Disponible $ " + ( saldo.compareTo(permitido) >= 0 ? permitido : saldo );
            }
        } catch (DetiPOSFault ex) {
            LogManager.info("Error consultando saldo de la tarjeta localmente. Tarjeta = " + codigo);
            LogManager.debug(ex);
        }
        return "NA";
    }

    private Comprobante determinePuntos(String id) {
        try {
            DinamicVO<String, String> puntos = PuntosOmicromApi.getPuntosConsumo(Integer.parseInt(id));
            LogManager.info(puntos);
            return StringUtils.isNVL(puntos.NVL("@PuntosTicket")) ? 
                    new Comprobante() :
                    new Comprobante()
                        .append("PNT_CONSUMO", puntos.NVL("@PuntosTicket"))
                        .append("PNT_INICIALES", puntos.NVL("@TotalAntesDeVenta"))
                        .append("PNT_FINALES", puntos.NVL("@Totalpuntos"));
        } catch (DetiPOSFault ex) {
            LogManager.info("Error consultando puntos");
            LogManager.debug(ex);
        }
        return new Comprobante();
    }

    private DinamicVO<String, String> getCliente(String id) throws DetiPOSFault {
        DinamicVO<String, String> cliente = ClientesDAO.getClienteByID(id);

        if (cliente.isVoid()) {
            throw new DetiPOSFault("Error consultando el cliente", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando el cliente", "El cliente no existe: " + id));
        }
        return cliente;
    }

    private DinamicVO<String, String> getTarjeta(String id) throws DetiPOSFault {
        DinamicVO<String, String> tarjeta = UnidadesDAO.getUnidadV01(id);

        if (tarjeta.isVoid()) {
            LogManager.error("Error consultando la tarjeta. La tarjeta no existe: " + id);
        }
        return tarjeta;
    }

    private DinamicVO<String, String> consultaBoleto(String codigo) {
        try {
            return BoletosDAO.getBoletoByID(codigo);
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
        return null;
    }
    private List <DinamicVO<String, String>> getBoletos(String id) throws DetiPOSFault {
        return Stream.of(id.split(Pattern.quote("|")))
                    .map(this::consultaBoleto).filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected final DinamicVO<String, String> retrieveData(String transaccion) throws DetiPOSFault {

        DinamicVO<String, String> data = new DinamicVO<>();
        try {
            QRGenerator qrGenerator = QRFactory.getFactory(VariablesDAO.getCorporativo("dpos_qr_generator", "com.detisa.omicrom.utils.qr.DefaultQRGenerator"));
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CONSUMO).replaceAll(SQL_TRANSACCION, transaccion);
            LogManager.info("Consultando consumo " + transaccion);
            LogManager.debug(sql);
            data.setEntries(OmicromSLQHelper.getUnique(sql));
            data.setEntries(qrGenerator.getQR(Integer.parseInt(transaccion)));
        } catch (DBException | IOException ex) {
            LogManager.info("Error consultando consumo " + transaccion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando el consumo "+transaccion));
        } catch (ReflectiveOperationException ex) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando el consumo "+transaccion));
        }

        return data;
    }//retrieveData

    private boolean updateComprobante(String idConsumo) throws DetiPOSFault {
        StringBuilder sqlBuffer = new StringBuilder();
        boolean updated = false;

        if (StringUtils.isNVL(idConsumo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error actualizando comprobante", "No se encontro el consumo " + idConsumo));
        }

        sqlBuffer.append("UPDATE rm ");
        sqlBuffer.append("SET comprobante = comprobante + 1 ");
        sqlBuffer.append("WHERE id = ").append(idConsumo);

        try {
            LogManager.info("Actualizando contador de impresiones " + idConsumo);
            LogManager.debug(sqlBuffer);
            updated = MySQLHelper.getInstance().execute(sqlBuffer.toString());
        } catch (DBException ex) {
            LogManager.info("Error actualizando contador de impresiones " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+idConsumo));
        }

        return updated;
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TRANSACCION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TRANSACCION"));
        }
        if (!ven.isAssigned(ticket.NVL("POSICION"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error imprimiendo comprobante", ven.NVL("nombre") + " no está asignado a la posición " + ticket.NVL("POSICION")));
        }
        if (ven.isInvalidLogin()) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error validando despachador", "Password incorrecto"));
        }
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = super.getComprobante();

        String gTotal;
        String descuento;

        ticket = retrieveData(parameters.NVL(WS_PRMT_TRANSACCION));
        ticket.setField("LETRA", FacturacionUtils.importeLetra(ticket.NVL("PESOS", "0.0")));
        if (!updateComprobante(ticket.NVL("TR"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error actualizando el numero de comprobante", "Para la posicion "+parameters.NVL(WS_PRMT_POSICION)));
        }

        if (!ticket.isNVL("CODIGO") && !"0".equals(ticket.NVL("CODIGO"))) {
            if (ticket.NVL("CODIGO").matches(PATTERN_TARJETA) || ticket.NVL("CODIGO").matches(PATTERN_CODIGO)) {
                comprobante.append(new Comprobante(getTarjeta(ticket.NVL("CODIGO")), "CC_"));
                if ("1".equals(ticket.NVL("PRINT_SALDO")) && !"S".equals(VariablesDAO.getCorporativo("autorizacion_corporativo"))) {
                    comprobante.append("SALDOTARJETA", getSaldoTarjetaLocal(ticket.NVL("CODIGO")));
                }
            } else if (ticket.NVL("CODIGO").matches(PATTERN_BOLETOS)) {
                int[] idxBoleto = {1};
                ticket.setField("TIPODEPAGO", "Boletos");
                getBoletos(ticket.NVL("CODIGO")).forEach(boleto -> {
                        LogManager.info(boleto);
                        comprobante.append("BOL" + idxBoleto[0], boleto.NVL("ID"))
                            .append("BOL_CODIGO@BOL" + idxBoleto[0], boleto.NVL("CODIGO"))
                            .append("BOL_SALDO@BOL" + idxBoleto[0], boleto.NVL("IMPORTEDISPONIBLE"))
                            .append("BOL_IMPORTE@BOL" + idxBoleto[0]++, parameters.NVL(WS_PRMT_TRANSACCION).equals(boleto.NVL("TICKET")) ? 
                                            boleto.NVL("IMPORTE1") : boleto.NVL("IMPORTE2"));
                        ticket.setField("CLIENTE", boleto.NVL("CLIENTE"));
                        ticket.setField("CLI_ID", boleto.NVL("CLIENTE"));
                });
            }
        }

        comprobante.append(new Comprobante(ticket));
        ImprimeIntegracion integracion = ImprimeIntegracionFactory.get(new JsonParser().parse(ticket.NVL("TRJSON")));
        if (integracion!=null) {
            comprobante.append("TRJSON", "1");
            comprobante.append(integracion.extract());
        }

        if (!ticket.isNVL("CLIENTE") && !"0".equals(ticket.NVL("CLIENTE"))) {
            try {
                comprobante.append(new Comprobante(getCliente(ticket.NVL("CLIENTE")), "CLI_"));
                LogManager.info("Tipo de Pago " + ticket.NVL("TIPODEPAGO"));
                if (ticket.NVL("TIPODEPAGO", "Contado").toUpperCase().matches("(CREDITO)|(PREPAGO)|(PUNTOS)")) {
                    comprobante.append(this.determinePuntos(ticket.NVL("TR")));
                }
            } catch (DetiPOSFault ex) {
                LogManager.error(ex);
            }
        }

        if ("1".equals(ticket.NVL("PRINT_SALDO"))) {
            comprobante.append("SALDOCLIENTE", "S".equals(VariablesDAO.getCorporativo("autorizacion_corporativo")) ?
                    getSaldoCorporativo(ticket.NVL("CLIENTE")).toPlainString() :
                    getSaldoLocal(ticket.NVL("CLIENTE")).toPlainString());
        }

        if (!ven.isNVL("NOMBRE") && !"NO PIDE".equals(ven.NVL("nip"))) {
            comprobante.append("DESPACHADOR", ven.NVL("NOMBRE"));
        }

        int[] adt = {1};
        VentaAditivosDAO.getByReference(ticket.NVL("TR")).forEach(aditivo -> comprobante
                            .append("ADT" + adt[0], aditivo.NVL(VentaAditivosVO.VTA_FIELDS.id.name()))
                            .append(new Comprobante(aditivo, "ADT" + adt[0]++, VentaAditivosVO.VTA_FIELDS.id.name(), "")));

        descuento = ConsumosDAO.getDescuento(ticket.NVL("TR"));
        if (!StringUtils.isNVL(descuento)) {
            comprobante.append("DESCUENTO", descuento);
        }

        gTotal = ConsumosDAO.getGrandTotal(ticket.NVL("TR"));
        if (!StringUtils.isNVL(gTotal)) {
            comprobante.append("GTOTAL", gTotal);
            comprobante.append("LTOTAL", FacturacionUtils.importeLetra(gTotal));
        }
        return comprobante;
    }
}
