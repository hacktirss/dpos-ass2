package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;

public class VentaDivisasAction extends ImprimeTransaccionAction {
    
    private static final String ERROR = "No es posible marcar como JARREO una transaccion enviada a PEMEX, asignada a un Cliente o Facturada";

    public static final String WS_PRMT_DIVISA = "divisa";

    public VentaDivisasAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private boolean exist(String idConsumo) {
        String sql = "SELECT * FROM formas_de_pago WHERE id = " +  idConsumo;

        try {
            LogManager.info("Marcando consumo como venta con divisa " + idConsumo);
            LogManager.debug(sql);
            return !OmicromSLQHelper.empty(sql);
        } catch (DBException ex) {
            LogManager.info("Error arcando consumo como venta con divisa " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
    private boolean marcaDivisa(String idConsumo, String divisa) throws DetiPOSFault {

        boolean updated = false;

        if (StringUtils.isNVL(idConsumo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No se encontro el consumo"));
        }

        if (exist(idConsumo)) return true;

        String sql = "INSERT INTO formas_de_pago (id, clave, descripcion, detalle, monto) " +
                        "SELECT rm.id, '01', M.clave, D.tipo_de_cambio, ROUND(rm.pesos / D.tipo_de_cambio, 2) monto " +
                        "FROM rm JOIN divisas D JOIN cfdi33_c_moneda M ON M.clave = D.clave " +
                        "WHERE D.clave = '" + divisa + "' AND rm.id = " +  idConsumo;

        try {
            LogManager.info(String.format("Marcando consumo %s con divisa %s", idConsumo, divisa));
            LogManager.debug(sql);
            updated = MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error marcando consumo %s con divisa %s", idConsumo, divisa));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+idConsumo));
        }

        return updated;
    }//updateComprobante

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_DIVISA)) {
            throw new DetiPOSFault("Se esperaba el parametro TIPO DE DIVISA", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Se esperaba el parametro TIPO DE JARREO", "Se esperaba el parametro TIPO DE DIVISA"));
        }
        return this;
    }//validateRequest
    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DinamicVO<String, String> ven;

        try {
            ven = VendedoresDAO.getNameByNIP(parameters.NVL(WS_PRMT_AUTH));
        } catch (DBException DBE) {
            throw new DetiPOSFault(DBE.getMessage());
        }

        if ("INCORRECTO".equals(ven.NVL("nip")) || ven.isNVL("NOMBRE")) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error de Parametros", "Error validando el nip del despachador"));
        }//if

        DinamicVO<String, String> ticket = retrieveData(parameters.NVL(WS_PRMT_TRANSACCION));

        if ("1".equals(ven.NVL("fix")) && !ticket.NVL("POSICION").equals(ven.NVL("posicion"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error marcando jarreo", ven.NVL("NOMBRE") + " no está asignado a la posición " + ticket.NVL("POSICION")));
        }

        if (!marcaDivisa(parameters.NVL(WS_PRMT_TRANSACCION), parameters.NVL(WS_PRMT_DIVISA))) {
            throw new DetiPOSFault(ERROR, 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ERROR, ERROR));
        }

        return super.getComprobante();
    }//getComprobante
}//SaldoAction
