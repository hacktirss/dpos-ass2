/**
 * CorteDetalleDAO
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since Dec 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import static com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.ENTITY_NAME;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.CDT_FIELDS;
import static com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.CFACTOR;
import static com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.OVERFLOW_BENNETT;
import static com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.OVERFLOW_EXCEPTION;
import static com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.OVERFLOW_GENERIC;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CorteDetalleDAO {

    private CorteDetalleDAO() {}

    public static boolean newDetalle(CorteDetalleVO detalle) {

        LogManager.info("Insertando detalle " + detalle);
        try {
            detalle.setField(CDT_FIELDS.idnvo, MySQLDB.next(ENTITY_NAME, CDT_FIELDS.idnvo.name()));
            return MySQLHelper.getInstance().insert(detalle);
        } catch(DBException ex) {
            LogManager.info("Error insertando detalle" + detalle);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }

    public static boolean updateMontoFinalDetalle(CorteDetalleVO detalle) throws DBException {

        String sql = "UPDATE " + ENTITY_NAME + " "
                + "SET "
                + CDT_FIELDS.fmonto1 + " = " + detalle.getFmonto1() + ", "
                + CDT_FIELDS.fmonto2 + " = " + detalle.getFmonto2() + ", "
                + CDT_FIELDS.fmonto3 + " = " + detalle.getFmonto3() + " "
                + "WHERE " + CDT_FIELDS.id + " = " + detalle.getId() + " "
                + "AND " + CDT_FIELDS.idnvo + " = " + detalle.getIdnvo() + " "
                + "AND " + CDT_FIELDS.posicion + " = " + detalle.getPosicion();

        LogManager.info(
                String.format("Actualizando monto final ctd.id = %s, ctd.idnvo %s", 
                        detalle.NVL(CorteDetalleVO.CDT_FIELDS.id.name()), 
                        detalle.NVL(CorteDetalleVO.CDT_FIELDS.idnvo.name())));
        LogManager.debug(sql);
        return MySQLHelper.getInstance().execute(sql);
    }//updateMontoFinalDetalle

    public static boolean createCTDTotalizador(String idTarea, String id) {

        String sql = 
                    "INSERT INTO ctd (id, posicion, "
                +       "imonto1, fmonto1, ivolumen1, fvolumen1, "
                +       "imonto2, fmonto2, ivolumen2, fvolumen2, "
                +       "imonto3, fmonto3, ivolumen3, fvolumen3 ) "
                +       "SELECT " + id + ", posicion, "
                +              "monto1, monto1, volumen1, volumen1, "
                +           "monto2, monto2, volumen2, volumen2, "
                +           "monto3, monto3, volumen3, volumen3  "
                +       "FROM totalizadores "
                +       "WHERE idtarea = " + idTarea;

        try {
            LogManager.info("Creando ctd desde el Totalizador " + id);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error creando ctd desde el Totalizador " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return false;
    }

    public static List <CorteDetalleVO> getDetallesCorte(String corteID) throws DetiPOSFault {

        List <CorteDetalleVO> detallesCorte = new ArrayList <> ();
        String sql = "SELECT * "
                + "FROM ctd "
                + "JOIN ( SELECT id, posicion, MAX( idnvo ) idnvo FROM ctd GROUP BY id, posicion ) aux USING( id, posicion, idnvo ) "
                + "WHERE id = " + corteID + " ORDER BY posicion";

        try {
            LogManager.info("Consultando detalles del corte " + corteID);
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(vo -> detallesCorte.add(new CorteDetalleVO(vo)));
        } catch (DBException ex) {
            LogManager.info("Error consultando detalles del corte " + corteID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando detalles", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Consultando corte " + corteID));
        }

        return detallesCorte;
    }

    public static List <CorteDetalleVO> getDetallesZeroCorte(String isla) throws DetiPOSFault {

        List <CorteDetalleVO> detallesCorte = new ArrayList <> ();
        String sql = "SELECT 0 id, man.posicion idnvo, man.posicion, "
                + "0 imonto1, 0 imonto2, 0 imonto3, 0 ivolumen1, 0 ivolumen2, 0 ivolumen3, "
                + "0 fmonto1, 0 fmonto2, 0 fmonto3, 0 fvolumen1, 0 fvolumen2, 0 fvolumen3 "
                + "FROM man JOIN estado_posiciones ON man.posicion = estado_posiciones.posicion "
                + "WHERE man.activo = 'Si' AND man.isla = " + isla + "  AND estado_posiciones.estado <> '-'";

        try {
            LogManager.info("Creando detalles corte en ceros");
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(vo -> detallesCorte.add(new CorteDetalleVO(vo)));
        } catch (DBException ex) {
            LogManager.info("Error creando detalles core en ceros");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo los detalles", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando creando corte en ceros"));
        }

        return detallesCorte;
    }

    public static List <DinamicVO<String, String>> getDetallesCorteActives(String corteID, String isla) throws DetiPOSFault {
        List <DinamicVO<String, String>> detallesCorte = new ArrayList <> ();
        String sql = "SELECT ctd.*, com.descripcion, v.dispensarios, IFNULL( rm.importe, 0.00 ) consumo, IFNULL( rm.descuento, 0.00 ) descuento FROM ( "
                        + "SELECT id, idnvo, posicion, '1' as manguera, "
                        +       "ROUND(imonto1, 2) as imonto, ROUND(fmonto1, 2) as fmonto, "
                        +       "ROUND(ivolumen1, 2) as ivolumen, ROUND(fvolumen1, 2) as fvolumen "
                        + "FROM ctd WHERE id = " + corteID + " "
                        + "UNION ALL SELECT id, idnvo, posicion, '2' as manguera, "
                        +       "ROUND(imonto2, 2) as imonto, ROUND(fmonto2, 2) as fmonto, "
                        +       "ROUND(ivolumen2, 2) as ivolumen, ROUND(fvolumen2, 2) as fvolumen "
                        + "FROM ctd WHERE id = " + corteID + " "
                        + "UNION ALL SELECT id, idnvo, posicion, '3' as manguera, "
                        +       "ROUND(imonto3, 2) as imonto, ROUND(fmonto3, 2) as fmonto, "
                        +       "ROUND(ivolumen3, 2) as ivolumen, ROUND(fvolumen3, 2) as fvolumen "
                        + "FROM ctd WHERE id = " + corteID + " ) ctd "
                        + "JOIN ( SELECT id, posicion, MAX( idnvo ) idnvo FROM ctd GROUP BY id, posicion ) aux USING( id, posicion, idnvo ) "
                        + "JOIN man ON ctd.posicion = man.posicion " + ( StringUtils.isNumber(isla) ? "AND man.isla_pos = " + isla + " " : " " ) 
                        + "LEFT JOIN ( SELECT posicion, corte, manguera, ROUND( SUM( pesos ), 2 ) importe, ROUND( SUM( descuento ), 2 ) descuento FROM rm GROUP BY corte, posicion, manguera ) rm ON man.posicion = rm.posicion AND rm.corte = ctd.id AND ctd.manguera = rm.manguera "
                        + "JOIN man_pro ON ctd.posicion = man_pro.posicion AND ctd.manguera = man_pro.manguera "
                        + "JOIN com ON com.clavei = man_pro.producto " 
                        + "JOIN variables v ON TRUE "
                        + "WHERE man.activo = 'Si' AND man_pro.activo = 'Si' AND com.activo = 'Si' "
                        + "ORDER BY posicion, manguera";

        try {
            LogManager.info("Consultando detalles en mangueras activas corte " + corteID);
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(vo->{
                        if (BigDecimal.ZERO.compareTo(new BigDecimal(vo.NVL("imonto", "0.00")))==0
                                && BigDecimal.ZERO.compareTo(new BigDecimal(vo.NVL("fmonto", "0.00")))==0) {
                            LogManager.info("Ajustando Cortes TEAM");
                            vo.setField("dmonto", vo.NVL("consumo", "0.00"));
                        } else {
                            vo.setField("dmonto", CorteDetalleDAO.getDiferencia(vo.NVL("imonto", "0.00"), vo.NVL("fmonto", "0.00"), "P", vo.NVL("dispensarios")));
                        }
                        vo.setField("dvolumen", CorteDetalleDAO.getDiferencia(vo.NVL("ivolumen", "0.00"), vo.NVL("fvolumen", "0.00"), "V", vo.NVL("dispensarios")));
                        detallesCorte.add(vo);
                    });
        } catch (DBException ex) {
            LogManager.info("Error consultando detalles core activo " + corteID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniendo los detalles", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Corte "+corteID));
        }

        return detallesCorte;
    }

    public static String getCorteAbierto() throws DetiPOSFault {
        try {
            return OmicromSLQHelper.getUnique("SELECT ct.id FROM ct WHERE ct.status = 'Abierto'").NVL("id");
        } catch (DBException ex) {
            LogManager.info("Error consultando detalles corte abierto");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando detalles", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Corte Actual"));
        }
    }

    public static List <DinamicVO<String, String>> getDetallesCorteAbierto(String isla) throws DetiPOSFault {
        String corte = getCorteAbierto();
        List <DinamicVO<String, String>> detallesCorte = new ArrayList <> ();
        String sql = "SELECT ctd.*, man_pro.volumenf fvolumen, man_pro.montof fmonto, com.descripcion, variables.dispensarios, IFNULL( rm.importe, 0.00 ) consumo, IFNULL( rm.descuento, 0.00 ) descuento "
                    + "FROM ("
                    + "    SELECT ctd.id, ctd.idnvo, ctd.posicion, '1' manguera, ROUND(imonto1, 2) imonto, ROUND(ivolumen1, 2) as ivolumen "
                    + "    FROM ct JOIN ctd USING( id ) WHERE ct.id = " + corte + " "
                    + "    UNION ALL "
                    + "    SELECT ctd.id, ctd.idnvo, ctd.posicion, '2' manguera, ROUND(imonto2, 2) imonto, ROUND(ivolumen2, 2) as ivolumen "
                    + "    FROM ct JOIN ctd USING( id ) WHERE ct.id = " + corte + " "
                    + "    UNION ALL "
                    + "    SELECT ctd.id, ctd.idnvo, ctd.posicion, '3' manguera, ROUND(imonto3, 2) imonto, ROUND(ivolumen3, 2) as ivolumen "
                    + "    FROM ct JOIN ctd USING( id ) WHERE ct.id = " + corte + " "
                    + ") ctd "
                    + "JOIN ( SELECT id, posicion, MAX( idnvo ) idnvo FROM ctd GROUP BY id, posicion ) aux USING( id, posicion, idnvo ) "
                    + "JOIN man ON ctd.posicion = man.posicion " + ( StringUtils.isNumber(isla) && !"0".equals(isla) ? "AND man.isla_pos = " + isla + " " : " " ) 
                    + "LEFT JOIN ( SELECT posicion, corte, manguera, ROUND( SUM( pesos ), 2 ) importe, ROUND( SUM( descuento ), 2 ) descuento FROM rm WHERE rm.corte = " + corte + " GROUP BY corte, posicion, manguera ) rm ON man.posicion = rm.posicion AND rm.corte = ctd.id AND ctd.manguera = rm.manguera "
                    + "JOIN ( SELECT posicion, manguera, totalizadorV volumenf, `totalizador$` montof, producto FROM man_pro WHERE activo = 'Si' ) man_pro ON man_pro.posicion = ctd.posicion AND man_pro.manguera = ctd.manguera "
                    + "JOIN com ON com.clavei = man_pro.producto "
                    + "JOIN variables ON TRUE "
                    + "WHERE com.activo = 'Si' AND man.activo = 'Si' "
                    + "ORDER BY posicion, manguera";

        try {
            LogManager.info("Consultando detalles corte abierto");
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach((DinamicVO<String, String> vo)-> {
                        if (BigDecimal.ZERO.compareTo(new BigDecimal(vo.NVL("imonto", "0.00")))==0
                                && BigDecimal.ZERO.compareTo(new BigDecimal(vo.NVL("fmonto", "0.00")))==0) {
                            LogManager.info("Ajustando Cortes TEAM");
                            vo.setField("dmonto", vo.NVL("consumo", "0.00"));
                        } else {
                            vo.setField("dmonto", CorteDetalleDAO.getDiferencia(vo.NVL("imonto", "0.00"), vo.NVL("fmonto", "0.00"), "P", vo.NVL("dispensarios")));
                        }

                        vo.setField("dvolumen", CorteDetalleDAO.getDiferencia(vo.NVL("ivolumen", "0.00"), vo.NVL("fvolumen", "0.00"), "V", vo.NVL("dispensarios")));
                        detallesCorte.add(vo);
                    });
        } catch (DBException ex) {
            LogManager.info("Error consultando detalles corte abierto");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando detalles", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Corte Actual"));
        }

        return detallesCorte;
    }

    public static List<DinamicVO<String, String>> getAditivosCorteActives(String corteID, String isla) throws DetiPOSFault {
        List<DinamicVO<String, String>> detallesCorte = new ArrayList<>();
        String sql = "SELECT isla_pos ISLA, SUM( cantidad ) ARTICULOS, SUM( total ) IMPORTE "
                + "FROM vtaditivos "
                + "JOIN man USING( posicion ) "
                + "WHERE corte = " + (corteID) + " AND tm = 'C' "
                + (StringUtils.isNumber(isla) ? "AND isla_pos = " + (isla) + " " : "")
                + "GROUP BY isla_pos";
        try {
            LogManager.info("Consultando aditivos para el corte " + corteID);
            LogManager.debug(sql);
            OmicromSLQHelper.executeQuery(sql)
                    .forEach(vo -> detallesCorte.add(vo));
        } catch (DBException ex) {
            LogManager.info("Error consultando aditivos para el corte " + corteID);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error obteniexendo los detalles", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Corte Actual"));
        }
        return detallesCorte;
    }
  
    public static String getDiferencia(String valori, String valorf, String tipo, String dispensario) {

        BigDecimal vi = new BigDecimal(valori);
        BigDecimal vf = new BigDecimal(valorf);
        BigDecimal overflowLimit = "Bennett".equals(dispensario) ? OVERFLOW_BENNETT : OVERFLOW_GENERIC;
        return (vf.compareTo(vi)<0 ? 
                "P".equals(tipo) && vi.subtract(vf).abs().compareTo(CFACTOR)<0 ?
                    BigDecimal.ZERO :
                    vi.compareTo(OVERFLOW_EXCEPTION)<0 ? 
                        OVERFLOW_EXCEPTION.subtract(vi).add(vf) : 
                        overflowLimit.subtract(vi).add(vf) :
                vf.subtract(vi)).setScale(2, RoundingMode.HALF_EVEN).toPlainString();
    }//getDiferencia

    /**
     * 
     * @param corteID
     * @return
     * @throws DetiPOSFault 
     */
    public static boolean updateCortesDetallesV2(String corteID) throws DetiPOSFault {
    
        String sql = "UPDATE ctd JOIN ( SELECT IF( Dispensarios = 'Bennet', 10000000, 1000000 ) overflow FROM variables ) v ON TRUE "
                + "JOIN ( "
                +       "SELECT man.posicion, t.idtarea corte, "
                +               "t.monto1 importe1, t.monto2 importe2, t.monto3 importe3, "
                +               "t.volumen1, t.volumen2, t.volumen3 "
                +       "FROM man "
                +       "LEFT JOIN totalizadores t ON man.posicion = t.posicion "
                +       "WHERE man.activo = 'Si' "
                + ") tot ON tot.corte = ctd.id AND tot.posicion = ctd.posicion "
                + "LEFT JOIN ( "
                +    "SELECT man.posicion, corte, "
                +        "IFNULL( rm.importe1, 0 ) importe1, IFNULL( rm.volumen1, 0 ) volumen1, "
                +        "IFNULL( rm.importe2, 0 ) importe2, IFNULL( rm.volumen2, 0 ) volumen2, "
                +        "IFNULL( rm.importe3, 0 ) importe3, IFNULL( rm.volumen3, 0 ) volumen3 "
                +    "FROM man "
                +    "LEFT JOIN ( "
                +            "SELECT "
                +                "posicion, corte, "
                +                "ROUND( SUM( CASE WHEN manguera=1 THEN pesos ELSE 0 END ), 2 ) importe1, "
                +                "ROUND( SUM( CASE WHEN manguera=1 THEN volumen ELSE 0 END ), 2 ) volumen1, "
                +                "ROUND( SUM( CASE WHEN manguera=2 THEN pesos ELSE 0 END ), 2 ) importe2, "
                +                "ROUND( SUM( CASE WHEN manguera=2 THEN volumen ELSE 0 END ), 2 ) volumen2, "
                +                "ROUND( SUM( CASE WHEN manguera=3 THEN pesos ELSE 0 END ), 2 ) importe3, "
                +                "ROUND( SUM( CASE WHEN manguera=3 THEN volumen ELSE 0 END ), 2 ) volumen3 "
                +            "FROM rm "
                +            "WHERE corte = " + corteID + " "
                +            "GROUP BY posicion "
                +    ") rm ON man.posicion = rm.posicion "
                +    "WHERE man.activo = 'Si' "
                + ") rm ON rm.corte = ctd.id AND rm.posicion = ctd.posicion "
                + "SET "
                +       "ctd.fmonto1 = IFNULL( tot.importe1, ROUND( MOD( ctd.imonto1 + IFNULL( rm.importe1, 0 ), overflow ), 2 ) ), "
                +       "ctd.difimporte1 = IF( tot.importe1 IS NULL, 0, ROUND( tot.importe1 - MOD( ctd.imonto1 + IFNULL( rm.importe1, 0 ), overflow ), 2 ) ), " 
                +       "ctd.fmonto2 = IFNULL( tot.importe2, ROUND( MOD( ctd.imonto2 + IFNULL( rm.importe2, 0 ), overflow ), 2 ) ), " 
                +       "ctd.difimporte2 = IF( tot.importe2 IS NULL, 0, ROUND( tot.importe2 - MOD( ctd.imonto2 + IFNULL( rm.importe2, 0 ), overflow ), 2 ) ), " 
                +       "ctd.fmonto3 = IFNULL( tot.importe3, ROUND( MOD( ctd.imonto3 + IFNULL( rm.importe3, 0 ), overflow ), 2 ) ), " 
                +       "ctd.difimporte3 = IF( tot.importe3 IS NULL, 0, ROUND( tot.importe3 - MOD( ctd.imonto3 + IFNULL( rm.importe3, 0 ), overflow ), 2 ) ), " 
                +       "ctd.fvolumen1 = IFNULL( tot.volumen1, ROUND( MOD( ctd.ivolumen1 + IFNULL( rm.volumen1, 0 ), overflow ), 2 ) ), " 
                +       "ctd.difvolumen1 = IF( tot.volumen1 IS NULL, 0, ROUND( tot.volumen1 - MOD( ctd.ivolumen1 + IFNULL( rm.volumen1, 0 ), overflow ), 2 ) ), " 
                +       "ctd.fvolumen2 = IFNULL( tot.volumen2, ROUND( MOD( ctd.ivolumen2 + IFNULL( rm.volumen2, 0 ), overflow ), 2 ) ), " 
                +       "ctd.difvolumen2 = IF( tot.volumen2 IS NULL, 0, ROUND( tot.volumen2 - MOD( ctd.ivolumen2 + IFNULL( rm.volumen2, 0 ), overflow ), 2 ) ), " 
                +       "ctd.fvolumen3 = IFNULL( tot.volumen3, ROUND( MOD( ctd.ivolumen3 + IFNULL( rm.volumen3, 0 ), overflow ), 2 ) ), " 
                +       "ctd.difvolumen3 = IF( tot.volumen3 IS NULL, 0, ROUND( tot.volumen3 - MOD( ctd.ivolumen3 + IFNULL( rm.volumen3, 0 ), overflow ), 2 ) ) "
                + "WHERE ctd.id = " + corteID;
        try {
            LogManager.info("Actualizando detalles del corte " + corteID);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando detalles del corte " + corteID);
            LogManager.debug(sql);
            throw new DetiPOSFault("Error actualizando los detalles (V2)", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error actualizando los detalles", "Corte "+corteID), ex);
        }
    }

    public static boolean updateCortesDetallesV3(String corteID) throws DetiPOSFault {

        String sql = "UPDATE ctd JOIN ( SELECT IF( Dispensarios = 'Bennet', 10000000, 1000000 ) overflow FROM variables ) v ON TRUE "
                    + "LEFT JOIN ( "
                    +       "SELECT posicion, "
                    +               "SUM( monto1 ) importe1, SUM( monto2 ) importe2, SUM( monto3 ) importe3, "
                    +               "SUM( volumen1 ) volumen1, SUM( volumen2 ) volumen2, SUM( volumen3 ) volumen3 "
                    +       "FROM ( "
                    +               "SELECT posicion, manguera, totalizadorV volumen1, totalizador$ monto1, 0 volumen2, 0 monto2, 0 volumen3, 0 monto3 "
                    +               "FROM man_pro WHERE manguera = 1 AND activo = 'Si' "
                    +               "UNION ALL "
                    +               "SELECT posicion, manguera, 0 volumen1, 0 monto2, totalizadorV volumen2, totalizador$ monto2, 0 volumen3, 0 monto3 "
                    +               "FROM man_pro WHERE manguera = 2 AND activo = 'Si' "
                    +               "UNION ALL "
                    +               "SELECT posicion, manguera, 0 volumen1, 0 monto1, 0 volumen2, 0 monto2, totalizadorV volumen3, totalizador$ monto3 "
                    +               "FROM man_pro WHERE manguera = 3 AND activo = 'Si' "
                    +       ") man "
                    +       "GROUP BY posicion "
                    + ") tot ON tot.posicion = ctd.posicion "
                    + "LEFT JOIN ( "
                    +    "SELECT man.posicion, corte, "
                    +        "IFNULL( rm.importe1, 0 ) importe1, IFNULL( rm.volumen1, 0 ) volumen1, "
                    +        "IFNULL( rm.importe2, 0 ) importe2, IFNULL( rm.volumen2, 0 ) volumen2, "
                    +        "IFNULL( rm.importe3, 0 ) importe3, IFNULL( rm.volumen3, 0 ) volumen3 "
                    +    "FROM man "
                    +    "LEFT JOIN ( "
                    +            "SELECT "
                    +                "posicion, corte, "
                    +                "ROUND( SUM( CASE WHEN manguera=1 THEN pesos ELSE 0 END ), 2 ) importe1, "
                    +                "ROUND( SUM( CASE WHEN manguera=1 THEN volumen ELSE 0 END ), 2 ) volumen1, "
                    +                "ROUND( SUM( CASE WHEN manguera=2 THEN pesos ELSE 0 END ), 2 ) importe2, "
                    +                "ROUND( SUM( CASE WHEN manguera=2 THEN volumen ELSE 0 END ), 2 ) volumen2, "
                    +                "ROUND( SUM( CASE WHEN manguera=3 THEN pesos ELSE 0 END ), 2 ) importe3, "
                    +                "ROUND( SUM( CASE WHEN manguera=3 THEN volumen ELSE 0 END ), 2 ) volumen3 "
                    +            "FROM rm "
                    +            "WHERE corte = " + corteID + " "
                    +            "GROUP BY posicion "
                    +    ") rm ON man.posicion = rm.posicion "
                    +    "WHERE man.activo = 'Si' "
                    + ") rm ON rm.corte = ctd.id AND rm.posicion = ctd.posicion "
                    + "SET "
                    +       "ctd.fmonto1 = IFNULL( tot.importe1, ROUND( MOD( ctd.imonto1 + IFNULL( rm.importe1, 0 ), overflow ), 2 ) ), "
                    +       "ctd.difimporte1 = IF( tot.importe1 IS NULL, 0, ROUND( tot.importe1 - MOD( ctd.imonto1 + IFNULL( rm.importe1, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.fmonto2 = IFNULL( tot.importe2, ROUND( MOD( ctd.imonto2 + IFNULL( rm.importe2, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.difimporte2 = IF( tot.importe2 IS NULL, 0, ROUND( tot.importe2 - MOD( ctd.imonto2 + IFNULL( rm.importe2, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.fmonto3 = IFNULL( tot.importe3, ROUND( MOD( ctd.imonto3 + IFNULL( rm.importe3, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.difimporte3 = IF( tot.importe3 IS NULL, 0, ROUND( tot.importe3 - MOD( ctd.imonto3 + IFNULL( rm.importe3, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.fvolumen1 = IFNULL( tot.volumen1, ROUND( MOD( ctd.ivolumen1 + IFNULL( rm.volumen1, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.difvolumen1 = IF( tot.volumen1 IS NULL, 0, ROUND( tot.volumen1 - MOD( ctd.ivolumen1 + IFNULL( rm.volumen1, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.fvolumen2 = IFNULL( tot.volumen2, ROUND( MOD( ctd.ivolumen2 + IFNULL( rm.volumen2, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.difvolumen2 = IF( tot.volumen2 IS NULL, 0, ROUND( tot.volumen2 - MOD( ctd.ivolumen2 + IFNULL( rm.volumen2, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.fvolumen3 = IFNULL( tot.volumen3, ROUND( MOD( ctd.ivolumen3 + IFNULL( rm.volumen3, 0 ), overflow ), 2 ) ), " 
                    +       "ctd.difvolumen3 = IF( tot.volumen3 IS NULL, 0, ROUND( tot.volumen3 - MOD( ctd.ivolumen3 + IFNULL( rm.volumen3, 0 ), overflow ), 2 ) ) "
                  + "WHERE ctd.id = " + corteID;
        try {
            LogManager.info("Actualizando detalles (V3) del corte " + corteID);
            LogManager.debug(sql);
            return MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error actualizando detalles del corte " + corteID);
            LogManager.debug(sql);
            throw new DetiPOSFault("Error actualizando los detalles (V3)", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error obteniendo los detalles", "Corte "+corteID), ex);
        }
    }
}
