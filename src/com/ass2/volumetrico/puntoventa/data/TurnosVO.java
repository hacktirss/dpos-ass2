/*
 * TurnosVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since dec 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import java.sql.Types;
import java.util.Map;

/**
 *
 * @author ROLANDO
 */
public class TurnosVO extends BaseVO {

    public static final String ENTITY_NAME = "tur";

    public static enum TUR_FIELDS {

        id,
        isla,
        turno,
        descripcion,
        horai,
        horaf,
        activo
    }

    private static final int[] FLAGS = new int[]{DBField.PRIMARY_KEY,
        0,
        0,
        0,
        0,
        0,
        0};

    private static final int[] TYPES = new int[]{Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.TIME,
        Types.TIME,
        Types.VARCHAR};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(TUR_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public TurnosVO() {
        super(true);
    }

    public TurnosVO(DinamicVO<String, String> poVO) {
        super(poVO, true);
        LogManager.debug("TurnoVO[" + this + "]");
    }

    public TurnosVO(Map<String, String> poFields) {
        super(poFields, true);
        LogManager.debug("TurnoVO[" + this + "]");
    }
}
