/*
 * DisplayDAO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2017
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;

public class DisplayDAO {

    public static final String TABLE = "display";

    public static final String CLOSED_TITLE = "<font color=\"#ff6633\"><b>Isla cerrada</b></font>";
    public static final String DEFAULT_TITLE = "<font color=\"#ff6633\"><b>Visor de posiciones</b></font>";
    public static final String DEFAULT_ATITLE = "<font color=\"#ff6633\"><b>Visor de posiciones (Corte Automatico)</b></font>";
    public static final String DEFAULT_MESSAGE = "-----";

    public static boolean showDisplay(String title, String message) {
        String sql = "UPDATE " + TABLE + " SET "
                + "titulo = '" + title + "', "
                + "mensaje = '" + message + "'";
        try {
            LogManager.info("showDisplay::" + message);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
    public static boolean resetDisplay(boolean automatico) {
        String sql = "UPDATE " + TABLE + " SET "
                + "titulo = '" + (automatico ? DEFAULT_ATITLE : DEFAULT_TITLE) + "', "
                + "mensaje = '" + DEFAULT_MESSAGE + "'";
        try {
            LogManager.info("resetDisplay::" + DEFAULT_MESSAGE);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
    public static boolean closedDisplay() {
        String sql = "UPDATE " + TABLE + " SET "
                + "titulo = '" + CLOSED_TITLE + "', "
                + "mensaje = '" + DEFAULT_MESSAGE + "'";
        try {
            LogManager.info("closedDisplay::" + DEFAULT_MESSAGE);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}
