/*
 * ComandosDAO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2017
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import static com.ass2.volumetrico.puntoventa.data.ComandosVO.*;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.GenericSleeper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ComandosDAO {

    public static final String EXECUTION_ERROR = "2";
    public static final String CANCELLED_PRESET = "Comando Cancelado";
    public static final String RESETTED_PRESET = "Comando Reiniciado";

    private ComandosDAO() {}

    public static ComandosVO create(ComandosVO comando) throws DetiPOSFault {

        try {
            LogManager.info("Creando comando " + comando.NVL(ComandosVO.CMD_FIELDS.comando.name()));
            LogManager.debug(comando);
            BaseDAO.insertAutoKey(MySQLDB.DB_NAME, (BaseVO) comando);
            if (comando.isNVL(CMD_FIELDS.id.name())) {
                throw new DBException("No se creo el registro en comandos");
            }
            LogManager.info("Comando creado con comandos.id = " + comando.NVL(CMD_FIELDS.id.name()));
            return comando;
        } catch (DBException ex) {
            OmicromLogManager.error("Error registrando el comando " + comando, ex);
            throw new DetiPOSFault("Error creando comando", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error registrando el comando " + comando.NVL(ComandosVO.CMD_FIELDS.comando.name())));
        }
    }//create

    public static ComandosVO getComandoPosicion(int posicion) {
        String sql = "SELECT * FROM comandos WHERE posicion = ? AND comando RLIKE '^(V|$)' ORDER BY id DESC LIMIT 1";
        ComandosVO comando = new ComandosVO();

        try (Connection conn = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, posicion);
            LogManager.debug(ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<DinamicVO<String, String>> fetch = BaseDAO.getFetch(rs);
                comando = fetch.isEmpty() ? new ComandosVO() : new ComandosVO(fetch.get(0));
            }
        } catch (SQLException ex) {
            OmicromLogManager.error("Error consultando el comando " + comando, ex);
        }

        return comando;
    }

    public static ComandosVO getComando(String id) {
        StringBuilder sqlBuffer = new StringBuilder ();
        ComandosVO comando = new ComandosVO();

        sqlBuffer.append("SELECT ").append(CollectionsUtils.fncsEnumAsString(CMD_FIELDS.class)).append(" ");
        sqlBuffer.append("FROM ").append(ENTITY_NAME).append(" ");
        sqlBuffer.append("WHERE ").append(CMD_FIELDS.id).append(" = ").append(id);

        try {
            LogManager.debug("Consultando comando " + id);
            comando = new ComandosVO(OmicromSLQHelper.getUnique(sqlBuffer.toString()));
        } catch (DBException ex) {
            OmicromLogManager.error("Error consultando el comando " + comando, ex);
        }

        return comando;
    }//validatePreset

    public static boolean cancelComando(String id) {
        StringBuilder sqlBuffer = new StringBuilder();
        
        sqlBuffer.append("UPDATE ").append(ENTITY_NAME).append(" SET ")
                 .append(CMD_FIELDS.intentos).append(" = 15,")
                 .append(CMD_FIELDS.ejecucion).append(" = 2,")
                 .append(CMD_FIELDS.descripcion).append("='").append(CANCELLED_PRESET).append("',")
                 .append(CMD_FIELDS.fecha_ejecucion).append("=NOW()\n")
                 .append("WHERE ").append(CMD_FIELDS.id).append("=").append(id);
        try {
            LogManager.info("Cancelando comando ");
            LogManager.debug(sqlBuffer.toString());
            return "".equals(id) || MySQLHelper.getInstance().execute(sqlBuffer.toString());
        } catch (DBException ex ){
            OmicromLogManager.error("Error cancelando el comando " + id, ex);
        }
        return false;
    }

    public static boolean resetComando(String id) {
        StringBuilder sqlBuffer = new StringBuilder();
        
        sqlBuffer.append("UPDATE ").append(ENTITY_NAME).append(" SET ")
                 .append(CMD_FIELDS.intentos).append(" = 0,")
                 .append(CMD_FIELDS.ejecucion).append(" = 0,")
                 .append(CMD_FIELDS.replica).append(" = 0,")
                 .append(CMD_FIELDS.descripcion).append("='").append(RESETTED_PRESET).append("',")
                 .append(CMD_FIELDS.fecha_ejecucion).append("=NULL\n")
                 .append("WHERE ").append(CMD_FIELDS.id).append("=").append(id);
        try {
            LogManager.info("Reiniciando comando " + id);
            return MySQLHelper.getInstance().execute(sqlBuffer.toString());
        } catch (DBException ex) {
            OmicromLogManager.error("Ocurrio un eror reiniciando el comando " + id, ex);
        }

        return false;
    }//cancelComando

    public static boolean isExecuted(String id, String intentos, String ejecucion, long waitForComando) {
        DinamicVO<String, String> comando;
        long timeout = Calendar.getInstance().getTimeInMillis()+(waitForComando * DateUtils.MILIS_POR_SEGUNDO);

        do {
            comando = getComando(id);
            if (intentos.equals(comando.NVL(CMD_FIELDS.intentos.name())) && ejecucion.equals(comando.NVL(CMD_FIELDS.ejecucion.name()))) {
                LogManager.info("Comando Establecido " + id);
                return true;
            } else if (EXECUTION_ERROR.equals(comando.NVL(CMD_FIELDS.ejecucion.name()))) {
                LogManager.error("Error en el comando " + id + " " + comando.NVL(CMD_FIELDS.intentos.name()));
                return false;
            }
            GenericSleeper.get().sleep();
        } while (timeout>Calendar.getInstance().getTimeInMillis());
        LogManager.info("Comando vencido " + id);

        return false;
    }//validatePreset

    public static List <ComandosVO> getErrorQueue() {

        List <ComandosVO> queue = new ArrayList <> ();
        String sql = "SELECT " + CollectionsUtils.fncsEnumAsString(ComandosVO.CMD_FIELDS.class) + " " 
                + "FROM " + ComandosVO.ENTITY_NAME + " " 
                + "WHERE (comando like '$%' OR comando like 'V%') " 
                + "AND ejecucion = 2 " 
                + "AND idtarea IS NOT NULL " 
                + "AND idtarea != 0 AND idtarea != '0' ";

        try {
            LogManager.info("Consultando errores");
            LogManager.debug(sql);
            queue = OmicromSLQHelper.executeQuery(sql).stream()
                        .map((DinamicVO<String, String> vo) -> new ComandosVO(vo))
                        .collect(Collectors.toList());
        } catch (DBException ex) {
            OmicromLogManager.error("Error consultando errores", ex);
        }
        return queue;
    }//getReconciliationQueue

    public static boolean clearComando(String id) {

        String sql = "UPDATE comandos SET " + CMD_FIELDS.idtarea + " = 0 WHERE " + CMD_FIELDS.id + " = " + id;
        try {
            LogManager.info("Limpiando comando " + id);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            OmicromLogManager.error("Error limpiando comando " + id, ex);
        }

        return false;
    }//cancelComando
}
