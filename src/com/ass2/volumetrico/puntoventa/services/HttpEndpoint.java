/**
 * Endpoint
 * ASS2PuntoVenta® WEB Service
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since Jul 2024
 */
package com.ass2.volumetrico.puntoventa.services;

import com.softcoatl.context.APPContext;
import com.softcoatl.utils.logging.LogManager;
import java.util.Properties;

public class HttpEndpoint implements Endpoint {

    private final Properties ctx;

    public HttpEndpoint() {
        ctx = APPContext.getInstance().getProperties();
    }

    @Override
    public String url() {
        return ATTR_HTTP_PREFIX + 
                ctx.getProperty(ATTR_HOST) + ":" + ctx.getProperty(ATTR_HTTP_PORT) + ctx.getProperty(ATTR_ENDPOINT_URI);
    }

    @Override
    public boolean valid() {
        return ctx.contains(ATTR_HOST) 
                && ctx.contains(ATTR_HTTP_PORT)
                && ctx.contains(ATTR_ENDPOINT_URI);
    }

    @Override
    public void publish() {
        
        if (valid()) {
            LogManager.info("Starting DetiPOS WEB Services at " + url());
            javax.xml.ws.Endpoint endpoint = javax.xml.ws.Endpoint.create(new DetiPOSPortImp());
            endpoint.publish(url());
        }
    }
}
