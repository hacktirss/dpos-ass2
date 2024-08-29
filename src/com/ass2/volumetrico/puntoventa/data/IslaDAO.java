/*
 * IslaDAO
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
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

public abstract class IslaDAO  {
    
    public static final String SQL_ISLA = "com/detisa/omicrom/sql/SelectIsla.sql";
    public static final String SQL_DEFISLA = "com/detisa/omicrom/sql/SelectDefaultIsla.sql";
    public static final String SQL_PRMTR_ISLA = "[$]ISLA";

    public static BaseVO getDefaultIsla() throws DetiPOSFault {

        BaseVO isla = new IslaVO();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_DEFISLA);
            LogManager.info("Consultando Isla por defecto");
            LogManager.debug(sql);
            List <DinamicVO<String, String>> actives = OmicromSLQHelper.executeQuery(sql);
            if (actives.isEmpty()) {
                throw new DetiPOSFault("No existe una isla activa");
            } else if (actives.size()!=1) {
                throw new DetiPOSFault("Existe más de una isla activa");
            }
            isla = new IslaVO(actives.get(0));
        } catch (DetiPOSFault | DBException | IOException ex) {
            LogManager.info("Error consultando Isla por defecto");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage(), new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando la informacion del la isla activa"));
        }
        return isla;
    }//getDefaultIsla

    public static BaseVO getIslaByID(String islaID) throws DetiPOSFault {

        BaseVO isla = new IslaVO();

        try {
            if (StringUtils.isNVL(islaID)) {
                throw new InvalidParameterException("Error de parametros. Se esperaba el valor ISLA_ID");
            }
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_ISLA).replaceAll(SQL_PRMTR_ISLA, islaID);
            LogManager.info("Consultando Isla " + islaID);
            LogManager.debug(sql);
            isla.setEntries(OmicromSLQHelper.getUnique(sql));
            if (isla.isVoid()) {
                throw new InvalidParameterException("La isla no existe o no se encuentra activa");
            }
        } catch (DBException | IOException | InvalidParameterException ex) {
            LogManager.info("Error consultando Isla " + islaID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage(), new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando la información del la isla "+islaID));
        }
        return isla;
    }//getIslaByID

    public static boolean updateIsla(String isla, String corteID, String turno) {
        String sql = "UPDATE islas SET turno = '" + turno + "', "
                        + "corte = '" + corteID + "', "
                        + "status = 'Abierta' "
                        + "WHERE isla = '" + isla + "'";
        try {
            LogManager.info(String.format("Actualizando Isla %s, Corte %s, Turno %s", isla, corteID, turno));
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error actualizando Isla %s, Corte %s, Turno %s", isla, corteID, turno));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }//updateIsla

    public static boolean closeIsla(String isla) {
        String sql = "UPDATE islas SET status='Cerrada', corte = 0 WHERE isla = " + isla;

        try {
            LogManager.debug("Cerrando Isla");
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error cerrabdi Isla %s", isla));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }//updateIsla
}//IslaDAO