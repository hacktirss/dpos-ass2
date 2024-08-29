/*
 * CombustibleVO
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

public class CombustibleVO extends BaseVO {

    public static final String ENTITY_NAME = "com";

    public static enum COM_FIELDS {

        id,
        clave,
        clavei,
        descripcion,
        precio,
        activo,
        iva,
        ieps
    }

    private static final int[] FLAGS = new int[]{
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
        Types.VARCHAR,
        Types.FLOAT,
        Types.VARCHAR,
        Types.FLOAT,
        Types.FLOAT};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(COM_FIELDS.class), TYPES, FLAGS);

    public CombustibleVO() {
        super(true);
    }//Default constructor

    public CombustibleVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("CombustibleVO[" + this + "]");
    }//Constructor

    public CombustibleVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("CombustibleVO[" + this + "]");
    }//Constructor
    
    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }
}
