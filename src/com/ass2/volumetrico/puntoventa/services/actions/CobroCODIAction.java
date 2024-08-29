package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import static com.ass2.volumetrico.puntoventa.services.actions.BaseAction.WS_PRMT_TRANSACCION;
import com.softcoatl.data.DinamicVO;

public class CobroCODIAction extends BaseAction {

    public static final String WS_PRMTR_CODI_TELEFONO = "telefonoAuth";

    public CobroCODIAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TRANSACCION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TRANSACCION"));
        }
        if (parameters.isNVL(WS_PRMTR_CODI_TELEFONO)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TELÉFONO"));
        }
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = super.getComprobante();
        return comprobante;
    }    

}//CobroCODIAction
