/*
 * POSConfigDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import static com.ass2.volumetrico.puntoventa.data.POSConfigVO.*;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.mysql.MySQLHelper;
import java.util.ArrayList;
import java.util.List;

public class POSConfigDAO {

    public static List <POSConfigVO> _getConfiguration(String posModel, String posID, String fileType) throws DBException {
 
        List <POSConfigVO> configuration = new ArrayList <> ();
        String sql = "SELECT " + MAPPING.getFieldString() + " "
                + "FROM " + ENTITY_NAME + " "
                + "WHERE status = 'P' "
                + "AND (" + FIELDS.pos_model + " = 'ALL' "
                + (!StringUtils.isNVL(posModel) ? "OR " + FIELDS.pos_model + " LIKE '%" + posModel.trim() + "%' " : "")
                + "OR " + FIELDS.pos_model + " IS NULL) "
                + "AND (" + FIELDS.pos_id + " IS NULL "
                + (!StringUtils.isNVL(posID) ? "OR " + FIELDS.pos_id + " = " + posID.trim() + ")" : ")" )
                + (!StringUtils.isNVL(fileType) ? "AND " + FIELDS.filetype + " LIKE '%" + fileType.trim() + "%'" : "");

        LogManager.info("Consultando archivos de configuración");
        LogManager.debug(sql);
        OmicromSLQHelper.executeQuery(sql)
                .forEach(conf -> configuration.add(new POSConfigVO(conf)));
                
        return configuration;
    }//getLogo

    public static List <DinamicVO<String, String>> getConfiguration(String fileType) throws DBException {
 
        String sql = "SELECT key_file, CONCAT( key_file, '.', extension ) fileName, CONVERT( file USING 'UTF8' ) fileContent FROM sys_files WHERE additional = 'POS' AND format = '" + fileType + "';";
        LogManager.info("Consultando archivos de configuración");
        LogManager.debug(sql);
        return OmicromSLQHelper.executeQuery(sql);
    }

    public static void updateDownloaded(String id) {

        String sql = "UPDATE pos_config "
                + "SET status = 'E' "
                + "WHERE id = " + id;
        
        try {
            LogManager.info("Actualizando descarga de archivos de configuración");
            LogManager.debug(sql);
            MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando descarga de archivos de configuración");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }
}//POSConfigDAO
