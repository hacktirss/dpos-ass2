/*
 * IslaVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class IslaVO extends BaseVO {

    public static final String ENTITY_NAME = "islas";

    public static enum ISL_FIELDS {

        isla,
        descripcion,
        turno,
        activo,
        status,
        corte
    }

    private static final int[] FLAGS = new int[]{

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
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(ISL_FIELDS.class), TYPES, FLAGS);

    public IslaVO() {
        super(true);
    }

    public IslaVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("IslaVO[" + this + "]");
        LogManager.debug(this.entries.getClass().getName());
    }

    public IslaVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("IslaVO[" + this + "]");
    }
    
    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }
}
