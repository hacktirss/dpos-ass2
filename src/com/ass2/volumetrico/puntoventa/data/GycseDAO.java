/*
 * GycseDAO
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2016
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GycseDAO {

    public static List <DinamicVO<String, String>> getServicios(String codigo) throws DetiPOSFault {

        String sql = "SELECT M.servicio, M.producto, M.nombre, M.descripcion " 
                + "FROM gycse_productos M " 
                + "WHERE M.active = 'S' " 
                + "AND M.display = 'S' " 
                + "AND M.codigo = '" + codigo + "' " 
                + "ORDER BY M.servicio, M.producto";
        try {
            LogManager.info("Consultando Servicios del Operador " + codigo);
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Consultando Operadores de Servicios");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando Servicios", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando Servicios del Operador " + codigo));
        }
    }

    public static List <DinamicVO<String, String>> getMontos(String codigo) throws DetiPOSFault {

        String sql = "SELECT M.importe " 
                + "FROM gycse_montos M " 
                + "WHERE M.active = 'S' " 
                + "AND M.display = 'S' " 
                + "AND M.codigo = '" + codigo + "' " 
                + "ORDER BY M.importe";
        try {
            LogManager.info("Consultando montos del operador " + codigo);
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Error consultando montos del operador " + codigo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando Montos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando Montos del operador " + codigo), ex);
        }
    }

    public static List <DinamicVO<String, String>> getOperadoresTA() throws DetiPOSFault {

        String sql = "SELECT O.nombre, O.descripcion, O.codigo, IFNULL(S.registros, 0) size "
                + "FROM gycse_operadores O " 
                + "LEFT JOIN ( " 
                + "   SELECT COUNT(*) registros, M.codigo " 
                + "   FROM gycse_montos M " 
                + "   WHERE M.active = 'S' " 
                + "   AND M.display = 'S' " 
                + "   GROUP BY M.codigo " 
                + ") S ON S.codigo = O.codigo " 
                + "WHERE O.tipo = 'R' " 
                + "AND O.active = 'S' " 
                + "AND O.display = 'S' " 
                + "ORDER BY O.nombre";
        try {
            LogManager.info("Consultando Operadores de TA");
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Error consultando Operadores de TA");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando TA", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando Operadores de TA"));
        }
    }

    public static List <DinamicVO<String, String>> getOperadoresServicios() throws DetiPOSFault {

        String sql = "SELECT O.nombre, O.descripcion, O.codigo, IFNULL(S.registros, 0) size " 
                    + "FROM gycse_operadores O " 
                    + "LEFT JOIN ( " 
                    + "   SELECT COUNT(*) registros, M.codigo " 
                    + "   FROM gycse_productos M " 
                    + "   WHERE M.active = 'S' " 
                    + "   AND M.display = 'S' " 
                    + "   GROUP BY M.codigo " 
                    + ") S ON S.codigo = O.codigo " 
                    + "WHERE O.tipo = 'S' " 
                    + "AND O.active = 'S' " 
                    + "AND O.display = 'S' " 
                    + "ORDER BY O.nombre";
        try {
            LogManager.info("Consultando Operadores de Servicios");
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info("Error consultando Operadores de Servicios");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando Servicios", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando Operadores de Servicios"));
        }
    }
    public static void updateOperacion(GycseOperacion operacion) throws DetiPOSFault {
    
        try (Connection connection = MySQLHelper.getInstance().getConnection()) {
            LogManager.info("Actualizando operación");
            LogManager.debug(operacion);
            BaseDAO.update(connection, operacion);
        } catch (SQLException | DBException ex) {
            LogManager.info("Actualizando operación");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    public static boolean updateStatusOperacion(String id, String status) throws DetiPOSFault {

        String sql = "UPDATE " + GycseOperacion.ENTITY_NAME + " "
                    + "SET " + GycseOperacion.FIELDS.status + " = '" + status + "' "
                    + "WHERE " + GycseOperacion.FIELDS.id + " = " + id;
        try {
            LogManager.info("Actualizando estatus de la operación " + status);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando estatus de la operación " + status);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    public static GycseOperacion getOperacion(String id) throws DetiPOSFault {

        String sql = "SELECT " + GycseOperacion.MAPPING.getFieldString() + " " +
                    "FROM " + GycseOperacion.MAPPING.getEntity() + " " +
                    "WHERE " + GycseOperacion.FIELDS.id + " = " + id;
        try {
            LogManager.info("Consultando Operación " + id);
            LogManager.debug(sql);
            return new GycseOperacion(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error consultando Operación " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    public static GycseOperacion getRecarga(String id) throws DetiPOSFault {

        String sql = "SELECT A.*, B.nombre, B.descripcion " 
                    + "FROM gycse_operaciones A " 
                    +"JOIN gycse_operadores B ON A.operador = B.codigo " 
                    + "WHERE " + GycseOperacion.FIELDS.id + " = " + id;
        try {
            LogManager.info("Consultando recarga " + id);
            LogManager.debug(sql);
            return new GycseOperacion(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error consultando recarga " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    public static GycseOperacion getServicio(String id) throws DetiPOSFault {

        String sql = "SELECT A.*, B.nombre, C.descripcion "
                    + "FROM gycse_operaciones A "
                    + "JOIN gycse_productos C ON C.servicio = A.servicio AND  C.producto = A.producto "
                    + "JOIN gycse_operadores B ON C.servicio = B.codigo "
                    + "WHERE " + GycseOperacion.FIELDS.id + " = " + id;
        try {
            LogManager.info("Consultando Servicio " + id);
            LogManager.debug(sql);
            return new GycseOperacion(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error consultando Servicio " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }
    public static String getPeticion(String id) throws DBException {

        String sql = "SELECT peticion "
                    + "FROM gycse_operaciones "
                    + "WHERE " + GycseOperacion.FIELDS.id + " = " + id;
        LogManager.info("Consultando petición " + id);
        LogManager.debug(sql);
        return OmicromSLQHelper.getUnique(sql).NVL("peticion");
    }

    public static List <DinamicVO<String, String>> getOperacionesAutorizadas() throws DBException {

        String sql = 
                    "SELECT "
                    +    "G.id, "
                    +    "LPAD( SUBSTRING( C.numestacion, 2 ), 8, '0' ) estacion, "
                    +    "G.peticion, "
                    +    "CONCAT( "
                    +        "DATE_FORMAT( G.fecha_envio, '%Y%m%d%H%i%s' ), "
                    +        "CASE WHEN G.tipo = 'R' THEN '001' ELSE '162' END, "
                    +        "LPAD( G.autorizacion, 18, '0' ), "
                    +        "G.numero, "
                    +        "'BIIS', "
                    +        "LPAD( ROUND( G.importe*100, 0 ), 6, '0' ), "
                    +        "G.peticion, "
                    +        "LPAD( G.codigo_respuesta, 2, '0' ), "
                    +        "LPAD( G.transaccion, 8, '0' ), "
                    +        "LPAD( CASE WHEN G.tipo='R' THEN G.operador ELSE '22' END, 2, '0' ), "
                    +        "LPAD( G.servicio, 4, '0'), "
                    +        "LPAD( G.producto, 4, '0'), "
                    +        "LPAD( G.captura, 40, '0') "
                    +   ") registro, "
                    +    "DATE_FORMAT( G.fecha_envio, '%d/%m/%Y %H:%i:%s' ) fecha ,"
                    +    "ROUND(G.importe*100, 0) importe, "
                    +    "ROUND(G.comision, 2) comision, "
                    +    "ROUND(G.comision_e, 2) comision_e, "
                    +    "ROUND(G.comision_d, 2) comision_d, "
                    +    "ROUND(G.comision_m, 2) comision_m "
                    + "FROM gycse_operaciones G, cia C "
                    + "WHERE G.codigo_respuesta = '00' "
                    + "AND G.status= 'A' "
                    + "AND G.conciliacion= 'N' "
                    + "AND G.maqueta = 0";

        LogManager.info("Consultando operaciones autorizadas");
        LogManager.debug(sql);
        return OmicromSLQHelper.executeQuery(sql);
    }
    public static List <DinamicVO<String, String>> getOperacionesSinConciliar() throws DBException {
        String sql = 
                    "SELECT "
                    +    "LPAD( SUBSTRING( C.numestacion, 2 ), 8, '0' ) estacion, "
                    +    "peticion, "
                    +    "CONCAT( "
                    +        "DATE_FORMAT( fecha_envio, '%Y%m%d%H%i%s' ), "
                    +        "CASE WHEN tipo = 'R' THEN '01' ELSE '00' END, "
                    +        "LPAD( autorizacion, 18, '0' ), "
                    +        "numero, "
                    +        "'BIIS', "
                    +        "LPAD( ROUND( importe*100, 0 ), 6, '0' ), "
                    +        "peticion, "
                    +        "LPAD( codigo_respuesta, 2, '0' ), "
                    +        "LPAD( transaccion, 8, '0' ), "
                    +        "operador, "
                    +        "servicio, "
                    +        "producto, "
                    +        "captura "
                    +    ") registro, "
                    +    "DATE_FORMAT( fecha_envio, '%Y%m%d %H%i%s' ) fecha , "
                    +    "ROUND( importe*100, 0 ) importe "
                    + "FROM `gycse_operaciones`, cia C "
                    + "WHERE codigo_respuesta = 0 "
                    + "AND DATE( fecha_envio ) = ADDDATE( DATE( NOW() ), -1 ) "
                    + "AND status = 'S'";

        LogManager.info("Consultando operaciones sin conciliar");
        LogManager.debug(sql);
        return OmicromSLQHelper.executeQuery(sql);
    }
}
