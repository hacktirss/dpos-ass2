package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.FacturacionUtils;
import com.softcoatl.database.mysql.MySQLHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.detisa.commons.TransactionEncoder;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;

public class ImprimeTicketAction extends BaseAction {

    public static final String RMI_PROPERTIES = "dposrmi.properties";

    public static final String RMI_CLIENTE = "dpos.rmi.balance.cliente.endpoint";
    public static final String RMI_TARJETA = "dpos.rmi.query.tarjeta.endpoint";

    public static final String SQL_CONSUMO_POSICION = "com/detisa/omicrom/sql/SelectConsumoPosicion.sql";
    public static final String SQL_POSICION = "[$]POSICION";

    public ImprimeTicketAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private DinamicVO<String, String> getCliente(String id) throws DetiPOSFault {
        DinamicVO<String, String> cliente = ClientesDAO.getClienteByID(id);

        if (cliente.isVoid()) {
            throw new DetiPOSFault("Error consultando el cliente", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando el cliente", "El cliente no existe: " + id));
        }
        return cliente;
    }//getCliente

    private DinamicVO<String, String> getTarjeta(String id) throws DetiPOSFault {
        DinamicVO<String, String> tarjeta = UnidadesDAO.getUnidadV01(id);

        if (tarjeta.isVoid()) {
            throw new DetiPOSFault("Error consultando la tarjeta", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando la tarjeta", "La tarjeta no existe: " + tarjeta));
        }
        return tarjeta;
    }//getTarjeta

    private DinamicVO<String, String> retrieveData(String posicion) throws DetiPOSFault {

        DinamicVO<String, String> data = new DinamicVO<>();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CONSUMO_POSICION).replaceAll(SQL_POSICION, posicion);
            LogManager.info("Consultando último consumo en la posición " + posicion);
            LogManager.debug(sql);
            data.setEntries(OmicromSLQHelper.getUnique(sql));
            data.setField("FOLIO_FAE", TransactionEncoder.base36Encode(data.NVL("FOLIO_FAE")).replaceAll("(.{6})", "$1-"));
            data.setField("QRC_FAE", data.NVL("FACT_LIGA") + "/rfc.php?ticket=" + data.NVL("FOLIO_FAE"));
        } catch (DBException | IOException ex) {
            LogManager.info("Ocurrió un error recuperando el consumo en la posición "+posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando el consumo en la posicion "+posicion));
        }

        return data;
    }//retrieveData

    private boolean updateComprobante(String idConsumo) throws DetiPOSFault {
        StringBuilder sqlBuffer = new StringBuilder();
        boolean updated = false;

        if (StringUtils.isNVL(idConsumo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No se encontro el consumo"));
        }

        sqlBuffer.append("UPDATE rm ");
        sqlBuffer.append("SET comprobante = comprobante + 1 ");
        sqlBuffer.append("WHERE id = ").append(idConsumo);

        try {
            LogManager.info("Incrementando contador de impresión " + idConsumo);
            LogManager.debug(sqlBuffer);
            updated = MySQLHelper.getInstance().execute(sqlBuffer.toString());
        } catch (DBException ex) {
            LogManager.info("Error incrementando contador de impresión " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+idConsumo));
        }

        return updated;
    }//updateComprobante

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_POSICION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro POSICION"));
        }
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = super.getComprobante();
        DinamicVO<String, String> ticket;

        ticket = retrieveData(parameters.NVL(WS_PRMT_POSICION));
        ticket.setField("LETRA", FacturacionUtils.importeLetra(ticket.NVL("PESOS", "0.0")));
        if (!updateComprobante(ticket.NVL("TR"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error actualizando el numero de comprobante", "Para la posicion "+parameters.NVL(WS_PRMT_POSICION)));
        }
        comprobante.append(new Comprobante(ticket));

        if (!ticket.isNVL("CLIENTE") && !"0".equals(ticket.NVL("CLIENTE"))) {
            try {
                comprobante.append(new Comprobante(getCliente(ticket.NVL("CLIENTE"))));
            } catch (DetiPOSFault ex) {
                LogManager.error(ex);
            }
        }

        if (!ticket.isNVL("CODIGO") && !"0".equals(ticket.NVL("CODIGO"))) {
            try {
                comprobante.append(new Comprobante(getTarjeta(ticket.NVL("CODIGO")), "CC_"));
            } catch (DetiPOSFault ex) {
                LogManager.error(ex);
            }
        }

        return comprobante;
    }//getComprobante
}//SaldoAction
