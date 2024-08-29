/*
 * BancosDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BoletosDAO {

    public static final String SQL_BOLETO = "com/detisa/omicrom/sql/SelectBoleto.sql";
    public static final String SQL_PRMTR_BOLID = "[$]BOLETO";

    private static boolean unique(ResultSet rs) throws SQLException {
        try {
            if (rs.last()) {
                int rows = rs.getRow();
                LogManager.info("Registros " + rows);
                switch (rows) {
                    case 0: throw new SQLException("No se encontró el boleto.");
                    case 1: return true;
                    default: throw new SQLException("Se encontró más de un registro.");
                }
            } else {
                throw new SQLException("No se encontraron registros.");
            }
        } finally {
            rs.beforeFirst();
        }
    }

    public static boolean verifyUniqueClientID(String ids[]) throws DetiPOSFault {

        String sql = "SELECT DISTINCT genbol.cliente CLIENTE " +
                    "FROM boletos " +
                    "JOIN genbol ON genbol.id = boletos.id " +
                    "WHERE codigo IN ( " + new StringBuilder("'").append(CollectionsUtils.fncsArrayAsString(ids, "', '")).append("'").toString() + " )";

        LogManager.info("Validando que los boletos [" + CollectionsUtils.fncsArrayAsString(ids) + "] pertenezcan al mismo cliente");
        LogManager.debug(sql);
        try (Connection conn = MySQLHelper.getInstance().getConnection();
                Statement ps = conn.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            return unique(rs);
        } catch (SQLException ex) {
            LogManager.info("Error validando el ciente de los boletos [" + CollectionsUtils.fncsArrayAsString(ids) + "]");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando el boleto", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Los boletos pertencen a diferentes clientes"));
        }
    }//getBoleto

    public static String getClientID(String ids[]) throws DetiPOSFault {

        String sql = "SELECT DISTINCT genbol.cliente CLIENTE " +
                    "FROM boletos " +
                    "JOIN genbol ON genbol.id = boletos.id " +
                    "WHERE codigo IN ( " + new StringBuilder("'").append(CollectionsUtils.fncsArrayAsString(ids, "', '")).append("'").toString() + " ) LIMIT 1";

        LogManager.info("Validando que los boletos [" + CollectionsUtils.fncsArrayAsString(ids) + "] pertenezcan al mismo cliente");
        LogManager.debug(sql);
        try (Connection conn = MySQLHelper.getInstance().getConnection();
                Statement ps = conn.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.first()) {
                return rs.getString("CLIENTE");
            }
            throw new SQLException("No se encontró el cliente");
        } catch (SQLException ex) {
            LogManager.info("Error validando el ciente de los boletos [" + CollectionsUtils.fncsArrayAsString(ids) + "]");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando el boleto", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "No se recuperó el cliente"));
        }
    }//getBoleto

    public static BaseVO getBoletoByID(String id) throws DetiPOSFault {

        BoletosVO boleto = new BoletosVO();

        LogManager.info("Recuperando información del boleto " + id);
        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_BOLETO)
                            .replaceAll(
                                    SQL_PRMTR_BOLID, 
                                    new StringBuilder("'").append(id).append("'").toString());
            try (Connection conn = MySQLHelper.getInstance().getConnection();
                    Statement stmnt = conn.createStatement();
                    ResultSet rs = stmnt.executeQuery(sql)) {
                LogManager.debug(sql);
                unique(rs);
                boleto.setEntries(BaseDAO.getFetch(rs).get(0));
            }
        } catch (IOException | SQLException ex) {
            LogManager.info("Error consultando el boleto " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando el boleto", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando el boleto " + id));
        }
        return boleto;
    }//getBoleto

    public static List <BoletosVO> getBoletos(String[] codigos) throws DetiPOSFault {

        List <BoletosVO> boletos = new ArrayList <> ();

        LogManager.info("Recuperando boletos [" + CollectionsUtils.fncsArrayAsString(codigos) + "]");
        
        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_BOLETO)
                            .replaceAll(
                                    SQL_PRMTR_BOLID, 
                                    new StringBuilder("'").append(CollectionsUtils.fncsArrayAsString(codigos, "', '")).append("'").toString());
            OmicromSLQHelper.executeQuery(sql).forEach(item ->
                    boletos.add(new BoletosVO(item)));
        } catch (IOException | DBException ex) {
            LogManager.error(ex);
        }
        return boletos;
    }//getBoletos
    
    public static boolean updateBoleto(String idnvo, String cargado, String ticket1, String ticket2, String vigente) {

        LogManager.info(String.format("Actualizando boleto %s Ticket1 %s Ticket2 %s Cargado %s Vigente %s", idnvo, ticket1, ticket2, cargado, vigente));
        if ((StringUtils.isNVL(idnvo) || StringUtils.isNVL(cargado))
                && (StringUtils.isNVL(ticket1) && StringUtils.isNVL(ticket2))){
            return false;
        }

        String sql = "UPDATE boletos SET importecargado = importecargado+? " 
                + ("".equals(ticket1) ? "" : ", ticket  = ?, importe1 = ? ") 
                + ("".equals(ticket2) ? "" : ", ticket2 = ?, importe2 = ? ") 
                + ("".equals(vigente) ? "" : ", vigente = ? ") 
                + "WHERE idnvo = ?";
        int pidx = 1;

        LogManager.info(String.format("Actuallizando boleto %s, cargado %s", idnvo, cargado));
        LogManager.debug(sql);
        try (Connection conn = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(pidx++, new BigDecimal(cargado));
            if (!"".equals(ticket1)) {
                ps.setInt(pidx++, Integer.parseInt(ticket1));
                ps.setBigDecimal(pidx++, new BigDecimal(cargado));
            } 
            if (!"".equals(ticket2)) {
                ps.setInt(pidx++, Integer.parseInt(ticket2));
                ps.setBigDecimal(pidx++, new BigDecimal(cargado));
            }
            if (!"".equals(vigente)) {
                ps.setString(pidx++, vigente);
            }
            ps.setInt(pidx, Integer.parseInt(idnvo));
            return ps.execute();
        } catch (SQLException ex) {
            LogManager.info(String.format("Error actuallizando boleto %s, cargado %s", idnvo, cargado));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}
