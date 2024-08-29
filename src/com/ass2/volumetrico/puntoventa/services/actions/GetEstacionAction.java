package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;

/**
 *
 * @author ROLANDO
 */
public class GetEstacionAction extends BaseAction {

    public GetEstacionAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }
    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TERMINAL)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TERMINAL"));
        }
        return this;
    }//validateRequest
}//GetLogoAction
