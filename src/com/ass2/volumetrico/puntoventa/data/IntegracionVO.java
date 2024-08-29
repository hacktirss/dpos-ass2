/*
 * IntegracionVO
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2016
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class IntegracionVO extends BaseVO {

    public static final String ENTITY_NAME = "integraciones";

    public static enum FIELDS {
        clave,
        descripcion,
        factory,
        status
    }

    private static final int[] FLAGS = new int[]{

        0,
        0,
        0,
        0,
    };

    private static final int[] TYPES = new int[]{

        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public IntegracionVO() {
        super(true);
    }

    public IntegracionVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("IntegracionVO[" + this + "]");
    }

    public IntegracionVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("IntegracionVO[" + this + "]");
    }

    
}
