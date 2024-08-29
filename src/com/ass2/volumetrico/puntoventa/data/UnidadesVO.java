/*
 * UnidadesVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class UnidadesVO extends BaseVO {

    public static final String ENTITY_NAME = "unidades";
    
    public static enum FIELDS {

        id,
        descripcion,
        cliente,
        placas,
        codigo,
        impreso,
        combustible,
        litros,
        importe,
        luni,
        lunf,
        mari,
        marf,
        miei,
        mief,
        juei,
        juef,
        viei,
        vief,
        sabi,
        sabf,
        domi,
        domf,
        interes,
        estado,
        depto,
        nip
    }
    private static final int[] FLAGS = new int[]{
    
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
        0
    };
    private static final int[] TYPES = new int[]{

        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.FLOAT,
        Types.FLOAT,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.TIME,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.VARCHAR
    };
    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public UnidadesVO() {
        super(true);
    }

    public UnidadesVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("UnidadesVO[" + this + "]");
    }

    public UnidadesVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("UnidadesVO[" + this + "]");
    }

    
    public static UnidadesVO getInstance() {
        UnidadesVO unidad = new UnidadesVO();

        unidad.setField(FIELDS.descripcion, "-");
        unidad.setField(FIELDS.cliente, "0");
        unidad.setField(FIELDS.placas, "0");
        unidad.setField(FIELDS.combustible, "* TODOS");
        unidad.setField(FIELDS.litros, "0");
        unidad.setField(FIELDS.importe, "0");
        unidad.setField(FIELDS.luni, "00:00:00");
        unidad.setField(FIELDS.lunf, "23:59:59");
        unidad.setField(FIELDS.mari, "00:00:00");
        unidad.setField(FIELDS.marf, "23:59:59");
        unidad.setField(FIELDS.miei, "00:00:00");
        unidad.setField(FIELDS.mief, "23:59:59");
        unidad.setField(FIELDS.juei, "00:00:00");
        unidad.setField(FIELDS.juef, "23:59:59");
        unidad.setField(FIELDS.viei, "00:00:00");
        unidad.setField(FIELDS.vief, "23:59:59");
        unidad.setField(FIELDS.sabi, "00:00:00");
        unidad.setField(FIELDS.sabf, "23:59:59");
        unidad.setField(FIELDS.domi, "00:00:00");
        unidad.setField(FIELDS.domf, "23:59:59");
        unidad.setField(FIELDS.interes, "-");
        unidad.setField(FIELDS.estado, "d");
        unidad.setField(FIELDS.depto, "0");
        unidad.setField(FIELDS.nip, "-----");

        return unidad;
    }
}//UnidadesVO
