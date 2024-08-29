/*
 * UnidadVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import java.sql.Types;
import java.util.Map;

public class UnidadVO extends BaseVO {

    public static final String ENTITY_NAME = "unidades";

    public static enum UND_FIELDS {

        id,
        descripcion,
        cliente,
        placas,
        codigo,
        impreso,
        combustible,
        aditivos,
        litros,
        importe,
        periodo,
        horai,
        horaf,
        interes,
        estado,
        depto
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
        0};

    private static final int[] TYPES = new int[]{
        
        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.INTEGER,
        Types.FLOAT,
        Types.FLOAT,
        Types.VARCHAR,
        Types.TIME,
        Types.TIME,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC
    };

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(UND_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public UnidadVO() {
        super(true);
    }

    public UnidadVO(DinamicVO<String, String> poVO) {
        super(poVO, true);
        LogManager.debug("UnidadVO[" + this + "]");
    }

    public UnidadVO(Map<String, String> poFields) {
        super(poFields, true);
        LogManager.debug("UnidadVO[" + this + "]");
    }
}//UnidadesVO
