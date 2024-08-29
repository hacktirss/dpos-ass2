/*
 * LogDAO
 * ASS2PuntoVenta®
 * © 2020, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since apr 2020
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;

public abstract class LogDAO extends BaseDAO {
    
    public static boolean log(String referencia, String id, String concepto, String usuario) {
        String sql = "INSERT INTO logs ( referencia, fecha, id, concepto, usuario ) VALUES("
                + "'" + referencia + "', "
                + "NOW(), "
                + "'" + id + "', "
                + "'" + concepto + "', "
                + "'" + usuario + "')";
        try {
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.error(ex);
        }
        return false;
    }//updateIsla
}//LogDAO