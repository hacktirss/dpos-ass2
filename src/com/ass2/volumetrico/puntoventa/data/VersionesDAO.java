/*
 * VersionesDAO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since oct 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;

public class VersionesDAO {

    public static boolean registerPOSVersion(String version) {
        String sql = "UPDATE servicios SET version = '" + version + "' "
                + "WHERE nombre = 'Newpos'";
        try {
            LogManager.info("Registrando Servicio POS " + version);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql.toString());
        } catch (DBException ex) {
            LogManager.info("Error registrando versión");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}
