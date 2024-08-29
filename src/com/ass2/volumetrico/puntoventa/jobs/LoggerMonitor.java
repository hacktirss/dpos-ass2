/*
 * LoggerMonitor
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2016
 */
package com.ass2.volumetrico.puntoventa.jobs;

import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.utils.DateUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LoggerMonitor implements Job {

    private final Logger LOG = LogManager.getRootLogger();
    public static final String CRON = "jobs.pos.alive.cron";

    private void setLoggerLevel() {
        Level confLevel = Level.toLevel(VariablesDAO.getCorporativo("dpos_log_level", "INFO").toUpperCase());
        LogManager.setLevel(confLevel);
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        LOG.info("Proceso en ejecución " + DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
        LOG.debug("Proceso en ejecución y debug " + DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
        setLoggerLevel();
    }
}
