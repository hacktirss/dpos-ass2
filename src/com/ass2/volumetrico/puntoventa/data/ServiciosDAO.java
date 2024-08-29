/*
 * ServiciosDAO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2017
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;

public class ServiciosDAO {

    public static String getActiveFuelTankSensorService() {
        String sql = "SELECT * FROM servicios WHERE nombre REGEXP 'veeder|autostik|sentinel' AND status = 'Si'";

        try {
            LogManager.info("Recuperando servicio activo de Sensor de Tanques");
            LogManager.debug(sql);
            return OmicromSLQHelper.getUnique(sql).NVL("nombre");
        } //listActives
        catch (DBException ex) {
            LogManager.info("Error recuperando servicio activo de Sensor de Tanques");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return "";
    }
}
