
package mx.com.detisa.server;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


@WebServiceClient(name = "OMICROMCorporateWSImplService", targetNamespace = "http://server.detisa.mx.com/", wsdlLocation = "http://169.57.7.20:9191/omicromcorpserv/receiver?wsdl")
public class OMICROMCorporateWSImplService
    extends Service
{

    private final static URL OMICROMCORPORATEWSIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException OMICROMCORPORATEWSIMPLSERVICE_EXCEPTION;
    private final static QName OMICROMCORPORATEWSIMPLSERVICE_QNAME = new QName("http://server.detisa.mx.com/", "OMICROMCorporateWSImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://169.57.7.20:9191/omicromcorpserv/receiver?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        OMICROMCORPORATEWSIMPLSERVICE_WSDL_LOCATION = url;
        OMICROMCORPORATEWSIMPLSERVICE_EXCEPTION = e;
    }

    public OMICROMCorporateWSImplService() {
        super(__getWsdlLocation(), OMICROMCORPORATEWSIMPLSERVICE_QNAME);
    }

    public OMICROMCorporateWSImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), OMICROMCORPORATEWSIMPLSERVICE_QNAME, features);
    }

    public OMICROMCorporateWSImplService(URL wsdlLocation) {
        super(wsdlLocation, OMICROMCORPORATEWSIMPLSERVICE_QNAME);
    }

    public OMICROMCorporateWSImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, OMICROMCORPORATEWSIMPLSERVICE_QNAME, features);
    }

    public OMICROMCorporateWSImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public OMICROMCorporateWSImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "OMICROMCorporateWSImplPort")
    public OMICROMCorporateWSImpl getOMICROMCorporateWSImplPort() {
        return super.getPort(new QName("http://server.detisa.mx.com/", "OMICROMCorporateWSImplPort"), OMICROMCorporateWSImpl.class);
    }

    @WebEndpoint(name = "OMICROMCorporateWSImplPort")
    public OMICROMCorporateWSImpl getOMICROMCorporateWSImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://server.detisa.mx.com/", "OMICROMCorporateWSImplPort"), OMICROMCorporateWSImpl.class, features);
    }

    private static URL __getWsdlLocation() {
        if (OMICROMCORPORATEWSIMPLSERVICE_EXCEPTION!= null) {
            throw OMICROMCORPORATEWSIMPLSERVICE_EXCEPTION;
        }
        return OMICROMCORPORATEWSIMPLSERVICE_WSDL_LOCATION;
    }

}
