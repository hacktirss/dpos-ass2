/**
 * DepositoDAO
 * ® 2020, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2020
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.StringUtils;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepositoDAO {

    public static DepositoVO getDeposito(String despachador, String corte) throws DetiPOSFault {

        DepositoVO deposito = new DepositoVO();
        String sql = "SELECT * FROM ctdep "
                + "WHERE corte = " + (corte) + " "
                + "AND despachador = " + (despachador) + " "
                + "ORDER BY id DESC LIMIT 1";
        try {
            LogManager.info("Consultando depósitos del despachador " + despachador);
            LogManager.debug(sql);
            deposito.setEntries(OmicromSLQHelper.getFirst(sql));
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.info("Error consultando depósitos del despachador " + despachador);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el deposito", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error obteniendo deposito para el despachador " + despachador));
        }
        return deposito;
    }

    public static DepositoVO getDepositoById(int id) throws DetiPOSFault {

        DepositoVO deposito = new DepositoVO();
        String sql = "SELECT * FROM ctdep "
                + "WHERE id = " + id;
        try {
            LogManager.info("Consultando depósito " + deposito);
            LogManager.debug(sql);
            deposito.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.info("Error consultando el depósito " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo el deposito", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error obteniendo el deposito " + id));
        }
        return deposito;
    }

    public static DepositoVO setDeposito(String despachador, String corte, String importe) throws DetiPOSFault {
        String sql = "INSERT INTO ctdep ( fecha, corte, despachador, peso, total ) VALUES( NOW(), ?, ?, ?, ? )";
        try (Connection conn = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(corte));
            ps.setInt(2, Integer.parseInt(despachador));
            ps.setBigDecimal(3, new BigDecimal(importe));
            ps.setBigDecimal(4, new BigDecimal(importe));
            if( ps.executeUpdate()>0 ) {
                return DepositoDAO.getDepositoById(Integer.parseInt(MySQLDB.lastInsertID(conn)));
            }
            return null;
        } catch(SQLException | DBException ex) {
            LogManager.info(String.format("Error registrando depósito para el despachador %s, corte %s, importe %s", despachador, corte, importe));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error insertando el deposito", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error insertando deposito para el despachador " + despachador));
        }
    }

    /*
    public static DepositoVO setDeposito(String despachador, String corte, String importe) throws DetiPOSFault {
        DepositoVO deposito = DepositoVO.getInstance();
        try {
            deposito.setField(DepositoVO.DEP_FIELDS.corte, corte);
            deposito.setField(DepositoVO.DEP_FIELDS.despachador, despachador);
            deposito.setField(DepositoVO.DEP_FIELDS.peso, importe);
            deposito.setField(DepositoVO.DEP_FIELDS.total, importe);
            LogManager.debug(deposito);
            LogManager.info(String.format("Registrando depósito para el despachador %s, corte %s, importe %s", despachador, corte, importe));
            MySQLDB.insertInAutoKeyedEntity(deposito);
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.info(String.format("Error registrando depósito para el despachador %s, corte %s, importe %s", despachador, corte, importe));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error insertando el deposito", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error insertando deposito para el despachador " + despachador));
        }
        return deposito;
    }
    */
    public static List<DinamicVO<String, String>> getDepositosCorte(String corte, String isla) {
        String sql = "SELECT DISTINCT ctdep.id, ctdep.fecha, ven.alias, ctdep.total, man.isla_pos " 
                    + "FROM ctdep " 
                    + "JOIN (SELECT DISTINCT rm.vendedor, rm.posicion, rm.corte FROM rm WHERE rm.corte = " + corte+ ") vencorte ON vencorte.vendedor = ctdep.despachador " 
                    + "JOIN ven ON ven.id = ctdep.despachador " 
                    + "JOIN man ON vencorte.posicion = man.posicion " 
                    + "WHERE ctdep.corte = " + corte + " "
                    + (StringUtils.isNumber(isla) ? "AND man.isla_pos = " + isla + " " : "")
                    + "ORDER BY man.posicion";
        try {
            LogManager.info("Consultando depósitos del corte " + corte);
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Eonsultando depósitos del corte " + corte);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return new ArrayList<>();
    }
}
