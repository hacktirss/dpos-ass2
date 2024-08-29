/*
 * DivisasDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since abr 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLHelper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DivisasDAO {
    
    public static BigDecimal getTipoDeCambio(String divisa) {

        String sql = "SELECT IFNULL( ROUND( tipo_de_cambio, 2 ), 1.0 ) tipo_de_cambio FROM divisas WHERE clave = '" + divisa + "'";
        
        LogManager.info("Consultando tipo de cambio para " + divisa);
        LogManager.debug(sql);
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("tipo_de_cambio");
            }
        } catch (SQLException ex) {
            LogManager.info("Error consultando tipo de cambio para " + divisa);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return BigDecimal.ONE;
    }

    public static DinamicVO<String, String> getDivisa(String divisa) {

        DinamicVO<String, String> div = new DinamicVO<>();
        String sql = "SELECT M.clave, M.descripcion, D.tipo_de_cambio FROM divisas D JOIN cfdi33_c_moneda M ON M.clave = D.clave WHERE D.clave = '" + divisa + "'";

        LogManager.info("Recuperando divisa " + divisa);
        LogManager.debug(sql);
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                Statement ps = connection.createStatement();
                ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {
                div.setField("clave", rs.getString("clave"));
                div.setField("descripcion", rs.getString("descripcion"));
                div.setField("tipo_fe_cambio", rs.getString("tipo_de_cambio"));
            }
        } catch (SQLException ex) {
            LogManager.info("Error recuperando divisa " + divisa);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return div;
    }
}
