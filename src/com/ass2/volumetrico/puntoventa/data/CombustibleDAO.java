/*
 * CombustibleDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import static com.ass2.volumetrico.puntoventa.data.CombustibleVO.*;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ROLANDO
 */
public abstract class CombustibleDAO {

    public static final String SQL_COMBUSTIBLE = "com/detisa/omicrom/sql/SelectCombustible.sql";
    public static final String SLQ_PRMTR_CLAVEI = "[$]CLAVEI";

    public static CombustibleVO getCombustibleByClavei(String clavei) throws DetiPOSFault  {

        CombustibleVO combustible = new CombustibleVO();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_COMBUSTIBLE).replaceAll(SLQ_PRMTR_CLAVEI, clavei);
            LogManager.info("Recuperando combustible con clave " + clavei);
            LogManager.debug(sql);
            combustible = new CombustibleVO(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Error recuperando el combustible " + clavei);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error recuperando combustible", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando el combubtible " + clavei));
        }
        return combustible;
    }

    public static List <CombustibleVO> getProductosActivos() throws DBException {
        String sql = "SELECT id, clave, clavei, descripcion, precio, activo, iva, ieps "
                + "FROM " + ENTITY_NAME;

        LogManager.info("Consultando productos activos");
        LogManager.debug(sql);
        return OmicromSLQHelper.executeQuery(sql).stream()
                .map((DinamicVO<String, String> vo) -> new CombustibleVO(vo))
                .collect(Collectors.toList());
    }
}
