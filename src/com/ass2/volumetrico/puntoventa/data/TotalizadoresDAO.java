/*
 * TotalizadoresDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;
import java.util.List;
import static com.ass2.volumetrico.puntoventa.data.TotalizadoresVO.*;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TotalizadoresDAO {

    public static boolean drop(String corte) {

        String sql = "DELETE FROM " + ENTITY_NAME + " WHERE " + T_FIELDS.idtarea + "=" + corte;
        LogManager.info("Limpiando Totalizadores para el corte " + corte);
        LogManager.debug(sql);
        try {
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error limpiando Totalizadores para el corte " + corte);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }

    public static boolean exist(String posicion, String corte) {

        String sql = "SELECT * FROM " + ENTITY_NAME + " "
                    + "WHERE " + T_FIELDS.posicion + " = " + posicion + " "
                    + "AND " + T_FIELDS.idtarea + "=" + corte;
        try {
            LogManager.info("Consultando Totalizador para el corte " + corte + " posicion " + posicion);
            LogManager.debug(sql);
            return !OmicromSLQHelper.empty(sql);
        } catch (DBException ex) {
            LogManager.info("Error consultando Totalizador para el corte " + corte + " posicion " + posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        
        return false;
    }

    public static List <TotalizadoresVO> getTotalizadores(String idTarea) throws DBException {

        Map<String, TotalizadoresVO> totalizadores = new HashMap<>();
        String sql = "SELECT DISTINCT "
                    + "posicion, turno, monto1, volumen1, "
                    + "monto2, volumen2, monto3, volumen3, "
                    + "DATE( fecha ) fecha, idtarea "
                    + "FROM " + ENTITY_NAME + " "
                    + "WHERE " + T_FIELDS.idtarea + " = " + idTarea + " "
                    + "AND (volumen1 <> 0 OR volumen2 <> 0 OR volumen3 <> 0)";

        LogManager.info("Consultando Totalizadores para el corte " + idTarea);
        LogManager.debug(sql);
        OmicromSLQHelper.executeQuery(sql)
                .forEach((DinamicVO<String, String> tot) -> { 
                    if (!totalizadores.containsKey(tot.NVL("posicion"))) { 
                        totalizadores.put(tot.NVL("posicion"), new TotalizadoresVO(tot)); 
                    } 
                });
        return new ArrayList<>(totalizadores.values());
    }
    
    public static List <TotalizadoresVO> getTotalizadoresManPro(String idTarea) throws DBException {

        Map<String, TotalizadoresVO> totalizadores = new HashMap<>();
        String sql = "SELECT DISTINCT "
                + "posicion, SUM( monto1 ) monto1, SUM( monto2 ) monto2, SUM( monto3 ) monto3, "
                + "SUM( volumen1 ) volumen1, SUM( volumen2 ) volumen2, SUM( volumen3 ) volumen3 "
                + "FROM ( "
                +       "SELECT posicion, `totalizador$` monto1, totalizadorV volumen1, 0 monto2, 0 volumen2, 0 monto3, 0 volumen3 FROM man_pro WHERE activo = 'Si' AND manguera = 1 "
                +       "UNION ALL "
                +       "SELECT posicion, 0 monto1, 0 volumen1, `totalizador$` monto2, totalizadorV volumen2, 0 monto3, 0 volumen3 FROM man_pro WHERE activo = 'Si' AND manguera = 2 "
                +       "UNION ALL "
                +       "SELECT posicion, 0 monto1, 0 volumen1, 0 monto2, 0 volumen2, `totalizador$` monto3, totalizadorV volumen3 FROM man_pro WHERE activo = 'Si' AND manguera = 3 "
                + ") totalizadores GROUP BY posicion";
        LogManager.info("Simulando Totalizadores para el corte " + idTarea);
        LogManager.debug(sql);
        OmicromSLQHelper.executeQuery(sql)
                .forEach((DinamicVO<String, String> tot) -> { 
                    if (!totalizadores.containsKey(tot.NVL("posicion"))) { 
                        totalizadores.put(tot.NVL("posicion"), new TotalizadoresVO(tot)); 
                    } 
                });
        return new ArrayList<>(totalizadores.values());
    }
}
