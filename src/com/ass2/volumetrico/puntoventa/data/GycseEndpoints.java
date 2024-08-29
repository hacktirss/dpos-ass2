/*
 * GycseEndpoints
 * DPOS
 *  2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel VillafaÃ±a, Softcoatl
 * @version 1.0
 * @since aug 2016
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

/**
 *
 * @author Rolando Esquivel
 */
public class GycseEndpoints extends BaseVO {
    public static final String ENTITY_NAME = "gycse_endpoints";

    public static enum FIELDS {

        id,
        clave,
        nombre,
        endpoint,
        service,
        portname,
        client,
        user,
        pwd,
        activo,
        pruebas
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
        Types.NUMERIC,
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class), TYPES, FLAGS);

    public GycseEndpoints() {
        super(true);
    }

    public GycseEndpoints(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("GycseEndpoints[" + this + "]");
    }

    public GycseEndpoints(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("GycseEndpoints[" + this + "]");
    }

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }
}//GycseEndpoint
