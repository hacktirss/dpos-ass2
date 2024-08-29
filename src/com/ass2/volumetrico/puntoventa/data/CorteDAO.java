/*
 * CorteVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.database.DBException;
import static com.ass2.volumetrico.puntoventa.data.CorteVO.*;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;

public class CorteDAO {

    public static CorteVO getCorte(String isla, String id) throws DetiPOSFault {

        CorteVO corte = new CorteVO();
        StringBuffer sql = new StringBuffer("SELECT ")
                                    .append("   id, ")
                                    .append("   DATE_FORMAT( fecha, '%d/%m/%Y %H:%i:%s' ) AS fecha, ")
                                    .append("   DATE_FORMAT( fechaf, '%d/%m/%Y %H:%i:%s' ) AS fechaf, ")
                                    .append("   concepto, isla, turno, ")
                                    .append("   usr,  status, statusctv ")
                                    .append("FROM ").append(ENTITY_NAME).append(" ")
                                    .append("WHERE isla = ").append(isla).append(" ")
                                    .append("AND id = ").append(id).append(" ")
                                    .append("ORDER BY ID desc");

        try {
            LogManager.debug(sql);
            if (!"0".equals(id)) {
                corte.setEntries(OmicromSLQHelper.getFirst(sql));
            }
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el corte", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "El corte no existe", "Error obteniendo el corte de la isla "+isla), ex);
        }

        return corte;
    }//getCorteAbierto

    public static CorteVO getCorte(String id) throws DetiPOSFault {

        return getCorte("1", id);
    }//getCorteAbierto

    public static String getUltimoCorteCerrado(String isla) throws DetiPOSFault {
        StringBuffer sql = new StringBuffer("SELECT IFNULL( MAX(id), 0 ) corte FROM ct WHERE isla = ").append(isla).append(" AND  status = 'Cerrado'");
        try {
            LogManager.debug(sql);
            return OmicromSLQHelper.getFirst(sql).NVL("corte");
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el corte", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "El corte no existe", "Error obteniendo el corte de la isla "+isla), ex);
        }
    }
    public static String getCorteAbierto(String isla) throws DetiPOSFault {
        StringBuffer sql = new StringBuffer("SELECT IFNULL( ( SELECT id FROM ct WHERE isla = ").append(isla).append(" AND status = 'Abierto' ORDER BY ID DESC ), 0 ) corte");

        try {
            LogManager.debug(sql);
            return OmicromSLQHelper.getFirst(sql).NVL("corte");
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el corte", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "El corte no existe", "Error obteniendo el corte de la isla "+isla), ex);
        }
    }//getCorteAbierto

    public static boolean closeCorte(String id, String detail) throws DBException {
        String sql = "UPDATE ct SET status = 'Cerrado', fechaf = NOW(), concepto = '"+DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss '(" + detail + ")'")+"' WHERE id = "+id;
        LogManager.info("Cerrando corte " + id);
        MySQLHelper.getInstance().execute(sql);
        return true;
    }//closeCortes

    public static CorteVO newCorte(String isla, String turno) throws DBException {
        CorteVO corte = CorteVO.getInstance(isla, turno);
        corte.setField(CT_FIELDS.id, String.valueOf(MySQLDB.next(ENTITY_NAME, CT_FIELDS.id.name())));
        LogManager.info("Creatindo corte en isla " + isla + " turno " + turno);
        LogManager.debug(corte);
        MySQLHelper.getInstance().insert(corte);
        LogManager.info("Corte creaado con id " + corte.NVL(CorteVO.CT_FIELDS.id.name()));
        return corte;
    }//newCorte
}//CorteDAO
