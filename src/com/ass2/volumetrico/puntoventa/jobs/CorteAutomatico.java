/*
 * CorteAutomatico
 * ASS2PuntoVenta®
 * ® 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2017
 */
package com.ass2.volumetrico.puntoventa.jobs;

import com.detisa.omicrom.bussiness.Corte;
import com.detisa.omicrom.bussiness.CorteFactory;
import com.detisa.omicrom.bussiness.corte.Common;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.schedule.JobScheduler;
import java.util.Calendar;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CorteAutomatico implements Job {

    public static final String CRON = "jobs.omicrom.corte.cron";
    public static final String THREAD = "CORTE_AUTO";

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            DinamicVO<String, String> ex = Common.getStatusIsla("1");
            LogManager.info(ex);
            
            if ("Si".equals(ex.NVL("ca"))) {
                if (ex.isNVL("id") || !"Abierto".equals(ex.NVL("cst"))) { //If does not exists or it's closed
                    if (CorteFactory.locateExecution(ex.NVL("isla")) == null) {
                        try {
                            LogManager.info("Ejecutando corte automático");
                            CorteFactory.getCorte(ex.NVL("isla"), Corte.CLIENT.AUT, Corte.PROCESS.CLOSEANDOPEN).start();
                        } catch (DBException | DetiPOSFault ex1) {
                            LogManager.error(ex1);
                            LogManager.debug("Trace", ex1);
                        }
                    } else {
                        LogManager.debug("Corte automático en ejecución");
                    }
                    Calendar next = Calendar.getInstance(DateUtils.REGIONAL_MEXICO);
                    next.add(Calendar.MINUTE, 1);
                    reschedulle(
                            DateUtils.fncsFormat(
                                    "HH:mm:ss",
                                    next));
                    return;
                }
            }//is active?
            reschedulle(ex.NVL("next"));
        } //execute
        catch (DetiPOSFault ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }

    private void reschedulle(String time) {
        LogManager.info("Schedulling next execution at " + time);
        JobScheduler.getInstance().remove(THREAD);
        JobScheduler.getInstance().schedule(
                CorteAutomatico.class, 
                "CORTE_AUTO", 
                DateUtils.fncsFormat(
                        "s m H * * ?", 
                        DateUtils.fncoAdd(DateUtils.fncoCalendar("HH:mm:ss", time, DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO, true), Calendar.SECOND, 1), 
                        DateUtils.REGIONAL_MEXICO, 
                        DateUtils.TIMEZONE_MEXICO));
    }//reschedulle
}//CorteAutomatico
