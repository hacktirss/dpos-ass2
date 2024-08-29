/**
 * DetiPOSDataBaseFaultInfo
 * Custom exception for DetiPOS WEBServices
 *
 * @author Rolando Esquivel VillafaÃ±a
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.exception;

public class DetiPOSDataBaseFaultInfo extends DetiPOSFaultInfo {
    
    public static final String ERROR = "Error de Base de Datos";

    public DetiPOSDataBaseFaultInfo() {
        super();
    }

    public DetiPOSDataBaseFaultInfo(int code, String message, String detail) {
        super(code, message, detail);
    }

    public DetiPOSDataBaseFaultInfo(String message, String detail) {
        super(DBA_ERROR, message, detail);
    }

    public DetiPOSDataBaseFaultInfo(String detail) {
        super(DBA_ERROR, ERROR, detail);
    }

    public DetiPOSDataBaseFaultInfo(int code, Throwable cause, String detail) {
        super(code, cause, detail);
    }

    public DetiPOSDataBaseFaultInfo(Throwable cause, String detail) {
        super(DBA_ERROR, cause, detail);
    }
}
