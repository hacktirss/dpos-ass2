/*
 * SmtpVO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2017
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
 * @author ROLANDO
 */
public class SmtpVO extends BaseVO {

    public static final String ENTITY_NAME = "smtp";

    public static enum FIELDS {

        id,
        smtpname,
        smtpsender,
        smtpport,
        smtpuser,
        smtpauth,
        smtploginuser,
        smtploginpass,
        smtpvalido
    }

    private static final int[] FLAGS = new int[]{DBField.PRIMARY_KEY,
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
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public SmtpVO() {
        super(true);
    }

    public SmtpVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("SmtpVO[" + this + "]");
    }

    public SmtpVO(Map<String, String> fields) {
        super(fields);
        LogManager.debug("SmtpVO[" + this + "]");
    }

    
}//SmtpVO
