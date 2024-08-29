package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;

/**
 *
 * @author ROLANDO
 */
public interface ProcessExecutor {
    public void registerProcess(Process process);
    public boolean execute() throws DetiPOSFault;
}//ProcessExecutor
