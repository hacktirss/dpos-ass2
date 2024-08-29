/*
 * Common
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2017
 */
package com.detisa.omicrom.bussiness.corte;

import com.ass2.volumetrico.puntoventa.common.OmicromVO;
import com.ass2.volumetrico.puntoventa.data.TurnosDAO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import java.util.Calendar;
import java.util.TimeZone;
import lombok.Data;

public class Common {

    @Data
    private static class Periodo {
        private Calendar fechaInicial;
        private Calendar fechaFinal;

        private void dump() {
            LogManager.info("Periodo definido de " 
                        + DateUtils.fncsFormat("yyyy-MM-dd HH:mm:ss", fechaInicial, DateUtils.REGIONAL_MEXICO, TimeZone.getDefault()) + " a " 
                        + DateUtils.fncsFormat("yyyy-MM-dd HH:mm:ss", fechaFinal, DateUtils.REGIONAL_MEXICO, TimeZone.getDefault()));

        }

        public Periodo(Calendar fechaInicial, Calendar fechaFinal) {
            this.fechaInicial = fechaInicial;
            this.fechaFinal = fechaFinal;
            dump();
        }
    }

    private static Periodo normalizaPeriodo(Calendar fechaInicial, Calendar fechaFinal, Calendar fechaMuestra) {

        Calendar fechaInicialNormalizada = (Calendar) fechaInicial.clone();
        Calendar fechaFinalNormalizada = (Calendar) fechaFinal.clone();

        if (fechaFinal.compareTo(fechaInicial)<=0) {

            if (fechaMuestra.compareTo(fechaFinal)<=0) {
                fechaInicialNormalizada.add(Calendar.DAY_OF_YEAR, -1);
            }

            if (fechaMuestra.compareTo(fechaInicial)>=0) {
                fechaFinalNormalizada.add(Calendar.DAY_OF_YEAR, +1);
            }
        }

        return new Periodo(fechaInicialNormalizada, fechaFinalNormalizada);
    }

    public static DinamicVO<String, String> _getStatusIsla(String isla) throws DetiPOSFault {
        DinamicVO<String, String> status = new OmicromVO();

        Calendar fechaInicial;
        Calendar fechaFinal;

        DateUtils.registerDateFormat("yyyy/MM/dd HH:mm:ss");

        DinamicVO<String, String> currentTurno = TurnosDAO.getCurrentTurno("1");

        String sFechai = DateUtils.fncsFormat("yyyy/MM/dd ") + currentTurno.NVL("horai");
        String sFechaf = DateUtils.fncsFormat("yyyy/MM/dd ") + currentTurno.NVL("horaf");

        LogManager.info("sFechai = " + sFechai);
        LogManager.info("sFechaf = " + sFechaf);

        fechaInicial = DateUtils.fncoCalendar("yyyy/MM/dd HH:mm:ss", sFechai, DateUtils.REGIONAL_MEXICO, TimeZone.getDefault(), true);
        fechaFinal   = DateUtils.fncoCalendar("yyyy/MM/dd HH:mm:ss", sFechaf, DateUtils.REGIONAL_MEXICO, TimeZone.getDefault(), true);

        LogManager.info(fechaInicial);
        LogManager.info(fechaFinal);

        Periodo periodo = normalizaPeriodo(fechaInicial, fechaFinal, Calendar.getInstance(TimeZone.getDefault()));

        String sInicial = DateUtils.fncsFormat("yyyy-MM-dd HH:mm:ss", periodo.getFechaInicial(), DateUtils.REGIONAL_MEXICO, TimeZone.getDefault());
        String sFinal   = DateUtils.fncsFormat("yyyy-MM-dd HH:mm:ss", periodo.getFechaFinal(), DateUtils.REGIONAL_MEXICO, TimeZone.getDefault());

        LogManager.info(sInicial);
        LogManager.info(sFinal);

        try {
            for (DinamicVO<String, String> vo : TurnosDAO.getCortes(sInicial, sFinal, isla)) {
                status.setEntries(vo);
            }
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return status;
    }//execute
    
    public static DinamicVO<String, String> getStatusIsla(String isla) throws DetiPOSFault {
        return new OmicromVO(TurnosDAO.determinaTurno("1"));
    }//execute

    public static void main(String[] args) {
        Calendar fechaInicial = DateUtils.fncoCalendar("yyyy/MM/dd HH:mm:ss", "2019/09/03 13:00:00");
        Calendar fechaFinal = DateUtils.fncoCalendar("yyyy/MM/dd HH:mm:ss", "2019/09/03 04:59:59");

        normalizaPeriodo(fechaInicial, fechaFinal, DateUtils.fncoCalendar("yyyy/MM/dd HH:mm:ss", "2019/09/03 03:00:00"));
    }
}//Common
