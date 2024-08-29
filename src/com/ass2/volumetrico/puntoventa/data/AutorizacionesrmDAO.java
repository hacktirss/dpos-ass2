/*
 * BitacoraDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.mysql.MySQLHelper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class AutorizacionesrmDAO {

    private AutorizacionesrmDAO() {}

    public static boolean evento(int idConsumo, Calendar fecha, BigDecimal monto, String transaccion) {
            String sql = "INSERT INTO rm_transacciones( id, fecha, monto, transaccion ) "
                    + "VALUES( ?, ?, ?, ? )";
            LogManager.debug(sql);
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            ps.setDate(2,new java.sql.Date(fecha.getTimeInMillis()));
            ps.setBigDecimal(3, monto);
            ps.setString(4, transaccion);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }

    public static boolean registered(int idConsumo) {
            String sql = "SELECT COUNT( * ) items FROM rm_transacciones WHERE id = ?";
            LogManager.debug(sql);
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("items") > 0;
                }
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return false;
    }
}
