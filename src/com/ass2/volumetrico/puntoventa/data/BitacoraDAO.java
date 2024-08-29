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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BitacoraDAO {

    public static boolean evento(String descripcion, String usuario, String ip) {
        String sql = "INSERT INTO bitacora_eventos ( "
                        + "fecha_evento, hora_evento, usuario, "
                        + "tipo_evento, descripcion_evento, ip_evento ) "
                + "VALUES( CURRENT_DATE(), CURRENT_TIME(), ?, 'PIN', ?, ? )";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, descripcion);
            ps.setString(3, ip);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }
}
