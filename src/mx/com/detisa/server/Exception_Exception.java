
package mx.com.detisa.server;

import javax.xml.ws.WebFault;


@WebFault(name = "Exception", targetNamespace = "http://server.detisa.mx.com/")
public class Exception_Exception
    extends java.lang.Exception
{

    private mx.com.detisa.server.Exception faultInfo;

    public Exception_Exception(String message, mx.com.detisa.server.Exception faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public Exception_Exception(String message, mx.com.detisa.server.Exception faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public mx.com.detisa.server.Exception getFaultInfo() {
        return faultInfo;
    }

}
