/*
 * ImpresionesDAO
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
import java.sql.Types;

public abstract class ImpresionesDAO {

    public static boolean evento(int rm, int pos, int ven) {
        String sql = "INSERT INTO impresiones_pos ( id_rm, id_pos, id_ven )"
                + "VALUES( ?, ?, ? )";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rm);
            ps.setInt(2, pos);
            if (ven > 0) {
                ps.setInt(3, ven);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }
}
