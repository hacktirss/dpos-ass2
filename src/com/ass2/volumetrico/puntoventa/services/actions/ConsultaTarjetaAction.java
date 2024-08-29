package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.data.UnidadVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;

public class ConsultaTarjetaAction extends BaseAction {

    public static enum UNIDAD_FIELDS {

        ID,
        DESCRIPCION,
        CLIENTE,
        PLACAS,
        CODIGO,
        IMPRESO,
        COMBUSTIBLE,
        LITROS,
        IMPORTE,
        PERIODO,
        SIMULTANEO,
        INTERES,
        ESTADO,
        HORAI,
        HORAF,
        ALLOWED,
        NIP,
        PIDENIP,
        CORPORATIVO
    }

    public ConsultaTarjetaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private DinamicVO<String, String> getUnidad(String tarjeta) throws DetiPOSFault {
        return UnidadesDAO.getUnidadV01(tarjeta);
    }
    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TARJETA)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TARJETA"));
        }//if
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DinamicVO<String, String> unidad;
        String tarjeta = parameters.NVL(WS_PRMT_TARJETA);
        unidad = getUnidad(tarjeta);
        if (unidad.isVoid() || unidad.isNVL(UnidadVO.UND_FIELDS.id.name())) {
            throw new DetiPOSFault("No existe la Unidad", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error consultando tarjeta", "La Unidad no existe " + tarjeta));
        }//if does not exist
        return new Comprobante(unidad);
    }//getComprobante
}//ConsultaTarjetaAction
