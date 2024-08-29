package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SysFileDAO {

    private SysFileDAO() {}

    public static final SysFile get(String key) {
        
        try (Connection conn = BaseDAO.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM sys_files WHERE key_file = ?")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return SysFile.parse(rs);
                }
            }
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return null;
    }
}
