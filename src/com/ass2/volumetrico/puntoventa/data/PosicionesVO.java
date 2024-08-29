/*
 * PosicionesDAO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2014
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

public class PosicionesVO extends BaseVO {

   public static final String ENTITY_NAME = "man";

    public static enum DSP_FIELDS {

        id,
        posicion,
        productos,
        activo,
        lado,
        isla,
        despachador,
        man,
        inventario,
        dispensario,
        numventas,
        conteoventas,
        despachadorsig
    }

   private static final int[] FLAGS = new int [] {DBField.PRIMARY_KEY,
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

    private static final int[] TYPES = new int[]{Types.NUMERIC,
                                                Types.NUMERIC,
                                                Types.NUMERIC,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.NUMERIC,
                                                Types.NUMERIC,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.NUMERIC,
                                                Types.NUMERIC,
                                                Types.NUMERIC,
                                                Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(DSP_FIELDS.class),TYPES,FLAGS);

    public PosicionesVO() {
        super(true);
    }

    public PosicionesVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("PosicionesVO[" + this + "]");
    }

    public PosicionesVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("PosicionesVO[" + this + "]");
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
