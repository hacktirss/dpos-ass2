/*
 * ClientesDAO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2017
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
import java.util.List;
import java.util.stream.Collectors;

public abstract class ClientesDAO {

    public static final String SQL_CLIENTE = "com/detisa/omicrom/sql/SelectCliente.sql";
    public static final String SQL_CLIENTE_RFC = "com/detisa/omicrom/sql/SelectClienteByRFC.sql";
    public static final String SQL_CLIENTE_BALANCE = "com/detisa/omicrom/sql/SelectClienteBalance.sql";
    public static final String SQL_PRMTR_CLIID = "[$]CLIENTE";
    public static final String SQL_PRMTR_CLIRFC = "[$]RFC";

    public static ClientesVO getClienteByID(String clienteID) throws DetiPOSFault {

        ClientesVO cliente = new ClientesVO();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CLIENTE).replaceAll(SQL_PRMTR_CLIID, clienteID);
            LogManager.info("Consultando cliente " + clienteID);
            LogManager.debug(sql);
            cliente.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Error recuperando la información del cliente " + clienteID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando cliente", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la informacion del cliente " + clienteID));
        }
        return cliente;
    }

    public static ClientesVO getClienteAutorizador(String nombre) throws DetiPOSFault {

        ClientesVO cliente = new ClientesVO();

        try {
            String sql = "SELECT * FROM cli WHERE nombre = '" + nombre + "' AND observaciones = 'Autorizador'";
            LogManager.info("Consultando autorizador " + nombre);
            LogManager.debug(sql);
            cliente.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error recuperando la información del autorizador " + nombre);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando cliente", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la informacion del autorizador " + nombre));
        }
        return cliente;
    }
    public static List<BaseVO> getClienteByRFC(String clienteRFC) throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CLIENTE_RFC).replaceAll(SQL_PRMTR_CLIRFC, clienteRFC);
            LogManager.info("Consultando cliente " + clienteRFC);
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql).stream()
                    .map((DinamicVO<String, String> item) -> new ClientesVO(item))
                    .collect(Collectors.toList());
        } catch (DBException | IOException ex) {
            LogManager.info("Error recuperando la información del cliente " + clienteRFC);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando cliente", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando la informacion del cliente " + clienteRFC));
        }
    }
    public static BaseVO getClienteBalanceByID(String clienteID) throws DetiPOSFault {

        BaseVO balance = new ClientesBalanceVO();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CLIENTE_BALANCE).replaceAll(SQL_PRMTR_CLIID, clienteID);
            LogManager.info("Consultando balance del cliente " + clienteID);
            LogManager.debug(sql);
            balance.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Error recuperando la información del cliente " + clienteID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando cliente", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando balance del cliente " + clienteID));
        }
        return balance;
    }
}
