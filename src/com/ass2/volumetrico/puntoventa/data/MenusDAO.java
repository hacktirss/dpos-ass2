/*
 * MenusDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import java.util.List;

public class MenusDAO {

    public static List <DinamicVO<String, String>> getMenus() throws DetiPOSFault {
        String sql = 
            "SELECT "
            +    "CONCAT( A.parent, '-', A.idx ) ID, "
            +    "A.text T, "
            +    "A.parent I, "
            +    "A.idx O, "
            +    "A.active A, "
            +    "A.protected P, "
            +    "B.size S "
            + "FROM pos_menu A "
            + "INNER JOIN ( "
            +    "SELECT "
            +        "COUNT(*) size, "
            +        "parent "
            +    "FROM pos_menu "
            +    "GROUP BY parent) AS B "
            + "ON B.parent = A.parent "
            + "ORDER BY A.parent, A.id";
        try {
            LogManager.info("Recuperando menús");
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Error recuperando menús");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando Menus", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando Menus"));
        }
    }

    public static List <DinamicVO<String, String>> getMenuList() throws DetiPOSFault {
        String sql = 
            "SELECT "
            +    "id_menu ID, "
            +    "nombre N, "
            +    "valor V "
            + "FROM menus_terminal MT "
            + "ORDER BY ID";
        try {
            LogManager.info("Recuperando menús");
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Error recuperando menús");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando Menus", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando Menus"));
        }
    }
}//MenusDAO
