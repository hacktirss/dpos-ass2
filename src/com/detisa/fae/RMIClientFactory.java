/*
 * RMIClientFactory
 * ASS2PuntoVenta®
 * © 2018, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since abr 2018
 */
package com.detisa.fae;

import com.sun.xml.ws.client.BindingProviderProperties;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;

/**
 *
 * @author Rolando Esquivel
 */
public class RMIClientFactory {
    
    public static final String DATASERVER_NAMESPACE = "http://detisa.com/fae/";

    public static DataServer getDataServerPort(String endpoint, String service, String port, int timeout) throws MalformedURLException {

        QName DATASERVER_SERVICE_QNAME = new QName(DATASERVER_NAMESPACE, service);
        QName DATASERVER_PORT_QNAME = new QName(DATASERVER_NAMESPACE, port);
        DataServer dataServerPort = Service.create(new URL(endpoint), DATASERVER_SERVICE_QNAME).getPort(DATASERVER_PORT_QNAME, DataServer.class, new AddressingFeature(true));
        ((BindingProvider) dataServerPort).getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, timeout);
        ((BindingProvider) dataServerPort).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, timeout);
        ((BindingProvider) dataServerPort).getRequestContext().put("javax.xml.ws.client.connectionTimeout", timeout); 
        ((BindingProvider) dataServerPort).getRequestContext().put("javax.xml.ws.client.receiveTimeout", timeout);
        ((BindingProvider) dataServerPort).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", timeout); 
        ((BindingProvider) dataServerPort).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeout);
        ((BindingProvider) dataServerPort).getRequestContext().put("timeout", timeout);
        return dataServerPort;
    }//getGysecPort
}
