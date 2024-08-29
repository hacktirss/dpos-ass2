package com.detisa.fae;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "DataServer", targetNamespace = "http://detisa.com/fae/", wsdlLocation = "http://172.31.99.229:9095/corporate/DataServer?wsdl")
public class DataServer_Service
    extends Service
{

    private final static URL DATASERVER_WSDL_LOCATION;
    private final static WebServiceException DATASERVER_EXCEPTION;
    private final static QName DATASERVER_QNAME = new QName("http://detisa.com/fae/", "DataServer");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://172.31.99.229:9095/corporate/DataServer?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        DATASERVER_WSDL_LOCATION = url;
        DATASERVER_EXCEPTION = e;
    }

    public DataServer_Service() {
        super(__getWsdlLocation(), DATASERVER_QNAME);
    }

    public DataServer_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), DATASERVER_QNAME, features);
    }

    public DataServer_Service(URL wsdlLocation) {
        super(wsdlLocation, DATASERVER_QNAME);
    }

    public DataServer_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, DATASERVER_QNAME, features);
    }

    public DataServer_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DataServer_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "DataServer")
    public DataServer getDataServer() {
        return super.getPort(new QName("http://detisa.com/fae/", "DataServer"), DataServer.class);
    }

    @WebEndpoint(name = "DataServer")
    public DataServer getDataServer(WebServiceFeature... features) {
        return super.getPort(new QName("http://detisa.com/fae/", "DataServer"), DataServer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (DATASERVER_EXCEPTION!= null) {
            throw DATASERVER_EXCEPTION;
        }
        return DATASERVER_WSDL_LOCATION;
    }

}
