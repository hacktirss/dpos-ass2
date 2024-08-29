/*
 * EstadoPosicionesDAO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.mysql.MySQLHelper;
import java.util.List;

public abstract class EstadoPosicionesDAO {

    public static boolean updateStatus(String posicion, String codigo, String kilometraje, String eco) throws DetiPOSFault {

        boolean updated = false;
        String sql;

        LogManager.debug("Codigo " + codigo);
        if (StringUtils.isNVL(posicion) || StringUtils.isNVL(codigo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error actualizando Estados_Posicion", "Parámetros inválidos"));
        }
        sql = "UPDATE estado_posiciones "
                + "SET "
                + (!StringUtils.isNVL(codigo) ? "codigo = '"  + codigo + "', " : "")
                + (!StringUtils.isNVL(eco) ? "eco = '" + eco + "', " : "")
                + "kilometraje = '" + (StringUtils.isNVL(kilometraje) ? "0" : kilometraje) + "' "
                + "WHERE posicion = " + posicion;
        try {
            LogManager.info(String.format("Actualizando posición %s, código %s, odómetro %s, eco %s", posicion, codigo, kilometraje, eco));
            LogManager.debug(sql);
            updated = MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error actualizando posición %s, código %s, odómetro %s, eco %s", posicion, codigo, kilometraje, eco));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return updated;
    }
    
    public static EstadoPosicionesVO getByID(int id) {

        EstadoPosicionesVO ep = new EstadoPosicionesVO();
        String sql = "SELECT " + CollectionsUtils.fncsEnumAsString(EstadoPosicionesVO.EP_FIELDS.class) + " "
                + "FROM " + EstadoPosicionesVO.ENTITY_NAME + " "
                + "WHERE " + EstadoPosicionesVO.EP_FIELDS.id + " = " + id;
        
        try {
            LogManager.debug("Consultando posición " + id);
            LogManager.debug(sql);
            ep.setEntries(OmicromSLQHelper.getUnique(sql));
            LogManager.debug("Estado actual posición: " + ep);
        } catch (DBException ex) {
            LogManager.info("Error consultando posición " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return ep;
    }

    public static int checkCardCode(String code) {

        String sql = "SELECT posicion FROM estado_posiciones WHERE codigo = '" + code + "'";
        
        try {
            LogManager.info("Consultando posición con código " + code);
            LogManager.debug(sql);
            List<DinamicVO<String, String>> current = OmicromSLQHelper.executeQuery(sql);
            if (!current.isEmpty()) {
                return Integer.parseInt(current.get(0).NVL("posicion"));
            }
        } catch (DBException ex) {
            LogManager.info("Error consultando posición con código " + code);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return 0;
    }

    public static EstadoPosicionesVO getByPosicion(String posicion) {

        EstadoPosicionesVO ep = new EstadoPosicionesVO();
        String sql = "SELECT " + CollectionsUtils.fncsEnumAsString(EstadoPosicionesVO.EP_FIELDS.class) + " "
                + "FROM " + EstadoPosicionesVO.ENTITY_NAME + " "
                + "WHERE " + EstadoPosicionesVO.EP_FIELDS.posicion + " = " + posicion;

        try {
            LogManager.info("Consultando posición " + posicion);
            LogManager.debug(sql);
            ep = new EstadoPosicionesVO(OmicromSLQHelper.getUnique(sql));
            LogManager.info(ep.getLogInfo());
        } catch (DBException ex) {
            LogManager.info("Error consultando posición " + posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return ep;
    }

    public static boolean presetInProgress(String posicion) {

        String sql = "SELECT if ( codigo = '0', 'false', 'true' ) inprogress "
                + "FROM " + EstadoPosicionesVO.ENTITY_NAME + " "
                + "WHERE " + EstadoPosicionesVO.EP_FIELDS.posicion + " = " + posicion;

        try {
            LogManager.info("Consultando posición " + posicion);
            LogManager.info(sql);
            return Boolean.valueOf(OmicromSLQHelper.getUnique(sql).NVL("inprogress"));
        } catch (DBException ex) {
            LogManager.info("Error consultando posición " + posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}
