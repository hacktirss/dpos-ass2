/*
 * UnidadesVO
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
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.io.IOException;

public abstract class UnidadesDAO {

    public static final String SQL_UNIDAD = "com/detisa/omicrom/sql/SelectUnidad.sql";
    public static final String SQL_PRMTR_CODIGO = "[$]CODIGO";

    public static BaseVO getUnidadV01(String codigo) throws DetiPOSFault {
        BaseVO unidad = new UnidadVO();
        StringBuilder combustible = new StringBuilder();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_UNIDAD).replaceAll(SQL_PRMTR_CODIGO, codigo);
            LogManager.info("Consultando unidad " + codigo);
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach((DinamicVO<String, String> vo) -> {
                        unidad.setEntries(vo);
                        combustible.append(vo.isNVL("COM") ? "" : (combustible.toString().isEmpty() ? "" :  "|") + vo.NVL("COM"));
                    });
            if (!combustible.toString().isEmpty()) {
                unidad.setField("COMBUSTIBLE", combustible.toString());
            }
            LogManager.debug(unidad);
        } catch (DBException | IOException ex) {
            LogManager.info("Ocurrió un eror recuperando la información de la unidad " + codigo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando unidad", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la información del la unidad " + codigo));
        }
        return unidad;
    }//getUnidadV01

    public static UnidadVO getUnidad(String codigo) throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_UNIDAD).replaceAll(SQL_PRMTR_CODIGO, codigo);
            LogManager.info("Consultando unidad " + codigo);
            LogManager.debug(sql);
            return new UnidadVO(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Ocurrió un eror recuperando la información de la unidad " + codigo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando unidad", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la información del la unidad " + codigo));
        }
    }//getUnidad
}//ManguerasDAO
