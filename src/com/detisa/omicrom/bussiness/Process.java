package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;

/**
 *
 * @author ROLANDO
 */
public interface Process {
    public boolean execute() throws DetiPOSFault;
}
