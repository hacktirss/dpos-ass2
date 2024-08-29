/*
 * VentaAditivosDAO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since oct 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentaAditivosDAO {

    public static final String SQL_VENTA_ADITIVOS = "com/detisa/omicrom/sql/SelectVentaAditivos.sql";
    public static final String SQL_VENTA_ADITIVOS_ID = "com/detisa/omicrom/sql/SelectVentaAditivosID.sql";
    public static final String SQL_PRMTR_REFERENCIA = "[$]REFERENCIA";
    public static final String SQL_PRMTR_ID = "[$]ID";

    public static List <DinamicVO<String, String>> getByReference(String referencia) throws DetiPOSFault {

        List <DinamicVO<String, String>> aditivos = new ArrayList <> ();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_VENTA_ADITIVOS)
                    .replaceAll(SQL_PRMTR_REFERENCIA, referencia);
            LogManager.info("Recuperando aditivos asociados al consumo " + referencia);
            LogManager.debug(sql);
            aditivos.addAll(OmicromSLQHelper.executeQuery(sql));
        } catch (DBException | IOException ex) {
            LogManager.error("Recuperando aditivos asociados al consumo " + referencia);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Recuperando venta de aditivos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando venta de aditivos"));
        }
        return aditivos;
    }

    public static DinamicVO<String, String> getById(String id) throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_VENTA_ADITIVOS_ID)
                    .replaceAll(SQL_PRMTR_ID, id);
            LogManager.info("Recuperando venta de aditivos " + id);
            LogManager.debug(sql);
            return OmicromSLQHelper.getUnique(sql);
        } catch (DBException | IOException ex) {
            LogManager.error("Recuperando aditivos asociados al consumo " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Recuperando venta de aditivos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando venta de aditivos"));
        }
    }
}
