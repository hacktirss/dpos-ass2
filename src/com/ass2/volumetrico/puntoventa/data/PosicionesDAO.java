/*
 * PosicionesDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLHelper;
import java.util.ArrayList;
import java.util.List;

public abstract class PosicionesDAO {
    
    public static List <DinamicVO<String, String>> listActives(String isla) throws DetiPOSFault {
        List <DinamicVO<String, String>> actives = new ArrayList <> ();

        String sql = "SELECT man.*, estado_posiciones.estado " 
                    + "FROM man " 
                    + "JOIN estado_posiciones ON man.posicion = estado_posiciones.posicion " 
                    + "WHERE ACTIVO = 'Si' " 
                    + "AND man.isla = " + isla + " "
                    + "AND estado_posiciones.estado <> '-'";

        try {
            LogManager.info("Consultando posiciones activas Isla " + isla);
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(man -> actives.add(new PosicionesVO(man)));
        } //listActives
        catch (DBException ex) {
            LogManager.info("Error consultando posiciones activas Isla " + isla);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando posiciones", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando el listado de posiciones activas"));
        }
        return actives;
    }//listActives

    public static List <DinamicVO<String, String>> listDisconnected(String isla) {

        List <DinamicVO<String, String>> actives = new ArrayList <> ();
        String sql = "SELECT man.*, estado_posiciones.estado " 
                    + "FROM man " 
                    + "JOIN estado_posiciones ON man.posicion = estado_posiciones.posicion " 
                    + "WHERE man.activo = 'Si' " 
                    + "AND man.isla=" + isla + " " 
                    + "AND estado_posiciones.estado = '-'";

        try {
            
            LogManager.info("Consultando posiciones desconectadas");
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(man -> actives.add(new PosicionesVO(man)));
        } //listActives
        catch (DBException ex) {
            LogManager.info("Error recuperando el listado de posiciones activas");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return actives;
    }//listDisconnected
    
    public static boolean updateDespachadoresTurno(String isla) {
        String sql = "UPDATE man SET despachador = despachadorsig, despachadorsig = posicion WHERE isla = " + isla;
        try {
            LogManager.info("Actualizando despachadores en Isla " + isla);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando despachadores en Isla " + isla);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}//PosicionesDAO
