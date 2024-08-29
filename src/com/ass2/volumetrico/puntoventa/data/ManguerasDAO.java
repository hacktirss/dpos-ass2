/*
 * ManguerasDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import static com.ass2.volumetrico.puntoventa.data.ManguerasVO.*;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class ManguerasDAO {
    
    public static final String SQL_CATALOGO = "com/detisa/omicrom/sql/SelectCatalogoMangueras.sql";
    public static final String SQL_POSMAN = "com/detisa/omicrom/sql/SelectPosicionManguera.sql";
    public static final String SQL_POSICION = "com/detisa/omicrom/sql/SelectPosicion.sql";

    public static final String SQL_PRMTR_MANGUERA = "[$]MANGUERA";
    public static final String SQL_PRMTR_POSICION = "[$]POSICION";

    public static boolean updateFactor(String posicion, String manguera, String factor) {
        String sql = "UPDATE " + ENTITY_NAME + " "
                + "SET " + DSP_FIELDS.factor + " = " + factor + " "
                + "WHERE " + DSP_FIELDS.posicion + " = " + posicion + " "
                + "AND " + DSP_FIELDS.manguera + " = " + manguera;
        
        try {
            LogManager.info("Actualizando Factor");
            LogManager.debug(sql);
            return BaseDAO.execute(MySQLHelper.DB_NAME, sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando Factor");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }

    public static BaseVO getDispensarioPosicionManguera(String posicion, String manguera) throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_POSMAN)
                    .replaceAll(SQL_PRMTR_MANGUERA, manguera)
                    .replaceAll(SQL_PRMTR_POSICION, posicion);
            LogManager.info(String.format("Recuperando Manguera %s en la Posición %s", manguera, posicion));
            LogManager.debug(sql);
            return new ManguerasVO(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info(String.format("Error recuperando Manguera %s en la Posición %s", manguera, posicion));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error Recuperando Manguera", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la informacion del la manguera " + manguera));
        }
    }//getDispensarioAtManguera

    public static List <DinamicVO<String, String>> getCatalogoMangueras() throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CATALOGO);
            LogManager.info("Recuperando Catálogo de Mangueras");
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException | IOException ex) {
            LogManager.info("Recuperando Catálogo de Mangueras");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error Recuperando Mangueras", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando el catálogo de mangueras"));
        }
    }//getCatalogoMangueras

    public static List <ManguerasVO> getMangueras() throws DetiPOSFault {

        List<ManguerasVO> mangueras = new ArrayList<>();
        try {
            String sql = "SELECT id, dispensario, posicion, manguera, dis_mang, producto, isla, activo, factor, enable, tanque, totalizadorV, totalizador$ "
                    + "FROM " + ENTITY_NAME + " "
                    + "WHERE " + DSP_FIELDS.activo + " = 'Si' "
                    + "ORDER BY posicion, manguera";
            LogManager.info("Recuperando Catálogo de Mangueras");
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(man -> mangueras.add(new ManguerasVO(man)));
        } catch (DBException ex) {
            LogManager.info("Recuperando Catálogo de Mangueras");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error Recuperando Mangueras", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando el catálogo de mangueras"));
        }
        return mangueras;
    }//getCatalogoMangueras

    public static ManguerasVO getDispensarioAtPosicion(String posicion, String manguera) throws DetiPOSFault {

        ManguerasVO dispensario = new ManguerasVO();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_POSICION)
                    .replaceAll(SQL_PRMTR_POSICION, posicion)
                    .replaceAll(SQL_PRMTR_MANGUERA, manguera);
            LogManager.info(String.format("Recuperando Manguera %s en la Posición %s", manguera, posicion));
            LogManager.debug(sql);
            dispensario = new ManguerasVO(OmicromSLQHelper.getUnique(sql));
            if (dispensario.isVoid()) {
                throw new InvalidParameterException("La posicion no existe o no se encuentra activa");
            }
        } catch (DBException | IOException | InvalidParameterException ex) {
            LogManager.info(String.format("Recuperando Manguera %s en la Posición %s", manguera, posicion));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage(), new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando la información del la posicion " + posicion));
        }
        return dispensario;
    }//getDispensarioAtPosicion

    public static BaseVO getDispensario(String posicion) throws DetiPOSFault {

        BaseVO dispensario = new PosicionesVO();

        try {
            String sql = "SELECT * FROM man WHERE posicion = " + posicion;
            LogManager.info(String.format("Recuperando Posición %s", posicion));
            LogManager.debug(sql);
            dispensario = new PosicionesVO(OmicromSLQHelper.getUnique(sql));
            if (dispensario.isVoid()) {
                throw new InvalidParameterException("La posicion no existe o no se encuentra activa");
            }
        } catch (DBException | InvalidParameterException ex) {
            LogManager.info(String.format("Recuperando Posición %s", posicion));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage(), new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando la información del la posicion " + posicion));
        }
        return dispensario;
    }//getDispensarioAtPosicion
}//ManguerasDAO
