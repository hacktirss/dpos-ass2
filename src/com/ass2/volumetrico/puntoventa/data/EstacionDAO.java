/*
 * EstacionesDAO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.io.IOException;
import org.apache.logging.log4j.Logger;;

public class EstacionDAO {

    private static final Logger LOG = LogManager.getRootLogger();
    private static final String SQL_ESTACION = "com/detisa/omicrom/sql/SelectEstacion.sql";

    public static DinamicVO<String, String> getDatosEstacion() throws DetiPOSFault {

        DinamicVO<String, String> datosEstacion = new DinamicVO<>();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_ESTACION);
            LOG.info("Consultando datos de estación");
            LOG.debug(sql);
            datosEstacion.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LOG.info("Error consultando datos de estación");
            LOG.error(ex);
            LOG.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }

        return datosEstacion;
    }//getDatosEstacion
}//EstacionDAO