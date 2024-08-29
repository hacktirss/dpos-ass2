/*
 * GycseEndpointsDAO
 * CobroServicios®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since ago 2016
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;

public class GycseEndpointsDAO {

    public static GycseEndpoints getEndpoint() throws DetiPOSFault {

        String sql = "SELECT " + GycseEndpoints.MAPPING.getFieldString() + " "
                    + "FROM " + GycseEndpoints.MAPPING.getEntity() + " "
                    + "WHERE " + GycseEndpoints.FIELDS.activo + " = 1";
        try {
            LogManager.info("Consultando Endpoint Gycse");
            LogManager.debug(sql);
            return new GycseEndpoints(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error consultando Endpoint Gycse");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }
}
