/*
 * InventarioDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.database.mysql.MySQLHelper;
import java.io.IOException;
import java.security.InvalidParameterException;

public class InventarioDAO {

    public static final String SQL_SELECT_ADITIVO = "com/detisa/omicrom/sql/SelectAditivo.sql";
    public static final String SQL_SELECT_ADITIVO_CODIGO = "com/detisa/omicrom/sql/SelectAditivoCodigo.sql";

    public static final String SQL_ISLA = "[$]ISLA";
    public static final String SQL_ADITIVO = "[$]ADITIVO";
    public static final String SQL_CODIGO = "[$]CODIGO";
    public static final String SQL_POSICION = "[$]POSICION";


    private InventarioDAO() {}

    public static BaseVO getInventarioByIsla(String clave, String posicion) throws DetiPOSFault {

        BaseVO inventario = new InventarioVO();

        try {
            if (StringUtils.isNVL(clave)) {
                throw new InvalidParameterException("Error de parametros. Se esperaba el valor CLAVE");                
            }
            if (StringUtils.isNVL(posicion)) {
                throw new InvalidParameterException("Error de parametros. Se esperaba el valor POSICION");
            }

            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_SELECT_ADITIVO)
                    .replaceAll(SQL_POSICION, posicion)
                    .replaceAll(SQL_ADITIVO, clave);
            LogManager.info(String.format("Recuperando inventario para %s en la posición %s", clave, posicion));
            LogManager.debug(sql);
            inventario.setEntries(OmicromSLQHelper.getFirst(sql));
            if (inventario.isVoid()) {
                throw new DBException(String.format("No se encontro el producto %s en la isla %s", clave, posicion));
            }
            LogManager.info("Inventario actual " + inventario);
        } catch (IndexOutOfBoundsException | InvalidParameterException | IOException | DBException ex) {
            OmicromLogManager.error(String.format("Error recuperando inventario para %s en la posición %s", clave, posicion), ex);
        }
        return inventario;
    }//queryAditivo

    public static BaseVO getInventarioByCodigoIsla(String codigo, String posicion) throws DetiPOSFault {

        BaseVO inventario = new InventarioVO();

        try {
            if (StringUtils.isNVL(codigo)) {
                throw new InvalidParameterException("Error de parametros. Se esperaba el valor CODIGO");                
            }
            if (StringUtils.isNVL(posicion)) {
                throw new InvalidParameterException("Error de parametros. Se esperaba el valor POSICION");
            }
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_SELECT_ADITIVO_CODIGO)
                    .replaceAll(SQL_POSICION, posicion)
                    .replaceAll(SQL_CODIGO, codigo);
            LogManager.info(String.format("Consultando inventario para %s en la isla %s", codigo, posicion));
            LogManager.debug(sql);
            inventario.setEntries(OmicromSLQHelper.getFirst(sql));
            if (inventario.isVoid()) {
                throw new DBException(String.format("No se encontro el producto %s en la isla %s", codigo, posicion));
            }
            LogManager.info("Inventario actual " + inventario);
        } catch (IndexOutOfBoundsException | InvalidParameterException | IOException | DBException ex) {
            OmicromLogManager.error(String.format("Error recuperando inventario para %s en la posición %s", codigo, posicion), ex);
        }
        return inventario;
    }//queryAditivo

    public static boolean updateInventario(String clave, String posicion, String cantidad) throws DetiPOSFault {
        String sql = "UPDATE invd JOIN man ON man.isla_pos = invd.isla_pos "
                    + "SET modificacion = NOW(), existencia = existencia - " + cantidad + " "
                    + "WHERE man.posicion = " + posicion + " AND invd.id = " + clave;

        try {
            LogManager.info(String.format("Actualizando inventario para %s en la posicion %s, cantidad %s", clave, posicion, cantidad));
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException exc) {
            LogManager.info(String.format("Error actualizando inventario para %s en la posicion %s, cantidad %s", clave, posicion, cantidad));
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, exc, "Error insertando venta de aditivo"));
        }
    }//updateInventario
}//InventarioDAO
