/*
 * GycseOperacion
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2016
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

public class GycseOperacion extends BaseVO {
    public static final String ENTITY_NAME = "gycse_operaciones";
    
    public static final String DEFAULT_CAPTURA = "0000000000000000000000000000000000000000";
    public static final String DEFAULT_NUMERO = "0000000000";

    public static enum FIELDS {

        id,
        tipo,
        corte,
        despachador,
        peticion,
        operador,
        producto,
        servicio,
        captura,
        fecha_envio,
        fecha_respuesta,
        autorizacion,
        numero,
        importe,
        comision,
        codigo_respuesta,
        mensaje_respuesta,
        descripcion_respuesta,
        transaccion,
        status,
        conciliacion,
        saldo_inicial,
        saldo_final,
        consultas,
        forma_de_pago,
        auth_banco,
        id_cli,
        facturado,
        uuid,
        maqueta,
        comprobante,
        comision_e,
        comision_d,
        comision_m
    }

    private static final int[] FLAGS = new int[]{

        DBField.PRIMARY_KEY,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0};

    private static final int[] TYPES = new int[]{

        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.TIMESTAMP,
        Types.TIMESTAMP,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class), TYPES, FLAGS);

    public GycseOperacion() {
        super(true);
    }
    public GycseOperacion(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("GycseOperacion[" + this + "]");
    }

    public GycseOperacion(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("GycseOperacion[" + this + "]");
    }

    
    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public static GycseOperacion newRecarga(
            String codigoDespachador,
            String codigoOperador,
            String numero,
            String importe,
            String paymentBank,
            String authBank,
            String pruebas) throws DBException, DetiPOSFault {
        GycseOperacion operacion = new GycseOperacion();

        operacion.setField(GycseOperacion.FIELDS.id, 
                MySQLDB.next(
                        operacion.getEntity(), 
                        GycseOperacion.FIELDS.id.name()));
        operacion.setField(FIELDS.tipo, "R");
        operacion.setField(FIELDS.corte, "0");
        operacion.setField(FIELDS.despachador, codigoDespachador);
        operacion.setField(FIELDS.operador, codigoOperador);
        operacion.setField(FIELDS.numero, numero);
        operacion.setField(FIELDS.importe, importe);
        operacion.setField(FIELDS.comision, "0");
        operacion.setField(FIELDS.servicio, "0000");
        operacion.setField(FIELDS.producto, "0000");
        operacion.setField(FIELDS.captura, DEFAULT_CAPTURA);
        operacion.setField(FIELDS.fecha_envio, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        operacion.setField(FIELDS.status, "S");
        operacion.setField(FIELDS.conciliacion, "N");
        operacion.setField(FIELDS.consultas, "0");
        operacion.setField(FIELDS.forma_de_pago, paymentBank);
        operacion.setField(FIELDS.auth_banco, authBank);
        operacion.setField(FIELDS.id_cli, "0");
        operacion.setField(FIELDS.facturado, "0");
        operacion.setField(FIELDS.uuid, "-----");
        operacion.setField(FIELDS.maqueta, pruebas);
        operacion.setField(FIELDS.comprobante, "0");
        operacion.setField(FIELDS.comision_e, "0");
        operacion.setField(FIELDS.comision_d, "0");
        operacion.setField(FIELDS.comision_m, "0");

        MySQLHelper.getInstance().insert(operacion);
        return GycseDAO.getOperacion(operacion.NVL(FIELDS.id.name()));
    }

    public static GycseOperacion newServicio(
            String codigoDespachador,
            String servicio,
            String producto,
            String captura,
            String importe,
            String paymentBank,
            String authBank,
            String pruebas) throws DBException, DetiPOSFault {
        GycseOperacion operacion = new GycseOperacion();

        operacion.setField(GycseOperacion.FIELDS.id, 
                MySQLDB.next(
                        operacion.getEntity(), 
                        GycseOperacion.FIELDS.id.name()));

        operacion.setField(FIELDS.despachador, codigoDespachador);
        operacion.setField(FIELDS.corte, "0");
        operacion.setField(FIELDS.operador, "00");
        operacion.setField(FIELDS.numero, DEFAULT_NUMERO);
        operacion.setField(FIELDS.importe, importe);
        operacion.setField(FIELDS.comision, "0");
        operacion.setField(FIELDS.tipo, "S");
        operacion.setField(FIELDS.servicio, servicio);
        operacion.setField(FIELDS.producto, producto);
        operacion.setField(FIELDS.captura, captura);
        operacion.setField(FIELDS.fecha_envio, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        operacion.setField(FIELDS.status, "S");
        operacion.setField(FIELDS.conciliacion, "N");
        operacion.setField(FIELDS.consultas, "0");
        operacion.setField(FIELDS.forma_de_pago, paymentBank);
        operacion.setField(FIELDS.auth_banco, authBank);
        operacion.setField(FIELDS.id_cli, "0");
        operacion.setField(FIELDS.facturado, "0");
        operacion.setField(FIELDS.uuid, "-----");
        operacion.setField(FIELDS.maqueta, pruebas);
        operacion.setField(FIELDS.comprobante, "0");
        operacion.setField(FIELDS.comision_e, "0");
        operacion.setField(FIELDS.comision_d, "0");
        operacion.setField(FIELDS.comision_m, "0");

        MySQLHelper.getInstance().insert(operacion);
        return GycseDAO.getOperacion(operacion.NVL(FIELDS.id.name()));
    }
}
