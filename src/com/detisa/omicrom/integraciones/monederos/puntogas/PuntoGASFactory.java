
package com.detisa.omicrom.integraciones.monederos.puntogas;

import com.detisa.integrations.puntogas.PuntoGasWSSoap;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class PuntoGASFactory {
    
    public static final String CARD_NAMESPACE = "http://puntogas.com/";
    public static final String CARD_SERVICENAME = "PuntoGasWS";
    public static final String CARD_PORTNAME = "PuntoGasWSSoap";

    public static final QName CARD_SERVICEQN = new QName(CARD_NAMESPACE, CARD_SERVICENAME);
    public static final QName CARD_PORTQN = new QName(CARD_NAMESPACE, CARD_PORTNAME);

    private PuntoGASFactory() {}

    public static final PuntoGasWSSoap getCardService(String url) throws MalformedURLException {
        return Service.create(new URL(url), CARD_SERVICEQN).getPort(CARD_PORTQN, PuntoGasWSSoap.class);
    }
}
 