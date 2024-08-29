package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSInternalFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;

public class ImprimeJarreoAction extends ImprimeTransaccionAction {
    
    private static final String ERROR = "No es posible marcar como JARREO una transaccion enviada a PEMEX, asignada a un Cliente o Facturada";

    public static enum TIPO {
        CONSIGNACION ("N"),   //Consignación
        JARREO ("J"),         //Jarreo
        UVA ("A");            // UVAS/PEMEX

        private final String tipo;
        private static TIPO decode(String tipo) {
            switch (tipo) {
                case "N" : return CONSIGNACION;
                case "J" : return JARREO;
                case "A" : return UVA;
                default: return null;
            }
        }
        private TIPO(String tipo) {
            this.tipo = tipo;
        }
        private String getTipo() {
            return tipo;
        }
    }

    public static final String WS_PRMT_TIPO = "tipoJarreo";
    private final TIPO tipo;

    public ImprimeJarreoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        tipo = TIPO.decode(param.NVL(WS_PRMT_TIPO));
    }//Constructor

    private boolean marcaJarreo(String idConsumo, String tipo, String ven) throws DetiPOSFault {
        String sql = "UPDATE rm " +
                        "SET tipo_venta  = '" + tipo + "', " +
                        "vendedor  = " + ven + ", " +
                        "enviado = 0 " +
                    "WHERE id = " + idConsumo + " " +
                    "AND cliente = 0 AND procesado = 0 AND uuid = '-----'";

        boolean updated = false;

        if (StringUtils.isNVL(idConsumo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No se encontro el consumo"));
        }

        try {
            LogManager.info("Marcando venta como jarreo " + idConsumo);
            LogManager.debug(sql);
            updated = MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error marcando venta como jarreo " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+idConsumo));
        }

        return updated;
    }//updateComprobante

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if("0".equals(VariablesDAO.getCorporativo("permite_jarreo_pos", "1"))) {
            throw new DetiPOSFault("El JARREO desde Terminales esta inhabilitado", new DetiPOSInternalFaultInfo("El JARREO desde Terminales esta inhabilitado", "El JARREO desde Terminales esta inhabilitado"));
        }
        if (parameters.isNVL(WS_PRMT_TIPO)) {
            throw new DetiPOSFault("Se esperaba el parametro TIPO DE JARREO", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Se esperaba el parametro TIPO DE JARREO", "Se esperaba el parametro TIPO DE JARREO"));
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
            throw new DetiPOSFault("Password incorrecto");
        }//if

        DinamicVO<String, String> ticket = retrieveData(parameters.NVL(WS_PRMT_TRANSACCION));

        if ("1".equals(ven.NVL("fix")) && !ticket.NVL("POSICION").equals(ven.NVL("posicion"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error marcando jarreo", ven.NVL("NOMBRE") + " no esta asignado a la posición " + ticket.NVL("POSICION")));
        }

        if (!marcaJarreo(parameters.NVL(BaseAction.WS_PRMT_TRANSACCION), tipo.getTipo(), ven.NVL("ID"))) {
            throw new DetiPOSFault(ERROR, 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ERROR, ERROR));
        }

        return super.getComprobante();
    }//getComprobante
}//SaldoAction
