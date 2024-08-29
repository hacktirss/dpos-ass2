/*
 * CambioPrecioDAO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2017
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;

public class CambioPrecioDAO {

    public static final boolean cambio(String fInicial, String fFinal) {

        String sql = "SELECT * FROM cp WHERE fechaapli BETWEEN '" + fInicial + "' AND '" + fFinal +"'";

        try {

            LogManager.info("Consultando cambios de precio");
            LogManager.debug(sql);
            return !OmicromSLQHelper.empty(sql);
        } catch (DBException ex) {
            LogManager.info("Error consultando cambios de precio");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}
