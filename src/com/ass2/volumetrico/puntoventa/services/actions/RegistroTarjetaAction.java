package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BitacoraDAO;
import com.softcoatl.database.mysql.MySQLDB;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.data.UnidadesVO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;

public class RegistroTarjetaAction extends BaseAction {

    public static final String WS_TARJETA_ID = "tarjeta.id";
    public static final String WS_TARJETA_IMPRESO = "tarjeta.impreso";

    public RegistroTarjetaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_TARJETA_ID)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro ID TARJETA"));
        }//if
        if (parameters.isNVL(WS_TARJETA_IMPRESO)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro IMPRESO"));
        }//if
        return this;
    }//validateRequest

    private Comprobante insertTarjeta() {
        try {
            UnidadesVO unidad = UnidadesVO.getInstance();
            BaseVO consulta = UnidadesDAO.getUnidad(parameters.NVL(WS_TARJETA_ID));
            if (!consulta.isVoid()) {
                return new Comprobante().append("E", consulta.NVL("id"));
            }
            unidad.setField(UnidadesVO.FIELDS.codigo, parameters.NVL(WS_TARJETA_ID));
            unidad.setField(UnidadesVO.FIELDS.impreso, parameters.NVL(WS_TARJETA_IMPRESO));
            BitacoraDAO.evento(String.format("Registro de Tarjetas. Código [%s] Impreso [%s]", parameters.NVL(WS_TARJETA_ID), parameters.NVL(WS_TARJETA_IMPRESO)), "POS", this.terminal.NVL("IP"));
            return new Comprobante().append("N", MySQLDB.insertInAutoKeyedEntity(unidad));
        } catch (DetiPOSFault |DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            return new Comprobante().append("E", ex.getMessage());
        }
    }//insertTarjeta

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return insertTarjeta();
    }//getComprobante
}//RegistroTarjetaAction
