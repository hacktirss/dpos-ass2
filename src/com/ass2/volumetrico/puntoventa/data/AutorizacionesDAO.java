/*
 * AutorizacionesDAO
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
import java.sql.Statement;

public class AutorizacionesDAO {

    public static int autorizacion(int cliente, String codigo, String tipo, ComandosVO comando, BigDecimal solicitado, BigDecimal autorizado, String detail) {
        String sql = "INSERT INTO bitacora_autorizaciones ( "
                        + "id_cliente, codigo, tipo, solicitado, autorizado, comando, id_comando, status, detail ) "
                + "VALUES( ?, ?, ?, ?, ?, ?, ?, 0, ? )";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cliente);
            ps.setString(2, codigo);
            ps.setString(3, tipo);
            ps.setBigDecimal(4, solicitado);
            ps.setBigDecimal(5, autorizado);
            ps.setString(6, comando.NVL("comando"));
            ps.setInt(7, comando.getCampoAsInt("id"));
            ps.setString(8, detail);
            ps.executeUpdate();
            LogManager.debug(ps);
            try (ResultSet idrs = ps.getGeneratedKeys()) {
                return idrs.next() ? idrs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
            return 0;
        }
    }

    public static boolean rechazo(int cliente, String codigo, String tipo, BigDecimal solicitado, String motivo) {
        String sql = "INSERT INTO bitacora_autorizaciones ( "
                        + "id_cliente, codigo, tipo, solicitado, status, detail ) "
                + "VALUES( ?, ?, ?, ?, 2, ? )";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cliente);
            ps.setString(2, codigo);
            ps.setString(3, tipo);
            ps.setBigDecimal(4, solicitado);
            ps.setString(5, motivo);
            LogManager.debug(ps);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }

    public static boolean saldos(int rmid, BigDecimal saldo, BigDecimal despachado, int id) {
        String sql = "INSERT INTO unidades_log( noPago, importeAnt, importe, importeDelPago, idUnidad, usr ) VALUES ( ?, ?, ?, ?, ?, 'DPOS' )";
        BigDecimal importe = saldo.subtract(despachado);
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rmid);
            ps.setBigDecimal(2, saldo);
            ps.setBigDecimal(3, importe);
            ps.setBigDecimal(4, despachado.negate());
            ps.setInt(5, id);
            LogManager.debug("Actualizando saldo de la unidad " + id);
            LogManager.debug(ps);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }

    public static boolean ejecutado(int rm, BigDecimal despachado, int id) {
        String sql = "UPDATE bitacora_autorizaciones SET id_consumo = ?, despachado = ?, status = 1 WHERE id_comando = ?";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rm);
            ps.setBigDecimal(2, despachado);
            ps.setInt(3, id);
            LogManager.debug(ps);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }

    public static boolean vencido(int id) {
        String sql = "UPDATE bitacora_autorizaciones SET despachado = 0, status = 2, detail = CONCAT( detail, '. Comando vencido' ) WHERE id_comando = ?";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            LogManager.debug(ps);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }
}
