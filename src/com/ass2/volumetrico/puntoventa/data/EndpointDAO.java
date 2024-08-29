package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EndpointDAO {

    public static EndpointVO parse(ResultSet rs) throws SQLException {
        EndpointVO endpoint = new EndpointVO();
        endpoint.setId_pac(rs.getInt("id_pac"));
        endpoint.setClave_pac(rs.getString("clave_pac"));
        endpoint.setUrl_webservice(rs.getString("url_webservice"));
        endpoint.setUrl_cancelacion(rs.getString("url_cancelacion"));
        endpoint.setUsuario(rs.getString("usuario"));
        endpoint.setPassword(rs.getString("password"));
        endpoint.setClave_aux(rs.getString("clave_aux"));
        endpoint.setClave_aux2(rs.getString("clave_aux2"));
        endpoint.setActivo(rs.getInt("activo"));
        endpoint.setPruebas(rs.getInt("pruebas"));
        endpoint.setPrioridad(rs.getInt("prioridad"));
        endpoint.setVersion(rs.getString("version"));
        endpoint.setTipo(rs.getString("tipo"));
        return endpoint;
    }

    public static EndpointVO get(String clave, String tipo) throws DetiPOSFault {
        String sql = "SELECT * FROM proveedor_pac WHERE clave_pac = ? AND tipo = ?";
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, clave);
            st.setString(2, tipo);
            LogManager.debug(st.toString());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return parse(rs);
                } else {
                    throw new DetiPOSFault("No está configurado el servidor " + clave);
                }
            }
        } catch (SQLException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }
}
