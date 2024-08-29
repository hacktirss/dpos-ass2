/*
 * TanquesDAO
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
import com.softcoatl.database.DBException;

public abstract class TanquesDAO {
    
    public static TanquesVO get(String tanque) {

        String sql = "SELECT tanque, producto, clave_producto, volumen_actual FROM tanques WHERE tanque = " + tanque;

        try {
            LogManager.info("Recuperando Tanque");
            LogManager.debug(sql);
            return new TanquesVO(OmicromSLQHelper.getUnique(sql));
        } //listActives
        catch (DBException ex) {
            LogManager.info("Error recuperando el listado de posiciones activas");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return new TanquesVO();
    }
}
