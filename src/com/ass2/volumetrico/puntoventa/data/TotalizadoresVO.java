/*
 * TotalizadoresVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class TotalizadoresVO extends BaseVO {

    public static final String ENTITY_NAME = "totalizadores";

    public static enum T_FIELDS {

        id,
        posicion,
        turno,
        monto1,
        volumen1,
        monto2,
        volumen2,
        monto3,
        volumen3,
        fecha,
        idtarea,
        idfolio
    }

    private static final int[] FLAGS = new int[]{DBField.PRIMARY_KEY,
        DBField.PRIMARY_KEY,
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

    private static final int[] TYPES = new int[]{Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.TIMESTAMP,
        Types.NUMERIC,
        Types.NUMERIC
    };

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(T_FIELDS.class), TYPES, FLAGS);

    public TotalizadoresVO() {
        super(true);
    }

    public TotalizadoresVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("TotalizadoresVO[" + this + "]");
    }

    public TotalizadoresVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("TotalizadoresVO[" + this + "]");
    }
    
    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public void setId(String id) {
        setField(T_FIELDS.id, id);
    }

    public void setPosicion(String posicion) {
        setField(T_FIELDS.posicion, posicion);
    }

    public void setTurno(String turno) {
        setField(T_FIELDS.turno, turno);
    }

    public void setMonto1(String monto1) {
        setField(T_FIELDS.monto1, monto1);
    }

    public void setVolumen1(String volumen1) {
        setField(T_FIELDS.volumen1, volumen1);
    }

    public void setMonto2(String monto2) {
        setField(T_FIELDS.monto2, monto2);
    }

    public void setVolumen2(String volumen2) {
        setField(T_FIELDS.volumen3, volumen2);
    }

    public void setMonto3(String monto3) {
        setField(T_FIELDS.monto3, monto3);
    }

    public void setVolumen3(String volumen3) {
        setField(T_FIELDS.volumen3, volumen3);
    }

    public void setFecha(String fecha) {
        setField(T_FIELDS.fecha, fecha);
    }

    public void setIdtarea(String idtarea) {
        setField(T_FIELDS.idtarea, idtarea);
    }

    public void setIdfolio(String idfolio) {
        setField(T_FIELDS.idfolio, idfolio);
    }

    public String getId() {
        return NVL(T_FIELDS.id.name());
    }

    public String getPosicion() {
        return NVL(T_FIELDS.posicion.name());
    }

    public String getTurno() {
        return NVL(T_FIELDS.turno.name());
    }

    public String getMonto1() {
        return NVL(T_FIELDS.monto1.name());
    }

    public String getVolumen1() {
        return NVL(T_FIELDS.volumen1.name());
    }

    public String getMonto2() {
        return NVL(T_FIELDS.monto2.name());
    }

    public String getVolumen2() {
        return NVL(T_FIELDS.volumen2.name());
    }

    public String getMonto3() {
        return NVL(T_FIELDS.monto3.name());
    }

    public String getVolumen3() {
        return NVL(T_FIELDS.volumen3.name());
    }

    public String getFecha() {
        return NVL(T_FIELDS.fecha.name());
    }

    public String getIdtarea() {
        return NVL(T_FIELDS.idtarea.name());
    }

    public String getIdfolio() {
        return NVL(T_FIELDS.idfolio.name());
    }
    
    public boolean isMonto1Zero() {
        return 0D == getCampoAsDouble(T_FIELDS.monto1.name());
    }
    public boolean isVolumen1Zero() {
        return 0D == getCampoAsDouble(T_FIELDS.volumen1.name());
    }
    public boolean is1Zero() {
        return isMonto1Zero() && isVolumen1Zero();
    }

    public boolean isMonto2Zero() {
        return 0D == getCampoAsDouble(T_FIELDS.monto2.name());
    }
    public boolean isVolumen2Zero() {
        return 0D == getCampoAsDouble(T_FIELDS.volumen2.name());
    }
    public boolean is2Zero() {
        return isMonto2Zero() && isVolumen2Zero();
    }

    public boolean isMonto3Zero() {
        return 0D == getCampoAsDouble(T_FIELDS.monto3.name());
    }
    public boolean isVolumen3Zero() {
        return 0D == getCampoAsDouble(T_FIELDS.volumen3.name());
    }
    public boolean is3Zero() {
        return isMonto3Zero() && isVolumen3Zero();
    }

    @Override
    public String toString() {
        return "TotalizadorVO[posicion = " + NVL(T_FIELDS.posicion.name()) + 
                "\n\tmanguera = 1, valores = [" +
                "monto1 = " + NVL(T_FIELDS.monto1.name()) +
                ", volumen1 = " + NVL(T_FIELDS.volumen1.name()) + "], " +
                "\n\tmanguera = 2, valores = [" +
                "monto2 = " + NVL(T_FIELDS.monto2.name()) +
                ", volumen2 = " + NVL(T_FIELDS.volumen2.name()) + "], " +
                "\n\tmanguera = 3, valores = [" +
                "monto3 = " + NVL(T_FIELDS.monto3.name()) +
                ", volumen3 = " + NVL(T_FIELDS.volumen3.name()) + "]]";
    }
}//TotalizadoresVO
