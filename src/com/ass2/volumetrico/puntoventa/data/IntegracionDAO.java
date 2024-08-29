/*
 * IntegracionDAO
 * ASS2PuntoVenta®
 * © 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2019
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.entity.vo.BaseVO;

public abstract class IntegracionDAO extends BaseDAO {

    public static BaseVO getIntegracion(String integracionID) throws DetiPOSFault {

        BaseVO integracion = new IntegracionVO();

        try {
            String sql = "SELECT * FROM integraciones WHERE clave = '" + integracionID + "'";
            LogManager.info("Consultando Integración " + integracionID);
            LogManager.debug(sql);
            integracion.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error consultando Integración " + integracionID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error Consultando Integracion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la información de la integracion " + integracionID));
        }
        return integracion;
    }
}
