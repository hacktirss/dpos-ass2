/**
 * DetiPOSFault
 * Custom exception for DetiPOS WEBServices
 *
 * @author Rolando Esquivel VillafaÃ±a
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.exception;

import com.ass2.volumetrico.puntoventa.services.DetiPOSPort;
import javax.xml.ws.WebFault;
import lombok.Getter;

@WebFault(name = "DetiPOSFault", targetNamespace = DetiPOSPort.DPOS_WS_NAMESPACE)
public class DetiPOSFault extends Exception {

    protected static final long serialVersionUID = 1L;
    public static final String GENERIC = "ERROR";

    @Getter private DetiPOSFaultInfo faultInfo;

    public DetiPOSFault() {
    }

    public DetiPOSFault(String message, DetiPOSFaultInfo info) {
        super(message);
        faultInfo = info;
    }

    public DetiPOSFault(DetiPOSFaultInfo info) {
        this(GENERIC, info);
    }

    public DetiPOSFault(String message, DetiPOSFaultInfo info, Throwable cause) {
        super(message, cause);
        faultInfo = info;
    }

    public DetiPOSFault(DetiPOSFaultInfo info, Throwable cause) {
        this(GENERIC, info, cause);
    }

    public DetiPOSFault(String message) {
        super(message);
    }
}
