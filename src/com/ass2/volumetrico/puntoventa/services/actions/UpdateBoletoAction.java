package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BoletosDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;

public class UpdateBoletoAction extends BaseAction {

    public static final String BLT_PRMTR_CARGO = "cargo";
    public static final String BLT_PRMTR_TICKET_1 = "ticket1";
    public static final String BLT_PRMTR_TICKET_2 = "ticket2";
    public static final String BLT_PRMTR_ACTIVO = "activo";

    public UpdateBoletoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor
    
    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL("boleto")) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro BOLETO"));
        }//if
        if (parameters.isNVL("cargo")) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro CARGO"));
        }//if
        if (parameters.isNVL("activo")) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro ACTIVO"));
        }//if
        return this;
    }//validateRequest

    private boolean update () {
        return BoletosDAO.updateBoleto(parameters.NVL("boleto"),
                    StringUtils.fncsFormat("#.00", Double.valueOf(parameters.NVL("cargo"))),
                    !parameters.isNVL("ticket1")?parameters.NVL("ticket1"):"",
                    !parameters.isNVL("ticket1")?parameters.NVL("ticket2"):"",
                    parameters.NVL("activo"));
    }//update

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return new Comprobante().append("UPDATED", update() ? "S" : "N");
    }//getComprobante
}//UpdateBoletoAction
