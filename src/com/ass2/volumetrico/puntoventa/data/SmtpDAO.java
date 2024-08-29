/*
 * SmtpDAO
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

public class SmtpDAO {

    public static SmtpVO getActiveMail() {
        String sql = "SELECT * FROM " + SmtpVO.ENTITY_NAME
                + " WHERE " + SmtpVO.FIELDS.smtpvalido + " = 1";
        SmtpVO current = new SmtpVO();

        try {
            LogManager.info("Consultando servicio activo de correo");
            LogManager.debug(sql);
            current.setEntries(OmicromSLQHelper.getFirst(sql));
            LogManager.debug(current);
        } catch (IndexOutOfBoundsException | DBException ex) {
            LogManager.info("Error consultando servicio activo de correo");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }

        return current;
    }//getActiveMail

}//SmtpDAO
