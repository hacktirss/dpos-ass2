/*
 * VariablesDAO
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class VariablesDAO {
    
    public static int getIdFAE() {
        
        String sql = "SELECT idfae FROM cia";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("idfae");
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return 0;
    }

    public static String getVariable(String key) {
        
        String sql = "SELECT " + key + " variable FROM variables";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("variable");
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return "";
    }

    public static Properties getSystemDefinition() {
        Properties context = new Properties();

        String sql = "SELECT llave, valor FROM variables_corporativo WHERE IFNULL( llave, '' ) != ''";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            while (rs.next()) {
                LogManager.info(rs.getString("llave") + ":" + rs.getString("valor"));
                if (rs.getString("llave")!=null && rs.getString("valor")!=null) {
                    context.put(rs.getString("llave"), rs.getString("valor"));
                }
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return context;
    }

    public static String getCorporativo(String key) {
        
        String sql = "SELECT valor FROM variables_corporativo WHERE llave = '" + key + "'";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("valor");
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return "";
    }

    public static String getCorporativo(String key, String def) {
        
        String sql = "SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = '" + key + "' ), '" + def + "' ) valor";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                LogManager.debug("variable: " + key + " = " + rs.getString("valor"));
                return rs.getString("valor");
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return "";
    }

    public static boolean isEpsilon() {
        
        String sql = "SELECT UPPER( IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'interfaz' ), 'IOTA' ) ) = 'EPSILON' isEpsilon";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBoolean("isEpsilon");
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return false;
    }

}
