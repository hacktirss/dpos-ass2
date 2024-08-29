/*
 * ConsumosDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ConsumosDAO {

    public static final String SQL_CORTE = "com/detisa/omicrom/sql/SelectConsumosCorte.sql";
    public static final String SQL_SIN_CORTE = "com/detisa/omicrom/sql/SelectConsumosSinCorte.sql";
    public static final String SQL_CONSUMO_DIARIO = "com/detisa/omicrom/sql/ValidateConsumoDiario.sql";
    public static final String SQL_SALDO_TARJETA = "com/detisa/omicrom/sql/SelectSaldoTarjeta.sql";

    public static final String SQL_PRMTR_POSICION = "[$]POSICION";
    public static final String SQL_PRMTR_MANGUERA = "[$]MANGUERA";

    public static final String SQL_PRMTR_TURNO = "[$]TURNO";
    public static final String SQL_PRMTR_CORTE = "[$]CORTE";

    public static final String SQL_PRMTR_CODIGO = "[$]CODIGO";

    public static String getGrandTotal(String rmid) {

        String sql = "SELECT SUM( pesos ) pesos, SUM( TOTAL ) GTOTAL FROM ("
                + "SELECT CAST( pesos AS decimal( 12, 2 ) ) pesos, CAST( pesos AS decimal( 12, 2 ) ) - descuento TOTAL "
                + "FROM rm WHERE id = " + rmid + " " 
                + "UNION ALL SELECT CAST( total AS decimal( 12, 2 ) ) pesos, CAST( total AS decimal( 12, 2 ) ) TOTAL "
                + "FROM vtaditivos WHERE referencia = " + rmid + " ) SUB";
        
        try {
            LogManager.info("Calculando el total del consumo " + rmid);
            LogManager.debug(sql);
            return OmicromSLQHelper.getFirst(sql).NVL("GTOTAL");
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.info("Error calculando el total del consumo " + rmid);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return "";
    }

    public static String getDescuento(String rmid) {

        String sql = "SELECT IF( rm.descuento > 0, 1, 0 ) mostrar, rm.descuento DESCUENTO FROM rm WHERE id = " + rmid;

        try {
            LogManager.info("Recuperando el descuento del consumo " + rmid);
            LogManager.debug(sql);
            DinamicVO<String, String> descuento = OmicromSLQHelper.getFirst(sql);
            return "1".equals(descuento.NVL("mostrar")) ? descuento.NVL("DESCUENTO") : "";
        } catch (DBException | IndexOutOfBoundsException ex) {
            LogManager.info("Error recuperando el descuento del consumo " + rmid);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return "";
    }

    public static boolean applyDescuento(int rmid, BigDecimal descuento) {

        String sql = "UPDATE rm SET descuento = ?, pagoreal = ( pagoreal - ? )  WHERE id  = ?";
        LogManager.debug(sql);
        try (Connection connection = MySQLHelper.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, descuento);
            ps.setBigDecimal(2, descuento);
            ps.setInt(3, rmid);
            return (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            LogManager.error(ex);
            return false;
        }
    }

    public static DinamicVO<String, String> getSaldoTarjeta(String codigo) throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_SALDO_TARJETA).replaceAll(SQL_PRMTR_CODIGO, codigo);
            LogManager.info("Consultando el saldo disponible para el código " + codigo);
            LogManager.debug(sql);
            return OmicromSLQHelper.getUnique(sql);
        } catch (DBException | IOException exc) {
            LogManager.info("Error consultando el consumo disponible para el código " + codigo);
            throw new DetiPOSFault("Error consultando el consumo disponible", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, exc, "Error consultando codigo " + codigo));
        }
    }

    public static String getQueryPeriodo(String periodo) {
        switch (periodo) {
            case "D": return "com/detisa/omicrom/sql/ConsumoCodigoDiario.sql";
            case "S": return "com/detisa/omicrom/sql/ConsumoCodigoSemanal.sql";
            case "Q": return "com/detisa/omicrom/sql/ConsumoCodigoQuincenal.sql";
            case "M": return "com/detisa/omicrom/sql/ConsumoCodigoMensual.sql";
            case "B": return "com/detisa/omicrom/sql/ConsumoCodigoSaldo.sql";
            default: return SQL_CONSUMO_DIARIO;
        }
    }
    public static DinamicVO<String, String> getConsumoDisponible(String codigo, String periodo) throws DetiPOSFault {

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(ConsumosDAO.getQueryPeriodo(periodo)).replaceAll(SQL_PRMTR_CODIGO, codigo);
            LogManager.info("Consultando el consumo disponible para el código " + codigo);
            LogManager.debug(sql);
            return OmicromSLQHelper.getUnique(sql);
        } catch (DBException | IOException exc) {
            LogManager.info("Error consultando el consumo disponible para el código " + codigo);
            throw new DetiPOSFault("Error consultando el consumo disponible", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, exc, "Error consultando codigo " + codigo));
        }
    }

    public static ConsumoVO totalizaCorte(String posicion, String manguera, String turno, String corte) throws DetiPOSFault {

        ConsumoVO totalCorte = new ConsumoVO();
        String sql = "SELECT " +
                     "      M.posicion AS posicion, " +
                     "      M.manguera AS manguera, " +
                     "      M.producto AS producto, " +
                     "      ROUND(IFNULL(SUM(T.volumen), 0), 2) AS volumen, " +
                     "      ROUND(IFNULL(SUM(T.pesos), 0), 2) AS pesos " +
                     "FROM man_pro M " +
                     "LEFT JOIN rm T  " +
                     "      ON T.posicion = M.posicion  " +
                     "      AND T.manguera = M.manguera " +
                     "WHERE T.corte = "+corte+" " +
                     "AND T.posicion = "+posicion+" " +
                     "AND T.manguera = "+manguera+" ";
        try {
            LogManager.info(String.format("Consultando consumos para el corte %s, posicion %s, manguera %s", corte, posicion, manguera));
            LogManager.debug(sql);
            totalCorte = new ConsumoVO(OmicromSLQHelper.getFirst(sql));
        } catch (DBException | IndexOutOfBoundsException | NullPointerException ex) {
            LogManager.info(String.format("Error consultando consumos para el corte %s, posicion %s, manguera %s", corte, posicion, manguera));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando consumos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Consultando corte " + corte));
        }
        return totalCorte;
    }

    public static List <ConsumoVO> getCorte(String posicion, String manguera, String turno, String corte) throws DetiPOSFault {

        List <ConsumoVO> consumosCorte = new ArrayList <> ();
        
        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CORTE)
                    .replaceAll(SQL_PRMTR_POSICION, posicion)
                    .replaceAll(SQL_PRMTR_MANGUERA, manguera)
                    .replaceAll(SQL_PRMTR_TURNO, turno)
                    .replaceAll(SQL_PRMTR_CORTE, corte);
            LogManager.info(String.format("Recuperando consumos del corte %s posicion %s manguera %s", corte, posicion, manguera));
            LogManager.debug(sql);
            for (DinamicVO<String, String> rm : OmicromSLQHelper.executeQuery(sql)) {
                consumosCorte.add(new ConsumoVO(rm));
            }
        } catch (DBException | IOException ex) {
            LogManager.info(String.format("Error ecuperando consumos del corte %s posicion %s manguera %s", corte, posicion, manguera));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error recuperando consumos", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Consultando corte " + corte));
        }
        return consumosCorte;
    }//getCorte

    public static List <ConsumoVO> getSinCorte(String posicion, String manguera, String turno) throws DetiPOSFault {

        List <ConsumoVO> consumosCorte = new ArrayList <> ();
        
        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_SIN_CORTE)
                    .replaceAll(SQL_PRMTR_POSICION, posicion)
                    .replaceAll(SQL_PRMTR_MANGUERA, manguera)
                    .replaceAll(SQL_PRMTR_TURNO, turno);
            LogManager.info(String.format("Recuperando consumos sin corte posicion %s manguera %s", posicion, manguera));
            LogManager.debug(sql);
            consumosCorte = OmicromSLQHelper.executeQuery(sql).stream()
                                .map((DinamicVO<String, String> vo) -> new ConsumoVO(vo)).collect(Collectors.toList());
        } catch (DBException | IOException ex) {
            LogManager.info(String.format("Error recuperando consumos sin corte posicion %s manguera %s", posicion, manguera));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error recuperando consumos sin corte", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Consultando posicion " + posicion));
        }
        return consumosCorte;
    }//getSinCorte

    public static ConsumoVO getByID(int id) {

        ConsumoVO consumo = new ConsumoVO();
        String sql = "SELECT " + CollectionsUtils.fncsEnumAsString(ConsumoVO.RM_FIELDS.class) + " " 
                + "FROM " + ConsumoVO.ENTITY_NAME + " " 
                + "WHERE " + ConsumoVO.RM_FIELDS.id.name() + " = " + id;
        
        try {
            LogManager.info("Recuperando consumos con rm.id " + id);
            LogManager.debug(sql);
            consumo.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error recuperando consumos con rm.id " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }

        return consumo;
    }

    public static ConsumoVO getByIDCXC(int posicion, int idcxc) {

        ConsumoVO consumo = new ConsumoVO();
        String sql = "SELECT " + CollectionsUtils.fncsEnumAsString(ConsumoVO.RM_FIELDS.class) + " " 
                + "FROM " + ConsumoVO.ENTITY_NAME + " " 
                + "WHERE " + ConsumoVO.RM_FIELDS.posicion.name() + " = " + posicion + " AND " + ConsumoVO.RM_FIELDS.idcxc.name() + " = " + idcxc;
        
        try {
            LogManager.info("Recuperando consumos con rm.idcxc " + idcxc);
            LogManager.debug(sql);
            consumo.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException ex) {
            LogManager.info("Error recuperando consumos con rm.idcxc " + idcxc);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }

        return consumo;
    }

    public static List <ConsumoVO> getReconciliationQueue() {
        List <ConsumoVO> queue = new ArrayList <> ();
        ConsumoVO consumo;
        String sql = "SELECT " + CollectionsUtils.fncsEnumAsString(ConsumoVO.RM_FIELDS.class) + " "
                + "FROM " + ConsumoVO.ENTITY_NAME + " "
                + "WHERE informacorporativo = 1";
        
        try {
            LogManager.info("Consultando consumos sin conciliar");
            LogManager.debug(sql);
            for (DinamicVO<String, String> vo : OmicromSLQHelper.executeQuery(sql)) {
                consumo = new ConsumoVO(vo);
                queue.add(consumo);
            }
        } catch (DBException ex) {
            LogManager.info("Error consultando consumos sin conciliar");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }

        return queue;
    }//getReconciliationQueue

    public static boolean reconciliate(String id) {

        String sql = "UPDATE " + ConsumoVO.ENTITY_NAME + " "
                +"SET " + ConsumoVO.RM_FIELDS.informacorporativo + " = 3 "
                +"WHERE " + ConsumoVO.RM_FIELDS.id + " = " + id;
        try {
            LogManager.info("Conciliando consumo con rm.id " + id);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error conciliando consumo con rm.id " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }

    public static List<DinamicVO<String, String>> getVentasPorTipoCliente(String corte, String isla) {
        String sql = 
                "SELECT SUM( importe ) pesos, SUM( descuento ) descuento, SUM( cantidad ) volumen, COUNT( * ) ventas, cli.tipodepago " +
                "FROM (" +
                        "SELECT ROUND( pesos, 2 ) importe, ROUND( descuento, 2 ) descuento, ROUND( volumen, 2 ) cantidad, cliente, posicion " +
                        "FROM rm " +
                        "WHERE rm.corte = " + corte + " AND rm.tipo_venta = 'D' " +
                        "UNION ALL "+
                        "SELECT ROUND( total, 2 ) importe, 0.00 descuento, ROUND( cantidad, 2 ) cantidad, cliente, posicion " +
                        "FROM vtaditivos "+
                        "WHERE vtaditivos.corte = " + corte + " AND vtaditivos.tm = 'C' " +
                ") ventas "  +
                "JOIN cli ON cli.id = ventas.cliente " +
                "JOIN man ON man.posicion = ventas.posicion " + ( StringUtils.isNumber(isla) ? "AND man.isla_pos = " + isla + " " : "" ) +
                "GROUP BY cli.tipodepago";
        try {
            LogManager.info(String.format("Recuperando consumos por tipo de cliente. Corte %s, isla %s", corte, isla));
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error recuperando consumos por tipo de cliente. Corte %s, isla %s", corte, isla));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return new ArrayList<>();
    }
    
    public static List<DinamicVO<String, String>> getDetalleVentasCXC(String corte, String isla) {
        String sql = 
                "SELECT ventas.importe pesos, ventas.descuento, ventas.cantidad volumen, ventas.fechahora, cli.tipodepago " +
                "FROM (" +
                        "SELECT ROUND( pesos, 2 ) importe, ROUND( descuento, 2 ) descuento, ROUND( volumen, 2 ) cantidad, fin_venta fechahora, corte, cliente, posicion " +
                        "FROM rm " +
                        "WHERE rm.tipo_venta = 'D' " +
                        "UNION ALL " +
                        "SELECT ROUND( total, 2 ) importe, 0.00 descuento, ROUND( cantidad, 2 ) cantidad, fecha fechahora, corte, cliente, posicion " +
                        "FROM vtaditivos "+
                        "WHERE vtaditivos.tm = 'C' " +
                ") ventas "  +
                "JOIN cli ON cli.id = ventas.cliente " +
                "JOIN man ON man.posicion = ventas.posicion " + ( StringUtils.isNumber(isla) ? "AND man.isla_pos = " + isla + " " : "" ) +
                "WHERE ventas.corte = " + corte + " AND cli.tipodepago != 'Contado' " +
                "ORDER BY cli.tipodepago, fechahora";
        try {
            LogManager.info(String.format("Recuperando consumos por tipo de cliente. Corte %s, isla %s", corte, isla));
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error recuperando consumos por tipo de cliente. Corte %s, isla %s", corte, isla));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return new ArrayList<>();
    }
    
    public static List<DinamicVO<String, String>> getEfectivo(String corte, String isla) {
        String sql = "SELECT corte, isla_pos, pesos contado, descuento, IFNULL( total, 0 ) deposito, pesos - descuento - IFNULL( total, 0 ) efectivo " +
                            "FROM ( " +
                                    "SELECT corte, isla_pos, SUM( ROUND( pesos, 2 ) ) pesos, SUM( ROUND( descuento, 2 ) ) descuento FROM (" +
                                            "SELECT rm.corte, man.isla_pos, rm.cliente, rm.pesos, rm.descuento " +
                                            "FROM rm " +
                                            "JOIN man ON man.posicion = rm.posicion " +
                                            "WHERE rm.corte =  " + corte + " AND rm.tipo_venta = 'D' " +
                                            "UNION ALL " +
                                            "SELECT vta.corte, man.isla_pos, vta.cliente, vta.total pesos, 0.00 descuento " +
                                            "FROM vtaditivos vta " +
                                            "JOIN man ON man.posicion = vta.posicion " +
                                            "WHERE vta.corte =  " + corte + " AND vta.tm = 'C' " +
                                    ") A " +
                                    "JOIN cli ON cli.id = A.cliente " +
                                    "WHERE cli.tipodepago = 'Contado' " +
                                    ( StringUtils.isNumber(isla) ? "AND A.isla_pos = " + isla + " " : "" ) +
                                    "GROUP BY A.isla_pos " +
                            ") ventas " +
                            "LEFT JOIN ( " +
                                    "SELECT corte, isla_pos, SUM( total ) total FROM ( " +
                                            "SELECT DISTINCT ctdep.id, vencorte.corte, man.isla_pos, ctdep.total " +
                                            "FROM ctdep " +
                                            "JOIN (SELECT DISTINCT rm.vendedor, rm.posicion, rm.corte FROM rm WHERE rm.corte = " + corte + ") vencorte ON vencorte.vendedor = ctdep.despachador " +
                                            "JOIN ven ON ven.id = ctdep.despachador " +
                                            "JOIN man ON vencorte.posicion = man.posicion " +
                                            "WHERE ctdep.corte = " + corte + " " +
                                            (StringUtils.isNumber(isla) ? "AND man.isla_pos = " + isla + " " : "") +
                                            "ORDER BY man.posicion) D GROUP BY corte, isla_pos " +
                            ") depositos USING( corte, isla_pos )";
        try {
            LogManager.info(String.format("Recuperando efectivo por Isla. Corte %s, isla %s", corte, isla));
            LogManager.debug(sql);
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error recuperando consumos por tipo de cliente. Corte %s, isla %s", corte, isla));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return new ArrayList<>();
    }
    
    public static boolean setPagoReal(String id, BigDecimal pago) {
        String sql = "UPDATE rm SET pagoreal = " + pago.toPlainString() + " WHERE id = " + id;
        LogManager.debug(sql);
        try {
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info(String.format("Error actualizando pago real del consumo %s a %s", id, pago.toPlainString()));
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }
}//ConsumosDAO
