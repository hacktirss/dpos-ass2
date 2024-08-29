/*
 * AccesiblePOS
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2016
 */
package com.ass2.volumetrico.puntoventa.jobs;

import com.ass2.volumetrico.puntoventa.data.TerminalesDAO;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AccesiblePOS implements Job {

    public static final String CRON = "jobs.pos.checkstatus.cron";

    private List <String> getActivePOS() {

        try {
            return TerminalesDAO.getActives()
                    .stream().filter(vo -> !vo.isNVL("ip"))
                    .map(vo -> vo.NVL("ip")).collect(Collectors.toList());
        } catch (DBException ex) {
            LogManager.error(ex);
        }
        return new ArrayList <> ();
    }

    public boolean isAccesible(String ip) throws IOException {
        InetAddress inet;

        inet = InetAddress.getByName(ip);
        LogManager.info("Enviando ping a " + inet + ":" + (inet.isReachable(5000) ? " La terminal responde" : " Terminal inaccesible"));
        
        return true;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        getActivePOS().forEach((String ip) -> {
            try {
                isAccesible(ip);
            } catch (IOException ex) {
                LogManager.error(ex);
            }
        });
    }
}
