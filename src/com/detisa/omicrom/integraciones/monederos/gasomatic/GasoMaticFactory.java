
package com.detisa.omicrom.integraciones.monederos.gasomatic;

import com.mx.detisa.integrations.gasomatic.WsSoap;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class GasoMaticFactory {
    
    public static final String GM_NAMESPACE = "https://www.gasomatic.com.mx/wsCatLodemoV2/";
    public static final String GM_SERVICENAME = "ws";
    public static final String GM_PORTNAME = "wsSoap";

    public static final QName GM_SERVICEQN = new QName(GM_NAMESPACE, GM_SERVICENAME);
    public static final QName GM_PORTQN = new QName(GM_NAMESPACE, GM_PORTNAME);

    private GasoMaticFactory() {}

    public static final WsSoap getCardService(String url) throws MalformedURLException {
        return Service.create(new URL(url), GM_SERVICEQN).getPort(GM_PORTQN, WsSoap.class);
    }
}
 