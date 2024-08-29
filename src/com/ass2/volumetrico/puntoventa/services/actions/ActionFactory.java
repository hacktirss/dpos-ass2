package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public abstract class ActionFactory {

    public abstract DetiPOSAction getPOSAction(Object action, DinamicVO<String, String> parameters) throws DetiPOSFault;
    public abstract DetiPOSAction getAnonymousAction(Object action, DinamicVO<String, String> parameters) throws DetiPOSFault;
}//DposActionFactory
