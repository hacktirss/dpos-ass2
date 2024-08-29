/*
 * @CorteVO
 * @version 1.0
 * @author Rolando Esquivel Villafaña, REV@Softcoatl
 * Created 26/05/2014
 * Last Modified 26/05/2014
 * Modification log
 * lunes 26 de mayo de 2014 created by REV@Softcoatl
 * ALL Rights Reserved ©2014 REV@Softcoatl, Softcoatl
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

/**
 * @CorteVO @author Rolando Esquivel Villafaña, REV@Softcoatl Modification
 * log lunes 26 de mayo de 2014 created by REV@Softcoatl ALL Rights Reserved
 * ©2014 REV@Softcoatl, Softcoatl
 */
public class CorteVO extends BaseVO {

    public static final String ENTITY_NAME = "ct";

    public static enum CT_FIELDS {

        id,
        fecha,
        hora,
        fechaf,
        concepto,
        isla,
        turno,
        usr,
        status,
        statusctv
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
        0};

    private static final int[] TYPES = new int[]{
        Types.NUMERIC,
        Types.TIMESTAMP,
        Types.TIME,
        Types.TIMESTAMP,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR
    };

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(CT_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public CorteVO() {
        super(true);
    }

    public CorteVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("CorteVO[" + this + "]");
    }

    public CorteVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("CorteVO[" + this + "]");
    }

    
    public static CorteVO getInstance(String isla, String turno) {

        CorteVO corte = new CorteVO ();
        corte.setField(CorteVO.CT_FIELDS.fecha, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        corte.setField(CorteVO.CT_FIELDS.hora, DateUtils.fncsFormat("HH:mm:ss", Calendar.getInstance()));
        corte.setField(CorteVO.CT_FIELDS.fechaf, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        corte.setField(CorteVO.CT_FIELDS.concepto, "<b>Turno Abierto</b>");
        corte.setField(CorteVO.CT_FIELDS.isla, isla);
        corte.setField(CorteVO.CT_FIELDS.turno, turno);
        corte.setField(CorteVO.CT_FIELDS.usr, "POS"); 
        corte.setField(CorteVO.CT_FIELDS.status, "Abierto");
        corte.setField(CorteVO.CT_FIELDS.statusctv, "Abierto");

        return corte;
    }
    
    public boolean isOpen() {
        return "Abierto".equals(NVL(CT_FIELDS.id.name()));
    }
}//CorteVO
