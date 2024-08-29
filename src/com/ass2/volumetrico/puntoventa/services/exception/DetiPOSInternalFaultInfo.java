/**
 * DetiPOSInternalFaultInfo
 * Custom exception for DetiPOS WEBServices
 *
 * @author Rolando Esquivel VillafaÃ±a
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.exception;

public class DetiPOSInternalFaultInfo extends DetiPOSFaultInfo {
    
    public DetiPOSInternalFaultInfo() {
        super();
    }

    public DetiPOSInternalFaultInfo(int code, String message, String detail) {
        super(code, message, detail);
    }

    public DetiPOSInternalFaultInfo(String message, String detail) {
        super(INT_ERROR, message, detail);
    }

    public DetiPOSInternalFaultInfo(int code, Throwable cause, String detail) {
        super(code, cause, detail);
    }

    public DetiPOSInternalFaultInfo(Throwable cause, String detail) {
        super(INT_ERROR, cause, detail);
    }
}
