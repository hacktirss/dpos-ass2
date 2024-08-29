/*
 * VersionesVO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since oct 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class VtaAditivosVO extends BaseVO {

    public static final String ENTITY_NAME = "vtaditivos";

    public static enum ADT_FIELDS {

        id,
        clave,
        cantidad,
        unitario,
        total,
        corte,
        posicion,
        fecha,
        descripcion,
        cliente
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
        0};

    private static final int[] TYPES = new int[]{

        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.FLOAT,
        Types.FLOAT,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.TIMESTAMP,
        Types.VARCHAR,
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(ADT_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public VtaAditivosVO() {
        super(true);
    }

    public VtaAditivosVO(DinamicVO<String, String> vo) {
        super(vo);
        LogManager.debug("VtaAditivosVO[" + this + "]");
    }

    public VtaAditivosVO(Map<String, String> fields) {
        super(fields);
        LogManager.debug("VtaAditivosVO[" + this + "]");
    }

    
}
