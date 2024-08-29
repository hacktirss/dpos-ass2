/*
 * ManguerasVO
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

public class ManguerasVO extends BaseVO {

    public static final String ENTITY_NAME = "man_pro";

    public static enum DSP_FIELDS {

        id,
        dispensario,
        posicion,
        manguera,
        dis_mang,
        producto,
        isla,
        activo,
        factor,
        enable,
        vendedor,
        tanque,
        totalizadorV,
        totalizador$,
        accept_full
    }

    private static final int[] FLAGS = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static final int[] TYPES = new int[]{
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.BOOLEAN};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(DSP_FIELDS.class), TYPES, FLAGS);

    public ManguerasVO() {
        super(true);
    }

    public ManguerasVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("ManguerasVO[" + this + "]");
    }

    public ManguerasVO(Map<String, String> poFields) {
        super(poFields, true);
        LogManager.debug("ManguerasVO[" + this + "]");
    }

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }
}//DispensarioVO
