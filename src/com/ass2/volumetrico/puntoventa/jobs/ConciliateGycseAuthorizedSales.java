/*
 * ConciliateGycseAuthorizedSales
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2016
 */
package com.ass2.volumetrico.puntoventa.jobs;

import com.ass2.volumetrico.puntoventa.data.GycseDAO;
import com.ass2.volumetrico.puntoventa.data.GycseOperacion;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.detisa.omicrom.gycse.GycseFault;
import com.detisa.omicrom.gycse.GycseRepositorioWS;
import com.detisa.omicrom.gycse.RMIClientFactory;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.context.APPContext;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import java.net.MalformedURLException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ConciliateGycseAuthorizedSales implements Job {

    public static final String CRON = "jobs.gycse.conciliate.cron";

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        GycseRepositorioWS repositorioWS;
        String status;
        try {
            if ("1".equals(VariablesDAO.getCorporativo("gycse_enabled", "0"))) {
                repositorioWS = RMIClientFactory.getGycsePort(APPContext.getInitParameter(RMIClientFactory.GYCSE_ENDPOINT), 0);
                for (DinamicVO<String, String> operacion : GycseDAO.getOperacionesSinConciliar()) {
                    try {
                        status = repositorioWS.consultaOperacion(operacion.NVL("peticion"));
                        GycseDAO.updateStatusOperacion(operacion.NVL(GycseOperacion.FIELDS.id.name()), status);
                    } catch (GycseFault | DetiPOSFault ex) {
                        LogManager.error(ex);
                        LogManager.debug("Trace", ex);
                    }
                }
            }
        } catch (DBException | MalformedURLException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }
}//EnviaOperaciones
