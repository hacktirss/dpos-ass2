/*
 * TerminalAlmacenamientoVO
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

public class TerminalAlmacenamientoVO extends BaseVO {

    public static final String ENTITY_NAME = "tanques";

    public static enum FIELDS {
        posicion,
        manguera,
        tanque,
        fecha,
        volumen_inicial,
        volumen_final,
        volumen_descargado,
        volumen_actual,
        volumen_vendido,
        terminal,
        permisoCRE
    }

    private static final int[] FLAGS = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static final int[] TYPES = new int[]{
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.TIMESTAMP,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR
    };

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class), TYPES, FLAGS);

    public TerminalAlmacenamientoVO() {
        super(true);
    }

    public TerminalAlmacenamientoVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("TerminalAlmacenamientoVO[" + this + "]");
    }

    public TerminalAlmacenamientoVO(Map<String, String> poFields) {
        super(poFields, true);
        LogManager.debug("TerminalAlmacenamientoVO[" + this + "]");
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
