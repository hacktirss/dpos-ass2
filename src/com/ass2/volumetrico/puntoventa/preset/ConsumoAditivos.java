/*
 * ConsumoAditivos
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.data.ClientesBalanceVO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.CuentasPorCobrarDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.InventarioDAO;
import com.ass2.volumetrico.puntoventa.data.InventarioVO;
import com.ass2.volumetrico.puntoventa.data.IslaDAO;
import com.ass2.volumetrico.puntoventa.data.IslaVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.PosicionesVO;
import com.ass2.volumetrico.puntoventa.data.UnidadVO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.ass2.volumetrico.puntoventa.data.VentaAditivosDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import com.ass2.volumetrico.puntoventa.data.VentaAditivosVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_BANK_ID;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_PIN_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.Getter;
import lombok.Setter;

public class ConsumoAditivos extends Consumo {

    protected BaseVO aditivo;
    protected VentaAditivosVO venta;
    protected PosicionesVO man;

    @Getter private final BaseVO cliente;
    @Getter private final BaseVO unidad;
    @Getter @Setter private VendedoresVO ven;

    public static final String PRMT_CLAVE_ADITIVO   = "claveAditivo";
    public static final String PRMT_CODIGO_ADITIVO  = "codigoAditivo";
    public static final String PRMT_REFERENCIA      = "referencia";

    public ConsumoAditivos() {
        unidad = new UnidadVO();
        cliente = new ClientesVO();
    }

    public InventarioVO getAditivo() {
        return (InventarioVO) aditivo;
    }

    public VentaAditivosVO getVentaAditivo() {
        return venta;
    }

    private void autorizacionLocal() throws DetiPOSFault {
        DinamicVO<String, String> permitido;

        try {
            LogManager.info("Consultando consumo permitido " + unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()));
            permitido = ConsumosDAO.getConsumoDisponible(
                            unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()),
                            unidad.NVL(UnidadVO.UND_FIELDS.periodo.name()));
            LogManager.debug(permitido);
        } catch (DetiPOSFault ex) {
            LogManager.info("Error iniciando Unidad");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando la tarjeta", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, ex, "Consultando la tarjeta " + parameters.NVL(PRMT_ACCOUNT)));
        }

        if ("d".equals(unidad.NVL("ESTADO"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de parametros", "El Código " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)) + "no esta activo."));
        }

        if (cliente.isVoid()) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de parametros", "El Código " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)) + " no está asignado a un cliente o el cliente no existe."));
        }

        if ("No".equals(cliente.NVL("ACTIVO"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Cliente inactivo", "El cliente asignado al código " + unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()) + " no esta activo."));
        }
        
        if ("Si".equals(unidad.NVL("PIDENIP")) 
                && !unidad.NVL("NIP").equals("-----") 
                && !parameters.NVL(PRMT_PIN_ACCOUNT).equals(unidad.NVL("NIP"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error en el NIP", "El NIP ingresado es incorrecto."));
        }

        if (!"S".equals(unidad.NVL("ALLOWED"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error en el codigo", "Codigo fuera del horario autorizado."));
        }

        if (!unidad.NVL("COMBUSTIBLE").contains("*")
                && !unidad.NVL("COMBUSTIBLE").contains(getCombustible().NVL(CombustibleVO.COM_FIELDS.descripcion.name()))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error en el codigo", "Combustible " + getCombustible().NVL(CombustibleVO.COM_FIELDS.descripcion.name()) + " no permitido"));
        }

        if ("S".equals(cliente.NVL("CHECK_IMPORTES"))) {
            // Validate card limits 
            if ((unidad.isNVL("IMPORTE") || "0".equals(unidad.NVL("IMPORTE"))) 
                    && (unidad.isNVL("LITROS") || "0".equals(unidad.NVL("LITROS")))) {
                throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error en el codigo", "Tarjeta sin definir limites."));
            }

            // Checks client's balance
            if ("S".equals(cliente.NVL("CHECK_BALANCE")) && getImporte().compareTo(new BigDecimal(cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")))>0) {
                throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Saldo insuficiente", "Saldo insuficiente " + cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")));
            }
        }
    }

    private void initUnidad() throws DetiPOSFault {
        try {
            if (!parameters.isNVL(PRMT_REFERENCIA)) {
                validateReferencia(Integer.parseInt(parameters.NVL(PRMT_REFERENCIA)));
                ConsumoVO referencia = ConsumosDAO.getByID(Integer.parseInt(parameters.NVL(PRMT_REFERENCIA)));
                LogManager.debug(referencia);
                LogManager.info("Consultando cliente por referencia " + parameters.NVL(PRMT_REFERENCIA));
                cliente.setEntries(ClientesDAO.getClienteByID(referencia.NVL("cliente")));

                if (referencia.getCampoAsInt(ConsumoVO.RM_FIELDS.cliente.name())>0) {
                    LogManager.info("Consultando unidad por referencia " + parameters.NVL(PRMT_REFERENCIA));
                    unidad.setEntries(UnidadesDAO.getUnidadV01(referencia.NVL(ConsumoVO.RM_FIELDS.codigo.name())));
                }
            } else if (!parameters.isNVL(PRMT_ACCOUNT) || !parameters.isNVL(PRMT_BANK_ID)) {
                LogManager.info("Consultando unidad " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)));
                unidad.setEntries(UnidadesDAO.getUnidadV01(parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID))));
                parameters.setField(PRMT_ACCOUNT, unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()));
                LogManager.debug(unidad);

                LogManager.info("Consultando cliente " + unidad.NVL(UnidadVO.UND_FIELDS.cliente.name()));
                cliente.setEntries(ClientesDAO.getClienteByID(unidad.NVL(UnidadVO.UND_FIELDS.cliente.name())));
            } else {
                cliente.setEntries(ClientesDAO.getClienteByID("0"));
            }

            LogManager.debug(cliente);
        } catch (DetiPOSFault ex) {
            LogManager.info("Error iniciando Unidad");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando la tarjeta", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, ex, "Consultando la tarjeta " + parameters.NVL(PRMT_ACCOUNT)));
        }
    }//initUnidad

    @Override
    protected void loadManguera() throws DetiPOSFault {
        try {
            man = (PosicionesVO) ManguerasDAO.getDispensario(getParameters().NVL(PRMT_CNS_POS));
            posicion = EstadoPosicionesDAO.getByPosicion(man.NVL(PosicionesVO.DSP_FIELDS.posicion.name()));
            isla = IslaDAO.getIslaByID(man.NVL(PosicionesVO.DSP_FIELDS.isla.name()));
            LogManager.debug("Solicitando inventario " + parameters.NVL(PRMT_CODIGO_ADITIVO));
            aditivo = parameters.isNVL(PRMT_CLAVE_ADITIVO) ?
                    InventarioDAO.getInventarioByCodigoIsla(parameters.NVL(PRMT_CODIGO_ADITIVO).trim().replaceAll("\n", "").replaceAll("\r", ""), man.NVL(PosicionesVO.DSP_FIELDS.posicion.name())) :
                    InventarioDAO.getInventarioByIsla(parameters.NVL(PRMT_CLAVE_ADITIVO), man.NVL(PosicionesVO.DSP_FIELDS.posicion.name()));
            parameters.setField(PRMT_CLAVE_ADITIVO, aditivo.getField("ID"));
            ven = VendedoresDAO.getNameByNIPI(parameters.NVL(PRMT_PIN_EMPLOYEE), parameters.NVL(ConsumoAditivos.PRMT_CNS_POS));
        } catch (DBException ex) {
            LogManager.error(ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    private boolean validateReferencia(int referencia) throws DetiPOSFault {
        if (!"-----".equals(ConsumosDAO.getByID(referencia).NVL(ConsumoVO.RM_FIELDS.uuid.name()))) {
            throw new DetiPOSFault("No se pueden agregar aditivos a una venta facturada");
        }
        return true;
    }
    @Override
    public boolean validate() throws DetiPOSFault {
        super.validate();

        if (aditivo.isVoid()) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el aditivo", "No se encontro el producto " + parameters.NVL(PRMT_CLAVE_ADITIVO, parameters.NVL(PRMT_CODIGO_ADITIVO))));
        }
        if (!"Si".equals(aditivo.NVL("HAS_INVENTARIO"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el aditivo", "La posicion no maneja inventario"));
        }

        if (!unidad.isNVL("ID") && !"1".equals(unidad.NVL("ADITIVOS"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el aditivo", "Consumo de aditivos no permitido"));
        }

        if (parameters.NVL(PRMT_PIN_EMPLOYEE).length() != 0) {
            if (!ven.isAssigned(parameters.NVL(ConsumoAditivos.PRMT_CNS_POS))) {
                throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error consultando vendedor ", ven.NVL("nombre") + " no está asignado a la posición " + parameters.NVL(ConsumoAditivos.PRMT_CNS_POS)));
            }
            if (ven.isInvalidLogin()) {
                throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error validando despachador", "Password incorrecto"));
            }
        }

        return true;
    }//validate

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        populate(parameters);
        loadManguera();
        initUnidad();
    }//init

    @Override
    public boolean exec() throws DetiPOSFault {
        String vtaIdString;

        if (0>=aditivo.getCampoAsInt("EXISLA")) {
            LogManager.error(new DetiPOSFault("No hay existencias del producto " + parameters.NVL(PRMT_CLAVE_ADITIVO, parameters.NVL(PRMT_CODIGO_ADITIVO)) + ". Verifique el inventario."));
        }

        vtaIdString = "VADT:"+man.NVL(PosicionesVO.DSP_FIELDS.posicion.name())+":"+parameters.NVL(PRMT_CLAVE_ADITIVO);
        LogManager.info(vtaIdString+" Existencias : "+aditivo.getCampoAsInt("EXISLA")+" Movimiento (-)"+parameters.NVL(PRMT_CNS_CNT));

        InventarioDAO.updateInventario(parameters.NVL(PRMT_CLAVE_ADITIVO), man.NVL(PosicionesVO.DSP_FIELDS.posicion.name()), parameters.NVL(PRMT_CNS_CNT));
        insertVentaAditivo();

        aditivo = parameters.isNVL(PRMT_CLAVE_ADITIVO) ? 
                InventarioDAO.getInventarioByCodigoIsla(parameters.NVL(PRMT_CODIGO_ADITIVO).trim().replaceAll("\n", "").replaceAll("\r", ""), man.NVL(PosicionesVO.DSP_FIELDS.posicion.name())) : 
                InventarioDAO.getInventarioByIsla(parameters.NVL(PRMT_CLAVE_ADITIVO), man.NVL(PosicionesVO.DSP_FIELDS.posicion.name()));
        LogManager.info(vtaIdString+" Existencias : " + aditivo.getCampoAsInt("EXISLA")+" vtaditivos.id="+venta.NVL(VentaAditivosVO.VTA_FIELDS.id.name()));
        return true;
    }//exec

    private boolean updateComprobante(String idConsumo) throws DetiPOSFault {
        String sql = "UPDATE rm SET enviado = 0 WHERE id = " + idConsumo;
        boolean updated = false;
        try {
            updated = MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando referencia " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando la referenci "+idConsumo));
        }

        return updated;
    }

    private boolean insertVentaAditivo() throws DetiPOSFault {        
        String shortDescription = aditivo.NVL(InventarioVO.INV_FIELDS.descripcion.name());
        String iva;
        String vtaId = "";
        boolean insertCXC;

        while (45<shortDescription.length()) {
            if (shortDescription.lastIndexOf("[ ]")<=0)
                shortDescription = shortDescription.substring(0, 45);
            else
                shortDescription = shortDescription.substring(shortDescription.lastIndexOf("[ ]")).trim();
        }

        try {
            iva = OmicromSLQHelper.getUnique("SELECT factoriva/100 iva FROM inv WHERE id = " + aditivo.NVL(InventarioVO.INV_FIELDS.id.name())).NVL("iva");

            LogManager.info("Consumo de Aditivos para el cliente " + cliente.NVL("ID"));
            insertCXC = "Credito|Prepago|Tarjeta|Monedero".contains(cliente.NVL("TIPODEPAGO")) ;
            String sql = "INSERT INTO vtaditivos ( clave, cantidad, unitario, costo, total, corte, posicion, fecha, descripcion, cliente, codigo, vendedor, referencia, iva ) "
                    + "VALUES( ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ? )";
            try (Connection conn = MySQLHelper.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, parameters.NVL(PRMT_CLAVE_ADITIVO));
                ps.setInt(2, new BigDecimal(parameters.NVL(PRMT_CNS_CNT)).intValue());
                ps.setBigDecimal(3, new BigDecimal(aditivo.NVL(InventarioVO.INV_FIELDS.precio.name())));
                ps.setBigDecimal(4, new BigDecimal(aditivo.NVL(InventarioVO.INV_FIELDS.costo.name())));
                ps.setBigDecimal(5, new BigDecimal(aditivo.NVL(InventarioVO.INV_FIELDS.precio.name())).multiply(new BigDecimal(parameters.NVL(PRMT_CNS_CNT))));
                ps.setInt(6, Integer.parseInt(isla.NVL(IslaVO.ISL_FIELDS.corte.name())));
                ps.setInt(7, Integer.parseInt(parameters.NVL(PRMT_CNS_POS)));
                ps.setString(8, shortDescription);
                ps.setInt(9, Integer.parseInt(cliente.NVL("ID")));
                ps.setString(10, unidad.isNVL("ID") ? "0" : unidad.NVL("CODIGO"));
                ps.setInt(11, ven.getCampoAsInt("id"));
                ps.setInt(12, Integer.parseInt(parameters.NVL(PRMT_REFERENCIA, "0")));
                ps.setBigDecimal(13, new BigDecimal(StringUtils.NVL(iva, "0")));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vtaId = rs.getString(1);
                        venta = new VentaAditivosVO(VentaAditivosDAO.getById(vtaId));
                    }
                }
            }

            if (!"0".equals(parameters.NVL(PRMT_REFERENCIA, "0"))) {
                updateComprobante(parameters.NVL(PRMT_REFERENCIA));
            }

            if (insertCXC) {
                CuentasPorCobrarDAO.insertAditivo(vtaId);
            }

            return true;
        } catch (SQLException | DBException ex) {
            LogManager.info("Error");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw  new DetiPOSFault("No se encontró el producto");
        }
    }
}
