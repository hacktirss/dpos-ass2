package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.UpdateDTO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.FileUtils;

/**
 *
 * @author ROLANDO
 */
public class CheckUpdatesAction extends BaseAction {

    public CheckUpdatesAction(DinamicVO<String, String> param) throws DetiPOSFault {
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

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return checkForVersion() ? new Comprobante().append("UPD", "true").append("fname", FileUtils.getFileName(version.NVL("BIN_FILE"))) : new Comprobante().append("UPD", "false");
    }//getComprobante

    @Override
    public Object getResponse() throws DetiPOSFault {
        UpdateDTO update = new UpdateDTO();
        if (checkForVersion()) {
            update.setHasUpdate(true);
            update.setVersion(FileUtils.getFileName(version.NVL("BIN_FILE")));
            update.setMd5(version.NVL("MD5"));
        } else {
            update.setHasUpdate(false);
        }
        return update;
    }//getComprobante
}//GetLogoAction
