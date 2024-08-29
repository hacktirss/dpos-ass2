package com.ass2.volumetrico.puntoventa.context;

import com.ass2.volumetrico.puntoventa.data.SmtpDAO;
import com.ass2.volumetrico.puntoventa.data.SmtpVO;
import com.detisa.integrations.gasngo.service.GasngoServer;
import com.detisa.integrations.gasngo.service.GasngoService;
import com.detisa.integrations.gasngo.service.GasngoServiceFactory;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.context.APPContext;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.integration.ServiceLoader;
import com.softcoatl.integration.ServiceLocator;
import com.softcoatl.messaging.mail.Mailbox;
import com.softcoatl.messaging.mail.Session;
import java.io.IOException;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.naming.NamingException;

public class DetiPOSContext {

    public interface ContextFactory {
        public static DetiPOSContext getContext() {
            return new DetiPOSContext();
        }
    }

    public static final String DS_FACTORY = "service.database.datasource";

    private DetiPOSContext() { 
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LogManager.info("Received SIGTERM signal. Terminating process...");
            }
        });
    }

    public void configure(Properties poSystemProperties) {
        APPContext.getInstance().getProperties().putAll(poSystemProperties);
    }

    public Properties getProperties() {
        return APPContext.getInstance().getProperties();
    }

    private void setBDSession() {
        try {
            MySQLHelper.getInstance().forceExecute("USE omicrom");
            MySQLHelper.getInstance().forceExecute("SET lc_time_names = 'es_MX'");
            LogManager.info("Configurado BD OMICROM v1.0");
        } catch (DBException dbexc) {
            LogManager.fatal("Error iniciando contexto en MySQL", dbexc);
        }
    }

    public void initDataBaseService(String psDataBaseName) throws ReflectiveOperationException, NamingException {
        LogManager.info("Loading Datasource Service as " + APPContext.getInitParameter(DS_FACTORY));
        ServiceLoader.loadService(APPContext.getInitParameter(DS_FACTORY), psDataBaseName, psDataBaseName);
        setBDSession();
    }

    public void initGasngoService() throws ReflectiveOperationException, NamingException, IOException {
        LogManager.info("Loading Gasngo Service");
        if ("1".equals(VariablesDAO.getCorporativo("gasngo_active", "0"))) {
            APPContext.setInitParameter("gasngo." + GasngoServer.NAME + ".factory", GasngoServiceFactory.class.getCanonicalName());
            ServiceLoader.loadService(GasngoService.class, GasngoServer.NAME, GasngoServer.NAME);
            LogManager.info(ServiceLocator.getInstance().getService(GasngoServer.NAME));
        }
    }
    
    public void initMailerService() {
        Properties voProp = new Properties();
        final SmtpVO smtp = SmtpDAO.getActiveMail();
        voProp.setProperty(Mailbox.MAILBOX_HST, smtp.NVL(SmtpVO.FIELDS.smtpname.name()));
        voProp.setProperty(Mailbox.MAILBOX_USR, smtp.NVL(SmtpVO.FIELDS.smtploginuser.name()));
        voProp.setProperty(Mailbox.MAILBOX_PWD, smtp.NVL(SmtpVO.FIELDS.smtploginpass.name()));
        voProp.setProperty(Mailbox.MAILBOX_PTCL, Mailbox.MAILBOX_PROVIDER_SMTP);
        Session.getInstance().config(voProp, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtp.NVL(SmtpVO.FIELDS.smtploginuser.name()), smtp.NVL(SmtpVO.FIELDS.smtploginpass.name()));
            }
        });
    }
}
