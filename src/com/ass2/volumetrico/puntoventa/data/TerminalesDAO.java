/*
 * TerminalesDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since dec 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;
import java.util.List;

public abstract class TerminalesDAO {

    public static final String ENTITY_NAME = "pos_catalog";

    public static List<DinamicVO<String, String>> getActives() throws DBException {

        String sql = "SELECT pos_id, serial, model, ip, status "
                    + "FROM " + ENTITY_NAME + " WHERE status = 'A'";

        LogManager.info("Consultando Terminales activas");
        LogManager.debug(sql);
        return OmicromSLQHelper.executeQuery(sql);
    }

    public static DinamicVO<String, String> getTerminalBySerialNumber(String serialNumber) throws DBException {

        String sql = "SELECT pos_id AS POS_ID, serial AS SERIAL, "
                    + "model AS MODEL, ip AS IP, status AS STATUS "
                    + "FROM " + ENTITY_NAME + " WHERE serial = '" + serialNumber + "'";

        LogManager.info("Consultando Terminal " + serialNumber);
        LogManager.debug(sql);
        return OmicromSLQHelper.getUnique(sql);
    }
    
    public static boolean lastConnection(String posid) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                    + "SET lastConnection = NOW() WHERE pos_id = " + posid;
    
        LogManager.info("Estableciendo la última conexión para la terminal " + posid);
        LogManager.debug(sql);
        return MySQLDB.fieldExist("omicrom", ENTITY_NAME, "lastConnection")
                && MySQLHelper.getInstance().execute(sql);
    }//lastConnection

    public static boolean updateAppVersion(String posid, String appVersion) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                    + "SET appVersion = '" + appVersion + "' WHERE pos_id = " + posid;
    
        LogManager.info("Estableciendo la versión para la terminal " + posid);
        LogManager.debug(sql);
        return MySQLDB.fieldExist("omicrom", ENTITY_NAME, "appVersion")
                && MySQLHelper.getInstance().execute(sql);
    }

    public static boolean updateIP(String posid, String ip) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                    + "SET ip = '" + ip + "' WHERE pos_id = " + posid;
    
        LogManager.info("Estableciendo IP para la terminal " + posid);
        LogManager.debug(sql);
        return MySQLDB.fieldExist("omicrom", ENTITY_NAME, "ip")
                && MySQLHelper.getInstance().execute(sql);
    }

    public static boolean updateLAN(String posid, String lanMacAdd) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                    + "SET maclan = '" + lanMacAdd + "' WHERE pos_id = " + posid;
    
        LogManager.info("Estableciendo macaddress para la terminal " + posid);
        LogManager.debug(sql);
        return MySQLDB.fieldExist("omicrom", ENTITY_NAME, "maclan")
                && MySQLHelper.getInstance().execute(sql);
    }

    public static boolean updateWLAN(String posid, String wlanMacAdd) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                    + "SET macwifi = '" + wlanMacAdd + "' WHERE pos_id = " + posid;
        LogManager.info("Estableciendo macaddress wifi para la terminal " + posid);
        LogManager.debug(sql);
        return MySQLDB.fieldExist("omicrom", ENTITY_NAME, "macwifi")
                && MySQLHelper.getInstance().execute(sql);
    }

    public static boolean updateKernel(String posid, String kernelVersion) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                    + "SET kernel = '" + kernelVersion + "' WHERE pos_id = " + posid;
        LogManager.info("Estableciendo versión de kernel para la terminal " + posid);
        LogManager.debug(sql);
        return MySQLDB.fieldExist("omicrom", ENTITY_NAME, "kernel")
                && MySQLHelper.getInstance().execute(sql);
    }
}//TerminalesDAO
