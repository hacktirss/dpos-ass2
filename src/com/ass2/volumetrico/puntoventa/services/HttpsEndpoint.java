package com.ass2.volumetrico.puntoventa.services;

import com.ass2.volumetrico.puntoventa.data.SysFile;
import com.ass2.volumetrico.puntoventa.data.SysFileDAO;
import com.softcoatl.commons.soap.utils.SOAPLoggingHandler;
import com.softcoatl.context.APPContext;
import com.softcoatl.utils.logging.LogManager;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HttpsEndpoint implements Endpoint {

    private final Properties ctx;
    private final SysFile jks;

    public HttpsEndpoint() {
        ctx = APPContext.getInstance().getProperties();
        jks = SysFileDAO.get(ATTR_KEYSTORE);
    }

    @Override
    public String url() {
        return ATTR_HTTPS_PREFIX + 
                ctx.getProperty(ATTR_HOST) + ":" + ctx.getProperty(ATTR_HTTPS_PORT) + ctx.getProperty(ATTR_ENDPOINT_URI);
    }

    @Override
    public boolean valid() {
        return ctx.contains(ATTR_HOST)
                && ctx.contains(ATTR_HTTPS_PORT)
                && ctx.contains(ATTR_ENDPOINT_URI)
                && jks != null;
    }

    private static HttpContext initSSLContext(Properties ctx, SysFile jks, String url)
            throws IOException, GeneralSecurityException {

        KeyStore store = KeyStore.getInstance("JKS");
        store.load(new ByteArrayInputStream(jks.getFile()), jks.getAdditional().toCharArray());

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(store);

        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(store, jks.getAdditional().toCharArray());

        SSLContext ssl = SSLContext.getInstance("TLSv1.2");
        ssl.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), new SecureRandom());

        HttpsConfigurator configurator = new HttpsConfigurator(ssl);

        HttpsServer httpsServer = HttpsServer.create(
                new InetSocketAddress(
                        ctx.getProperty(ATTR_ENDPOINT_URI), 
                        Integer.parseInt(ctx.getProperty(ATTR_HTTPS_PORT))), 0);
        httpsServer.setHttpsConfigurator(configurator);

        HttpContext httpContext = httpsServer.createContext(url);
        httpsServer.start();

        return httpContext;
    }

    @Override
    public void publish() {
        if (valid()) {
            try {
                LogManager.info("Starting DetiPOS SSL WEB Services at " + url());
                javax.xml.ws.Endpoint https = javax.xml.ws.Endpoint.create(new DetiPOSPortImp());
                https.getBinding().getHandlerChain().add(new SOAPLoggingHandler());
                HttpContext securedContext = initSSLContext(ctx, jks, url());
                https.publish(securedContext);
            } catch (IOException | GeneralSecurityException ex) {
                LogManager.fatal(ex);
            }
        }
    }
    
}
