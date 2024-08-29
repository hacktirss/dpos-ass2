package com.detisa.omicrom.integraciones.monederos.omicrom;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PuntosOmicromApi {
    
    private PuntosOmicromApi() {}

        public static DinamicVO<String, String> getPuntosConsumo(int idrm) throws DetiPOSFault {

        try {
            String sql = "{call get_points_for_cli(?)}";
            LogManager.info("Consultando los puntos para el consumo " + idrm);
            try (Connection connection = MySQLHelper.getInstance().getConnection();
                    CallableStatement ps = connection.prepareCall(sql)) {
                ps.setInt(1, idrm);
                ps.execute();
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT @PuntosTicket, @Totalpuntos, @TotalAntesDeVenta")) {
                    if (rs.first()) {
                        LogManager.info(rs);
                        return BaseDAO.getRow(rs);
                    }
                }
            }
            throw new SQLException("No hay información");
        } catch (SQLException exc) {
            LogManager.debug("Error puntos: ", exc);
            LogManager.info("Error consultando los puntos disponibles para el consumo " + idrm);
            throw new DetiPOSFault("Error consultando puntos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, exc, "Error consultando los puntos disponibles para el consumo " + idrm));
        }
    }

    public static String getTipoMonedero(int idrm, String accion, String account) throws DetiPOSFault {

        try {
            String sql = "{call tipo_monedero(?, ?, ?)}";
            LogManager.info("Consultando los puntos para el consumo " + idrm);
            try (Connection connection = MySQLHelper.getInstance().getConnection();
                    CallableStatement ps = connection.prepareCall(sql)) {
                ps.setInt(1, idrm);
                ps.setString(2, accion);
                ps.setString(3, account);
                ps.execute();
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT @Msj")) {
                    if (rs.first()) {
                        return rs.getString("@Msj");
                    }
                }
            }
            throw new SQLException("No hay información");
        } catch (SQLException exc) {
            LogManager.debug("Error puntos: ", exc);
            LogManager.info("Error consultando los puntos disponibles para el consumo " + idrm);
            throw new DetiPOSFault("Error consultando puntos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, exc, "Error consultando los puntos disponibles para el consumo " + idrm));
        }
    }

}
