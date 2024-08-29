/**
 * StandAloneWSDetiPOS
 * DetiPOS® WEB Service
 * DetiPOS® Omicrom Services
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
package com.ass2.volumetrico.puntoventa.services;

import com.detisa.commons.ServiceRegister;
import com.ass2.volumetrico.puntoventa.context.DetiPOSContext;
import com.ass2.volumetrico.puntoventa.context.DetiPOSContext.ContextFactory;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.jobs.AccesiblePOS;
import com.ass2.volumetrico.puntoventa.jobs.CorteAutomatico;
import com.ass2.volumetrico.puntoventa.jobs.LoggerMonitor;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.context.APPContext;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.schedule.JobScheduler;
import com.softcoatl.utils.PropertyLoader;
import com.softcoatl.utils.ReflectUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import javax.naming.NamingException;

public class StandAloneWSDetiPOS {

    public static final String ENDPOINT = "ws.endpoint";
    public static final String MODULE = "Dpos";
    public static final String VERSION = "1.17.3.8";

    public static void main(String[] args) {

        String url;

        try {
            LogManager.info("Executing " + ReflectUtils.getJar(StandAloneWSDetiPOS.class).getName());
            DetiPOSContext context = ContextFactory.getContext();
            
            // Init Database Pool
            context.configure(PropertyLoader.load("ass2.properties"));
            context.initDataBaseService("DATASOURCE");
            context.configure(VariablesDAO.getSystemDefinition());

            if (!context.getProperties().containsKey("ws.endpoint")) {
                throw new DetiPOSFault("ERROR: endpoint not defined!");
            }
            context.initMailerService();
            context.initGasngoService();

            System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("java.net.useSystemProxies", "false");
            System.setProperty("com.sun.xml.internal.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");
            System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");

            ServiceRegister.registerWSVersion(StandAloneWSDetiPOS.class, MODULE, VERSION);

            new HttpEndpoint().publish();
            new HttpsEndpoint().publish();

            LogManager.info("DetiPOS WEB Services started succesfully!");

            JobScheduler.getInstance().initScheduler();
            JobScheduler.getInstance().schedule(
                    LoggerMonitor.class, 
                    "ALIVE", 
                    APPContext.getInitParameter(LoggerMonitor.CRON));
            JobScheduler.getInstance().schedule(
                    AccesiblePOS.class, 
                    "CHECK_POS_STATUS", 
                    APPContext.getInitParameter(AccesiblePOS.CRON));
            JobScheduler.getInstance().schedule(
                    CorteAutomatico.class, 
                    "CORTE_AUTO", 
                    DateUtils.fncsFormat("s m H * * ?", DateUtils.fncoAdd(Calendar.getInstance(), Calendar.SECOND, 10)));
        } catch (DetiPOSFault | ReflectiveOperationException | NamingException | URISyntaxException | IOException | GeneralSecurityException ex) {
            LogManager.getRootLogger().fatal("FATAL", ex);
        }
    }
}