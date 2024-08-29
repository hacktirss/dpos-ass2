package com.ass2.volumetrico.puntoventa.common;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLDB;
import static com.softcoatl.database.mysql.MySQLHelper.DB_NAME;
import java.util.List;
import java.util.stream.Collectors;

public class OmicromSLQHelper {

    public static List <DinamicVO<String, String>> executeQuery(String sql) throws DBException {
        return BaseDAO.executeQuery(DB_NAME, sql).stream()
                .map((DinamicVO<String, String> vo) -> new OmicromVO(vo)).collect(Collectors.toList());
    }

    public static List <DinamicVO<String, String>> executeQuery(StringBuffer sql) throws DBException {
        return OmicromSLQHelper.executeQuery(sql.toString());
    }

    public static DinamicVO<String, String> getUnique(String sql) throws DBException {
        return new OmicromVO(MySQLDB.getUniqueRow(sql));
    }

    public static DinamicVO<String, String> getUnique(StringBuffer sql) throws DBException {
        return OmicromSLQHelper.getUnique(sql.toString());
    }

    public static DinamicVO<String, String> getFirst(String sql) throws DBException {
        List <DinamicVO<String, String>> fetch = OmicromSLQHelper.executeQuery(sql);
        return fetch.isEmpty() ? new DinamicVO<>() : fetch.get(0);
    }

    public static DinamicVO<String, String> getFirst(StringBuffer sql) throws DBException {
        return OmicromSLQHelper.getFirst(sql.toString());
    }
    
    public static boolean empty(String sql) throws DBException {
        return OmicromSLQHelper.executeQuery(sql).isEmpty();
    }
    
    public static boolean empty(StringBuffer sql) throws DBException {
        return OmicromSLQHelper.empty(sql.toString());
    }
}
