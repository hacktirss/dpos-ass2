/**
 * DetiPOSFaultInfo
 * Custom exception for DetiPOS WEBServices
 *
 * @author Rolando Esquivel VillafaÃ±a
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.exception;

import com.ass2.volumetrico.puntoventa.services.DetiPOSPort;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "errorCode",
    "errorMessage",
    "errorDetail"
})
@XmlRootElement(name = "DetiPOSFault", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Getter
@Setter
public class DetiPOSFaultInfo {

    public static final Integer PRM_ERROR = 1;
    public static final Integer INT_ERROR = 2;
    public static final Integer DBA_ERROR = 3;

    @XmlElement(name = "ErrorCode", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected Integer errorCode;
    @XmlElement(name = "ErrorMessage", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String errorMessage;
    @XmlElement(name = "ErrorDetail", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String errorDetail;

    public DetiPOSFaultInfo() {
    }

    public DetiPOSFaultInfo(int code, String message, String detail) {
        super();
        errorCode = code;
        errorMessage = message;
        errorDetail = detail;
    }

    public DetiPOSFaultInfo(int code, Throwable cause, String detail) {
        Throwable voCause = cause;

        while (null != voCause.getCause()) {
            voCause = voCause.getCause();
        }
        errorCode = code;
        errorMessage = voCause.getMessage();
        errorDetail = detail;
    }

    @Override
    public String toString() {
        return "err("+errorCode+"):"+errorMessage+" "+errorDetail;
    }
}
