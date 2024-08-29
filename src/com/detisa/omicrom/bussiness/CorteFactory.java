/*
 * CorteFactory
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2020
 */
package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import java.lang.reflect.Constructor;

public abstract class CorteFactory {

    public static Corte getCorte(String isla, Corte.CLIENT client, Corte.PROCESS process) throws DBException, DetiPOSFault {
        String corteClass = VariablesDAO.getCorporativo("pos_version_corte", "com.detisa.omicrom.bussiness.CorteV2");
        try {
            LogManager.info("Creando Proceso de Corte Versión " + corteClass);
            Class<?> clazz = Class.forName(corteClass);
            Constructor<?> constructor = clazz.getConstructor(String.class, Corte.CLIENT.class, Corte.PROCESS.class);
            return (Corte) constructor.newInstance(isla, client, process);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException ex) {
            OmicromLogManager.error("Error creando proceso de corte", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    public static Corte locateExecution(String isla) {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        String threadName = "CORTE_" + isla;
        while (null != tg.getParent()) {
            tg = tg.getParent();
        }
        int activeCount = tg.activeCount() + 5;
        Thread[] t = new Thread[activeCount];
        int actualThreads = tg.enumerate(t);
        for (int i = 0; i < actualThreads; i++) {
            if (threadName.equals(t[i].getName())) {
                LogManager.info("Devolviento ejecución previa " + t[i].getName());
                return (Corte) t[i];
            }
        }
        return null;
    }
}