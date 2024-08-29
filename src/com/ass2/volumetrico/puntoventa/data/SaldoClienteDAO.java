/*
 * SaldoClienteDAO
 * ASS2PuntoVenta®
 * © 2020, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since apr 2020
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

public abstract class SaldoClienteDAO {
    
    public static final String SQL_SALDO_CLIENTE = "com/detisa/omicrom/sql/SelectSaldoCliente.sql";
    public static final String SQL_CLIENTE = "[$]CLI_ID";

    public static DinamicVO<String, String> getSaldoCliente(String cliente) throws DetiPOSFault {

        DinamicVO<String, String> data = new DinamicVO<>();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_SALDO_CLIENTE).replaceAll(SQL_CLIENTE, cliente);
            LogManager.info("Consultando saldo del cliente " + cliente);
            LogManager.debug(sql);
            data.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Consultando saldo del cliente " + cliente);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando saldo", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la información del cliente " + cliente));
        }

        return data;
    }//retrieveData
}
