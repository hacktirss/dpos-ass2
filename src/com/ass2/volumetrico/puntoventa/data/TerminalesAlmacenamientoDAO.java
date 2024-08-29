package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TerminalesAlmacenamientoDAO {

    public static TerminalAlmacenamientoVO getTerminalAlmacenamiento(int posicion, int manguera) throws SQLException {
        String sql = 
                    "SELECT " +
                        "man_pro.posicion, man_pro.manguera, tanques.id tanque, me.fechae fecha, " +
                        "me.vol_inicial,  me.vol_final, tanques.volumen_actual, " +
                        "me.incremento vol_descargado, ROUND( me.vol_final - tanques.volumen_actual ) vol_vendido, " +
                        "me.vol_final - tanques.volumen_actual > me.vol_inicial me_last, t.llave terminal, t.permiso permisoCRE " +
                    "FROM man_pro " +
                    "JOIN tanques on tanques.tanque = man_pro.tanque " +
                    "JOIN me ON me.tanque = tanques.tanque " +
                    "JOIN ( SELECT * FROM permisos_cre WHERE catalogo = 'TERMINALES_ALMACENAMIENTO' ) t ON t.id = me.terminal " +
                    "WHERE man_pro.posicion = ? AND man_pro.manguera = ? " +
                    "ORDER BY me.id DESC LIMIT 2";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, posicion);
            ps.setInt(2, manguera);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.first() && rs.getBoolean("me_last")) {
                    return new TerminalAlmacenamientoVO(BaseDAO.getRow(rs));
                }
                if (rs.last()) {
                    return new TerminalAlmacenamientoVO(BaseDAO.getRow(rs));
                }
            }
            throw new SQLException("Error consultando Terminal de Almacenamiento");
        }
    }
}
