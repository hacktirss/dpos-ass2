package com.ass2.volumetrico.puntoventa.data;

/*
 * CuentasPorCobrarDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since oct 2021
 */
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;

public class CuentasPorCobrarDAO {

    public static boolean insertAditivo(String id) throws DBException {
        String sql = "INSERT INTO cxc (cliente, referencia, fecha, hora, concepto, tm, cantidad, importe, corte, producto) " +
                "SELECT cliente, id, DATE( fecha ), TIME( fecha ), descripcion, 'C', cantidad, total, corte, 'A' FROM vtaditivos WHERE id = " + id;
        LogManager.debug(sql);
        return MySQLHelper.getInstance().execute(sql);
    }

    public static boolean insertDespacho(String id) throws DBException {
        String sql = "INSERT INTO cxc (cliente, referencia, fecha, hora, concepto, tm, cantidad, importe, corte, producto) " +
                "SELECT cliente, id, DATE( fin_venta ), TIME( fin_venta ), 'Compra de combustible', 'C', volumen, pesos, corte, producto FROM rm WHERE id = " + id;
        LogManager.debug(sql);
        return MySQLHelper.getInstance().execute(sql);
    }

    public static boolean cancelConsumo(String id) throws DBException {
        String sql = "UPDATE cxc SET cliente = -cliente, referencia = -referencia, corte = -corte, factura = IF( factura IS NULL, factura, -factura ) "
                + "WHERE producto = 'C' AND referencia = " + id;
        LogManager.debug(sql);
        return MySQLHelper.getInstance().execute(sql);
    }

}
