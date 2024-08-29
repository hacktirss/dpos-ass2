/*
 * InventarioDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
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

public class InventarioVO extends BaseVO {

    public static final String ENTITY_NAME = "inv";

    public static enum INV_FIELDS {
        id,
        descripcion,
        umedida,
        rubro,
        activo,
        existencia,
        precio,
        isla,
        costo
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
        0};

    private static final int[] TYPES = new int[]{

        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.FLOAT,
        Types.NUMERIC,
        Types.FLOAT};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(IslaVO.ISL_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public InventarioVO() {
        super(true);
    }

    public InventarioVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("InventarioVO[" + this + "]");
    }

    public InventarioVO(Map<String, String> fields) {
        super(fields);
        LogManager.debug("InventarioVO[" + this + "]");
    }

    
}//InventarioVO
