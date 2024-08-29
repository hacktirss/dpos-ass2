/*
 * TurnosDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since dec 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.common.OmicromVO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import java.util.List;

public class TurnosDAO {

    public static TurnosVO getCurrentTurno(String isla) throws DetiPOSFault {

        String sql = "SELECT * FROM tur "
                + "WHERE "
                +       "( horai > horaf AND ( "
                +           "( TIME( TIME_FORMAT( CURTIME(), '%H:%i' ) ) BETWEEN TIME( TIME_FORMAT( horai, '%H:%i' ) ) AND CAST( '23:59' AS TIME ) ) OR "
                +           "( TIME( TIME_FORMAT( CURTIME(), '%H:%i' ) ) BETWEEN CAST( '00:00' AS TIME ) AND TIME( TIME_FORMAT( horaf, '%H:%i' ) ) ) ) ) "
                + "OR "
                +       "( horai < horaf AND TIME( TIME_FORMAT( CURTIME(), '%H:%i' ) ) BETWEEN "
                +           "TIME( TIME_FORMAT( horai, '%H:%i' ) ) AND "
                +           "TIME( TIME_FORMAT( horaf, '%H:%i' ) ) ) "
                + "AND activo = 'Si' AND isla = " + isla;

        try {
            LogManager.info("Recuperando corte actual isla " + isla);
            LogManager.debug(sql);
            return new TurnosVO(OmicromSLQHelper.getFirst(sql));
        } catch (IndexOutOfBoundsException | DBException ex) {
            LogManager.info("Error recuperando corte actual isla " + isla);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el turno", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, ex, "No existe el turno o no esta activo"));
        }
    }//getCurrentTurno

    public static List <DinamicVO<String, String>> getCortes(String sInicial, String sFinal, String isla) throws DBException {
        String sql = "SELECT I.isla, I.descripcion, I.status ist, T.turno, C.id id, "
                    + "C.status cst, V.corteautomatico ca, TIME_FORMAT( T.horaf, '%H:%i:%s' ) next "
                    + "FROM islas I " 
                    + "JOIN variables V ON TRUE " 
                    + "JOIN ( SELECT "
                    +           "STR_TO_DATE( '" + sInicial + "', '%Y-%m-%d %H:%i:%s' ) ini, " 
                    +           "STR_TO_DATE( '" + sFinal + "', '%Y-%m-%d %H:%i:%s' ) end ) P ON TRUE "
                    + "JOIN tur T ON T.isla = I.isla AND T.activo = 'Si' AND T.horai = TIME( P.ini ) AND T.horaf = TIME( P.end ) " 
                    + "LEFT JOIN ct C ON C.isla = I.isla AND C.turno = T.turno AND C.fecha BETWEEN P.ini AND P.end "
                    + "WHERE I.isla = " + isla;

        LogManager.info("Recuperando cortes");
        LogManager.debug(sql);
        return OmicromSLQHelper.executeQuery(sql);
    }

    public static DinamicVO<String, String> determinaTurno(String isla) throws DetiPOSFault {
        DinamicVO<String, String> vo = new OmicromVO();
        String sql = 
                    "SELECT I.isla, I.descripcion, I.status ist, T.turno, C.id, C.status cst, IF( V.corteautomatico = 'Si' && T.cortea = 1, 'Si', 'No' ) ca, T.tIni, T.today, T.tEnd, C.fecha ctIni, C.fechaf ctEnd, " 
                +       "TIME_FORMAT( T.tEnd, '%H:%i:%s' ) next " 
                +   "FROM ( " 
                +           "SELECT A.id, A.isla, A.turno, A.descripcion, A.horai, A.horaf, A.activo, A.today, A.cortea, " 
                +               "CASE WHEN A.tIni >= A.tEnd AND A.today <= A.tEnd THEN DATE_ADD( A.tIni, INTERVAL -1 DAY ) ELSE A.tIni END tIni, " 
                +               "CASE WHEN A.tIni >= A.tEnd AND A.today >= A.tIni THEN DATE_ADD( A.tEnd, INTERVAL +1 DAY ) ELSE A.tEnd END tEnd " 
                +           "FROM ( " 
                +                   "SELECT tur.*, T.today, " 
                +                       "STR_TO_DATE( CONCAT( DATE_FORMAT( T.today, '%Y-%m-%d' ), ' ', tur.horai ), '%Y-%m-%d %H:%i:%s' ) tIni, " 
                +                       "STR_TO_DATE( CONCAT( DATE_FORMAT( T.today, '%Y-%m-%d' ), ' ', tur.horaf ), '%Y-%m-%d %H:%i:%s' ) tEnd " 
                +                   "FROM tur JOIN ( SELECT NOW() today ) T ON TRUE " 
                +                   "WHERE activo = 'Si' " 
                +                       "AND ( " 
                +                           "( horai > horaf AND ( " 
                +                               "( TIME( TIME_FORMAT( T.today, '%H:%i' ) ) BETWEEN TIME( TIME_FORMAT( horai, '%H:%i' ) ) AND CAST( '23:59' AS TIME ) ) OR " 
                +                               "( TIME( TIME_FORMAT( T.today, '%H:%i' ) ) BETWEEN CAST( '00:00' AS TIME ) AND TIME( TIME_FORMAT( horaf, '%H:%i' ) ) ) ) ) " 
                +                           "OR " 
                +                           "( horai < horaf AND " 
                +                               "TIME( TIME_FORMAT( T.today, '%H:%i' ) ) BETWEEN TIME( TIME_FORMAT( horai, '%H:%i' ) ) AND TIME( TIME_FORMAT( horaf, '%H:%i' ) ) ) ) " 
                +           ") A " 
                +   ") T " 
                +   "JOIN islas I ON T.isla = I.isla " 
                +   "JOIN variables V ON TRUE " 
                +   "LEFT JOIN ct C ON C.isla = I.isla AND C.turno = T.turno AND C.fecha BETWEEN T.tIni AND T.tEnd AND C.status = 'Abierto' " 
                +   "WHERE I.isla =  1;";

        try {
            LogManager.info("Recuperando corte actual isla " + isla);
            LogManager.debug(sql);
            List<DinamicVO<String, String>> items = OmicromSLQHelper.executeQuery(sql);
            items.forEach(item -> { vo.setEntries( item ); });
            return vo;
        } catch (IndexOutOfBoundsException | DBException ex) {
            LogManager.info("Error recuperando corte actual isla " + isla);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el turno", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, ex, "No existe el turno o no esta activo"));
        }
    }//getCurrentTurno

}//TurnosDAO
