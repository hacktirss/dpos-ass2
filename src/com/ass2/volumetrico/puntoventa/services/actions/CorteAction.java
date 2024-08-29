package com.ass2.volumetrico.puntoventa.services.actions;

import com.detisa.omicrom.bussiness.Corte;
import com.detisa.omicrom.bussiness.CorteFactory;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.IslaDAO;
import com.ass2.volumetrico.puntoventa.data.IslaVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import static com.ass2.volumetrico.puntoventa.services.actions.BaseAction.WS_PRMT_POSICION;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;

public class CorteAction extends ImprimeCorteAction {

    public static final String WS_PRMT_PROCESS = "proceso";

    public CorteAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {

        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_POSICION) || "0".equals(parameters.NVL(WS_PRMT_POSICION))) {
            IslaVO defaultIsla = (IslaVO) IslaDAO.getDefaultIsla();
            if (defaultIsla.isVoid()) {
                throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro ISLA"));
            }
            parameters.setField(WS_PRMT_POSICION, defaultIsla.NVL("isla"));
        }
        if (!parameters.isNVL(WS_PRMT_TERMINAL)) {
            if (parameters.isNVL(WS_PRMT_AUTH)) {
                throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro PASSWORD"));
            }
            LogManager.debug("Validando el password "+parameters.NVL(WS_PRMT_AUTH));
            if (!validatePassword(parameters.NVL(WS_PRMT_AUTH))) {
                throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error validando el PASSWORD"));
            }
        }
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {

        Comprobante comprobante = new Comprobante();
        Corte corte = null;

        try {
            if (null == (corte = CorteFactory.locateExecution(parameters.NVL("posicion")))) {
                corte = CorteFactory.getCorte(parameters.NVL("posicion"), 
                    parameters.isNVL("idTerminal") ? Corte.CLIENT.OMI : Corte.CLIENT.POS, 
                    parameters.isNVL("proceso") ? Corte.PROCESS.CLOSEANDOPEN : Corte.PROCESS.valueOf(parameters.NVL("proceso").toUpperCase()));
                corte.start();
            }

            comprobante.append("STATUS", corte.getRunningStatus());
            LogManager.info("Corte " + corte.getRunningStatus());
        } catch (DBException ex) {
            throw new DetiPOSFault("Error iniciando corte", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error iniciando corte", "Isla 1"), ex);
        } finally {
            if (null != corte && corte.hasFinished()) {

                if (corte.isError()) {
                   comprobante.append("DONE", "ERROR").append("ERROR_DETAIL", corte.getErrorDetail());
                } else {
                    
                    comprobante = super.getComprobante().append("DONE", "SUCCESS");
                }
                corte.stopCorte(); // Force stopping
            }
        }

        return comprobante;
    }
}
