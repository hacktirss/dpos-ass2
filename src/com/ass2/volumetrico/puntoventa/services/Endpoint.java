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

public interface Endpoint {

    public static String ATTR_KEYSTORE     = "ws.keystore";
    public static String ATTR_HOST         = "ws.hostname";
    public static String ATTR_HTTP_PORT    = "ws.http.port";
    public static String ATTR_ENDPOINT_URI = "ws.endpoint";
    public static String ATTR_HTTPS_PORT   = "ws.https.port";
    public static String ATTR_HTTP_PREFIX  = "http://";
    public static String ATTR_HTTPS_PREFIX = "https://";

    public String url();
    public boolean valid();
    public void publish();
}
