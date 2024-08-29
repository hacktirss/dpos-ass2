/*
 * VendedoresDAO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since ago 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import java.util.List;

public class VendedoresDAO extends BaseDAO {
    
    public static VendedoresVO getNameByNIPI(String nip, String posicion) throws DBException {
        String sql = "SELECT " +
                "IFNULL( ven.nombre, venp.nombre ) nombre, " +
                "IFNULL( ven.id, venp.id ) id, IFNULL( ven.num_empleado, venp.num_empleado ) employee, " +
                "man.posicion, v.fix, " +
                    "CASE " +
                        "WHEN variables.nipticket = 'Si' THEN IF(  ven.nip IS NOT NULL AND ven.nip <> '', ven.nip, 'INCORRECTO' ) " +
                        "ELSE 'NO PIDE' " +
                    "END AS nip " +
                "FROM variables " +
                "JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'fixed_posicion_ven' ), '0' ) = '1' fix ) v ON TRUE " +
                "JOIN man ON man.posicion = " + posicion + " " +
                "JOIN ven venp ON man.despachador = venp.id " +
                "LEFT JOIN ven ON ven.nip = '" + nip + "' AND ven.activo = 'Si'";
        LogManager.debug(sql);
        return new VendedoresVO(OmicromSLQHelper.getUnique(sql.toString()));
    }//getNameByNIPI

    public static DinamicVO<String, String> getNameByNIP(String nip) throws DBException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT IFNULL( nombre, '' ) as NOMBRE, ")
                .append("IFNULL( ven.id, -1 ) as ID, ")
                .append("IFNULL( ven.nip, 'INCORRECTO' ) AS nip ")
                .append("FROM variables ")
                .append("LEFT JOIN ven ON ven.nip = '").append(nip).append("'");

        return OmicromSLQHelper.getUnique(sql.toString());
    }//getNameByNip

    public static DinamicVO<String, String> get(String id) throws DBException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT IFNULL( nombre, 'NO EXISTE' ) as NOMBRE, ")
                .append("CASE ")
                .append("WHEN variables.nipticket = 'Si' THEN IFNULL( ven.nip, 'NO DEFINIDO' ) ")
                .append("ELSE 'NO PIDE' END AS NIP ")
                .append("FROM variables ")
                .append("LEFT JOIN ven ON ven.id = ").append(Integer.valueOf(id));
        LogManager.debug(sql);
        return OmicromSLQHelper.getUnique(sql.toString());
    }

    public static DinamicVO<String, String> getByNIP(String nip) throws DBException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ")
                .append("FROM ven ")
                .append("WHERE nip = '").append(nip).append("' ")
                .append("AND activo = 'Si'");
        LogManager.debug(sql);
        return OmicromSLQHelper.getUnique(sql.toString());
    }//getByNip

    public static boolean existsEmployee(String employee) throws DBException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ")
                .append("FROM ven ")
                .append("WHERE num_empleado = '").append(employee).append("' ")
                .append("AND activo = 'Si'");
        LogManager.debug(sql);
        return !OmicromSLQHelper.getUnique(sql.toString()).isNVL("id");
    }//getByNip

    public static DinamicVO<String, String> getByAlias(String alias) throws DBException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ")
                .append("FROM ven ")
                .append("WHERE alias = '").append(alias).append("' ");
        LogManager.debug(sql);
        return OmicromSLQHelper.getUnique(sql.toString());
    }//getByAlias

    @Override
    public boolean create(BaseVO poNew) throws DBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DinamicVO<String, String> retrieve(BaseVO poCondition) throws DBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DinamicVO> retrieveAll(BaseVO poCondition) throws DBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(BaseVO poValues, BaseVO poCondition) throws DBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(BaseVO poCondition) throws DBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
