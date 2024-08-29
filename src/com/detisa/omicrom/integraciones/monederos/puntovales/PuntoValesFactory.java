
package com.detisa.omicrom.integraciones.monederos.puntovales;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mx.detisa.integrations.gasovales.ObjectFactory;
import com.mx.detisa.integrations.gasovales.V1Soap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class PuntoValesFactory {
    
    public static final String CARD_NAMESPACE = "http://tempuri.org/";
    public static final String CARD_SERVICENAME = "v1";
    public static final String CARD_PORTNAME = "v1Soap";

    public static final QName CARD_SERVICEQN = new QName(CARD_NAMESPACE, CARD_SERVICENAME);
    public static final QName CARD_PORTQN = new QName(CARD_NAMESPACE, CARD_PORTNAME);

    private PuntoValesFactory() {}

    public static final V1Soap getCardService(String url) throws MalformedURLException {
        return Service.create(new URL(url), CARD_SERVICEQN).getPort(CARD_PORTQN, V1Soap.class);
    }
    
    public static void main(String[] args) throws MalformedURLException, UnknownHostException, JsonProcessingException {

        V1Soap service = PuntoValesFactory.getCardService("http://sigma.gruges.com.mx/ges.central.services.soap/v1.asmx?WSDL");
        ObjectFactory of = new ObjectFactory();
    }
}
 