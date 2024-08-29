package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BoletosDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;

/**
 *
 * @author ROLANDO
 */
public class ConsultaBoletoAction extends BaseAction {

    public ConsultaBoletoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private DinamicVO<String, String> getBoleto(String boleto) throws DetiPOSFault {
        return BoletosDAO.getBoletoByID(boleto);
    }//getCliente

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_BOLETO)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro BOLETO"));
        }//if
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        String boleto = parameters.NVL(WS_PRMT_BOLETO);
        return new Comprobante(getBoleto(boleto), "BOL_");
    }//getComprobante
}//ConsultaBoletoAction
