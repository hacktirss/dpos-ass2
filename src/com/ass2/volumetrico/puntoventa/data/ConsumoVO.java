/*
 * ConsumoVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import static com.detisa.omicrom.bussiness.Corte.LIMITE_CONSUMO;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

public class ConsumoVO extends BaseVO {

    public static final String ENTITY_NAME = "rm";

    public static enum RM_FIELDS {

        id,
        dispensario, posicion,     manguera,   dis_mang,     producto,
        precio,      inicio_venta, fin_venta,   pesos,       volumen,
        pesosp,      volumenp,     importe,     comprobante, factor,
        completo,    vendedor,     turno,       corte,       iva,
        ieps,        tipo_venta,   procesado,   enviado,     cliente,
        placas,      codigo,       kilometraje, uuid,        depto, 
        vdm,         pagado,       puntos,      idcxc,       pagoreal, 
        informacorporativo, fecha_venta
    }

    private static final int[] FLAGS = new int[]{
        DBField.PRIMARY_KEY | DBField.AUTO,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0};

    private static final int[] TYPES = new int[]{
        Types.NUMERIC, 
        Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
        Types.FLOAT,   Types.DATE,    Types.DATE,    Types.FLOAT,   Types.FLOAT,
        Types.FLOAT,   Types.FLOAT,   Types.FLOAT,   Types.NUMERIC, Types.NUMERIC,
        Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.FLOAT,
        Types.FLOAT,   Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC,
        Types.VARCHAR, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC,
        Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.FLOAT, 
        Types.NUMERIC, Types.NUMERIC
    };

    public static final DBMapping MAPPING = 
            new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(RM_FIELDS.class), TYPES, FLAGS);

    public ConsumoVO () {
        super(true);
    }

    public ConsumoVO (DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("ConsumoVO[" + this + "]");
    }

    public ConsumoVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("ConsumoVO[" + this + "]");
    }

    
    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public int getId() {
        return getCampoAsInt(RM_FIELDS.id.name());
    }

    public boolean isCompleted() {
        return "1".equals(NVL(RM_FIELDS.completo.name()));
    }

    public BigDecimal getVolumenCalculado() {
        return getCampoAsDecimal(RM_FIELDS.pesos.name()).divide(getCampoAsDecimal(RM_FIELDS.precio.name()), RoundingMode.HALF_EVEN);
    }

    public BigDecimal getVolumen() {
        return getCampoAsDecimal(RM_FIELDS.volumen.name());
    }

    public boolean esMenor(BigDecimal limit) {
        return getVolumen().compareTo(LIMITE_CONSUMO)<=0
                && getVolumenCalculado().compareTo(LIMITE_CONSUMO)<=0;
    }
    
    public String toString() {
        return String.format("Consumo{ id %s, posición %s, manguera %s, importe %s, volumen %s }", 
                NVL(RM_FIELDS.id.name()),
                NVL(RM_FIELDS.posicion.name()),
                NVL(RM_FIELDS.manguera.name()),
                NVL(RM_FIELDS.pesos.name()),
                NVL(RM_FIELDS.volumen.name()));
    }

    public static ConsumoVO getInstance() {
        ConsumoVO consumo = new ConsumoVO();
        consumo.setField(ConsumoVO.RM_FIELDS.inicio_venta, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        consumo.setField(ConsumoVO.RM_FIELDS.fin_venta,  DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        consumo.setField(ConsumoVO.RM_FIELDS.importe, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.comprobante, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.completo, "1");
        consumo.setField(ConsumoVO.RM_FIELDS.tipo_venta, "D");
        consumo.setField(ConsumoVO.RM_FIELDS.procesado, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.enviado, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.cliente, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.placas, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.codigo, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.kilometraje, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.uuid, "-----");
        consumo.setField(ConsumoVO.RM_FIELDS.depto, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.vdm, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.pagado, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.puntos, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.idcxc, "");
        consumo.setField(ConsumoVO.RM_FIELDS.pagoreal, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.informacorporativo, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.idcxc, "0");
        consumo.setField(ConsumoVO.RM_FIELDS.fecha_venta, DateUtils.fncsFormat("yyyyMMdd", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        return consumo;
    }//getInstance
}
