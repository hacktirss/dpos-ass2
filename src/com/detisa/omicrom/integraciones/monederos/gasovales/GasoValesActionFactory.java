/*
 * CCActionFactory
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.gasovales;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.actions.ActionFactory;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.softcoatl.data.DinamicVO;

public class GasoValesActionFactory extends ActionFactory {

    public enum ACTIONS {
        CONSUMO,
        SALDO
    }//ACTIONS

    @Override
    public DetiPOSAction getPOSAction(Object action, DinamicVO<String, String> parameters) throws DetiPOSFault {
        ACTIONS actions = ACTIONS.valueOf((String) action);
        switch (actions) {
            case CONSUMO:
                return new ConsumoAction(parameters).validateRequest().validatePOS();
            case SALDO:
                return new SaldoAction(parameters).validateRequest().validatePOS();
        }
        return null;
    }

    @Override
    public DetiPOSAction getAnonymousAction(Object action, DinamicVO<String, String> parameters) throws DetiPOSFault {
        return null;
    }
}
