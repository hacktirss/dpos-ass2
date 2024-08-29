/*
 * BancosDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import java.util.ArrayList;
import java.util.List;

public class CFDICatalogosDAO {
    
    public static List <DinamicVO<String, String>> getFormaPago() {

        String sql = "SELECT clave, descripcion FROM cfdi33_c_fpago WHERE posmenu = '1'";

        try {
            LogManager.info("Recuperando formas de pago");
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Error recuperando formas de pago");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return new ArrayList<>();
    }//getBoleto
}//BancosDAO
