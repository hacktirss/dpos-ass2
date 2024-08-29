/*
 * VendedoresDAO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since ago 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class VendedoresVO extends BaseVO {

    public static final String ENTITY_NAME = "ven";

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public static enum VEN_FIELDS {

        id,
        nombre,
        direccion,
        colonia,
        municipio,
        alias,
        telefono,
        activo,
        fix,
        posicion,
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
        0};

    private static final int[] TYPES = new int[]{

        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(VEN_FIELDS.class), TYPES, FLAGS);

    public VendedoresVO() {
        super(true);
    }

    public VendedoresVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("VendedoresVO[" + this + "]");
    }

    public VendedoresVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("VendedoresVO[" + this + "]");
    }
    
    public boolean isAssigned(String posicion) {
        return "0".equals(NVL(VEN_FIELDS.fix.name())) 
                || NVL(VEN_FIELDS.posicion.name()).equals(posicion);
    }

    public boolean isInvalidLogin() {
        return "INCORRECTO".equals(NVL("nip"));
    }
}
