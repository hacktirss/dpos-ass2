package com.detisa.fae;

import javax.xml.ws.WebFault;

@WebFault(name = "Exception", targetNamespace = "http://detisa.com/fae/")
public class Exception_Exception extends java.lang.Exception {

    private com.detisa.fae.Exception faultInfo;

    public Exception_Exception(String message, com.detisa.fae.Exception faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public Exception_Exception(String message, com.detisa.fae.Exception faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public com.detisa.fae.Exception getFaultInfo() {
        return faultInfo;
    }

}
