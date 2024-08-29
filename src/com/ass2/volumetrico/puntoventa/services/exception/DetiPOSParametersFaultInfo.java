/**
 * DetiPOSParametersFaultInfo
 * Custom exception for DetiPOS WEBServices
 *
 * @author Rolando Esquivel VillafaÃ±a
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.exception;

public class DetiPOSParametersFaultInfo extends DetiPOSFaultInfo {
    
    public static final String ERROR = "Error de Parametros";

    public DetiPOSParametersFaultInfo() {
        super();
    }

    public DetiPOSParametersFaultInfo(int code, String message, String detail) {
        super(code, message, detail);
    }

    public DetiPOSParametersFaultInfo(String message, String detail) {
        super(PRM_ERROR, message, detail);
    }

    public DetiPOSParametersFaultInfo(String detail) {
        super(PRM_ERROR, ERROR, detail);
    }

    public DetiPOSParametersFaultInfo(int code, Throwable cause, String detail) {
        super(code, cause, detail);
    }

    public DetiPOSParametersFaultInfo(Throwable cause, String detail) {
        super(PRM_ERROR, cause, detail);
    }
}
