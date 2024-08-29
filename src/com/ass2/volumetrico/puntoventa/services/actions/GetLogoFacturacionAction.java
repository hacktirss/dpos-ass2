package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author ROLANDO
 */
public class GetLogoFacturacionAction extends BaseAction {

    public GetLogoFacturacionAction(DinamicVO<String, String> param) throws DetiPOSFault {
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

    private Comprobante getEncodedLogo() throws DetiPOSFault {
        Comprobante configuration = new Comprobante();
        try {
            configuration.append("logo.png", new String(Base64.encodeBase64(com.detisa.dao.CertificadosDAO.getBlobToByteArray("tk_img"))));
        } catch (IOException DBE) {
            throw new DetiPOSFault("Error recuperando logo", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, DBE, "Error recuperando logo"));
        }
        return configuration;
    }
    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return getEncodedLogo();
    }//getComprobante
}//GetLogoAction
