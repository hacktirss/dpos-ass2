package com.ass2.volumetrico.puntoventa.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Data;

@Data
public class SysFile {

    private String keyFile;
    private byte[] file;
    private String descripcion;
    private String format;
    private String extension;
    private String additional;
    
    public static final SysFile parse(ResultSet rs) throws SQLException {
        SysFile sysFile = new SysFile();
        sysFile.setKeyFile(rs.getString("key_file"));
        sysFile.setFile(rs.getBytes("file"));
        sysFile.setDescripcion(rs.getString("descripcion"));
        sysFile.setExtension(rs.getString("extension"));
        sysFile.setAdditional(rs.getString("additional"));
        return sysFile;
    }
}
