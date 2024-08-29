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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.mysql.MySQLHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BitacoraIntegracionesDAO {

    private BitacoraIntegracionesDAO() {}

    public static boolean evento(String idintegracion, String account, String accion, Object response) {
        String sql = "INSERT INTO bitacora_integraciones( idintegracion, account, accion, response ) "
                + "VALUES( ?, ?, ?, ? )";
        ObjectMapper mapper = new ObjectMapper();
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idintegracion);
            ps.setString(2, account);
            ps.setString(3, accion);
            ps.setString(4, mapper.writeValueAsString(response));
            LogManager.debug("BitacoraIntegracionesDAO::event " + mapper.writeValueAsString(response));
            return (ps.executeUpdate() > 0);
        } catch (SQLException | JsonProcessingException ex) {
            LogManager.error(ex);
            return false;
        }
    }
}
