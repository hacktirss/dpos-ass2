/*
 * CorteDetalleVO
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.Map;

public class CorteDetalleVO extends BaseVO {

    public static final String ENTITY_NAME = "ctd";
    public static final BigDecimal OVERFLOW_GENERIC = new BigDecimal("1000000");   // Generic Fuel Dispensers (Hong Yang, Gilbarco, Wayne) Overflows counters at 1, 000, 000
    public static final BigDecimal OVERFLOW_EXCEPTION = new BigDecimal("100000");  // Some Hong Yang Dispensers Overflows at 100, 000
    public static final BigDecimal OVERFLOW_BENNETT = new BigDecimal("10000000");  // Bennett dispensers overflows counters at 10, 000, 000
    public static final BigDecimal CFACTOR = new BigDecimal("5");

    public static enum CDT_FIELDS {

        id,
        idnvo,
        posicion,
        imonto1,
        imonto2,
        imonto3,
        ivolumen1,
        ivolumen2,
        ivolumen3,
        fmonto1,
        fmonto2,
        fmonto3,
        fvolumen1,
        fvolumen2,
        fvolumen3
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
        0,
        0,
        0,
        0};

    private static final int[] TYPES = new int[]{Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT,
        Types.FLOAT};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(CDT_FIELDS.class), TYPES, FLAGS);

    private BigDecimal overflowLimit = OVERFLOW_GENERIC;

    private BigDecimal difimporte1 = BigDecimal.ZERO;
    private BigDecimal difimporte2 = BigDecimal.ZERO;
    private BigDecimal difimporte3 = BigDecimal.ZERO;
    private BigDecimal difvolumen1 = BigDecimal.ZERO;
    private BigDecimal difvolumen2 = BigDecimal.ZERO;
    private BigDecimal difvolumen3 = BigDecimal.ZERO;

    public CorteDetalleVO() {
        super(true);
    }

    public CorteDetalleVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("CorteDetalleVO[" + this + "]");
    }

    public CorteDetalleVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("CorteDetalleVO[" + this + "]");
    }

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public void setOverflowLimit(BigDecimal overflowLimit) {
        this.overflowLimit = overflowLimit;
    }

    public static CorteDetalleVO instance(TotalizadoresVO totalizador, String idCorte) {

        CorteDetalleVO newDetalle = new CorteDetalleVO();

        newDetalle.setId(idCorte);
        newDetalle.setPosicion(totalizador.getPosicion());
        newDetalle.setImonto1(totalizador.getMonto1());
        newDetalle.setImonto2(totalizador.getMonto2());
        newDetalle.setImonto3(totalizador.getMonto3());
        newDetalle.setIvolumen1(totalizador.getVolumen1());
        newDetalle.setIvolumen2(totalizador.getVolumen2());
        newDetalle.setIvolumen3(totalizador.getVolumen3());
        newDetalle.setFmonto1(totalizador.getMonto1());
        newDetalle.setFmonto2(totalizador.getMonto2());
        newDetalle.setFmonto3(totalizador.getMonto3());
        newDetalle.setFvolumen1(totalizador.getVolumen1());
        newDetalle.setFvolumen2(totalizador.getVolumen2());
        newDetalle.setFvolumen3(totalizador.getVolumen3());

        return newDetalle;
    }

    public static CorteDetalleVO instance(String idCorte, String posicion) {

        CorteDetalleVO newDetalle = new CorteDetalleVO();

        newDetalle.setId(idCorte);
        newDetalle.setPosicion(posicion);
        newDetalle.setImonto1("0.0");
        newDetalle.setImonto2("0.0");
        newDetalle.setImonto3("0.0");
        newDetalle.setIvolumen1("0.0");
        newDetalle.setIvolumen2("0.0");
        newDetalle.setIvolumen3("0.0");
        newDetalle.setFmonto1("0.0");
        newDetalle.setFmonto2("0.0");
        newDetalle.setFmonto3("0.0");
        newDetalle.setFvolumen1("0.0");
        newDetalle.setFvolumen2("0.0");
        newDetalle.setFvolumen3("0.0");

        return newDetalle;
    }

    public void setId(String id) {
        setField(CDT_FIELDS.id, id);
    }

    public void setIdnvo(String idnvo) {
        setField(CDT_FIELDS.idnvo, idnvo);
    }

    public void setPosicion(String posicion) {
        setField(CDT_FIELDS.posicion, posicion);
    }

    public void setImonto1(String imonto1) {
        setField(CDT_FIELDS.imonto1, imonto1);
    }

    public void setImonto2(String imonto2) {
        setField(CDT_FIELDS.imonto2, imonto2);
    }

    public void setImonto3(String imonto3) {
        setField(CDT_FIELDS.imonto3, imonto3);
    }

    public void setIvolumen1(String ivolumen1) {
        setField(CDT_FIELDS.ivolumen1, ivolumen1);
    }

    public void setIvolumen2(String ivolumen2) {
        setField(CDT_FIELDS.ivolumen2, ivolumen2);
    }

    public void setIvolumen3(String ivolumen3) {
        setField(CDT_FIELDS.ivolumen3, ivolumen3);
    }

    public void setFmonto1(String fmonto1) {
        setField(CDT_FIELDS.fmonto1, fmonto1);
    }

    public void setFmonto2(String fmonto2) {
        setField(CDT_FIELDS.fmonto2, fmonto2);
    }

    public void setFmonto3(String fmonto3) {
        setField(CDT_FIELDS.fmonto3, fmonto3);
    }

    public void setFvolumen1(String fvolumen1) {
        setField(CDT_FIELDS.fvolumen1, fvolumen1);
    }

    public void setFvolumen2(String fvolumen2) {
        setField(CDT_FIELDS.fvolumen2, fvolumen2);
    }

    public void setFvolumen3(String fvolumen3) {
        setField(CDT_FIELDS.fvolumen3, fvolumen3);
    }

    public void setDifimporte1(BigDecimal difimporte1) {
        this.difimporte1 = difimporte1;
    }

    public void setDifimporte2(BigDecimal difimporte2) {
        this.difimporte2 = difimporte2;
    }

    public void setDifimporte3(BigDecimal difimporte3) {
        this.difimporte3 = difimporte3;
    }

    public void setDifvolumen1(BigDecimal difvolumen1) {
        this.difvolumen1 = difvolumen1;
    }

    public void setDifvolumen2(BigDecimal difvolumen2) {
        this.difvolumen2 = difvolumen2;
    }

    public void setDifvolumen3(BigDecimal difvolumen3) {
        this.difvolumen3 = difvolumen3;
    }

    public String getId() {
        return NVL(CDT_FIELDS.id.name());
    }

    public String getIdnvo() {
        return NVL(CDT_FIELDS.idnvo.name());
    }

    public String getPosicion() {
        return NVL(CDT_FIELDS.posicion.name());
    }

    public String getImonto1() {
        return NVL(CDT_FIELDS.imonto1.name());
    }

    public String getImonto2() {
        return NVL(CDT_FIELDS.imonto2.name());
    }

    public String getImonto3() {
        return NVL(CDT_FIELDS.imonto3.name());
    }

    public String getIvolumen1() {
        return NVL(CDT_FIELDS.ivolumen1.name());
    }

    public String getIvolumen2() {
        return NVL(CDT_FIELDS.ivolumen2.name());
    }

    public String getIvolumen3() {
        return NVL(CDT_FIELDS.ivolumen3.name());
    }

    public String getFmonto1() {
        return NVL(CDT_FIELDS.fmonto1.name());
    }

    public String getFmonto2() {
        return NVL(CDT_FIELDS.fmonto2.name());
    }

    public String getFmonto3() {
        return NVL(CDT_FIELDS.fmonto3.name());
    }

    public String getFvolumen1() {
        return NVL(CDT_FIELDS.fvolumen1.name());
    }

    public String getFvolumen2() {
        return NVL(CDT_FIELDS.fvolumen2.name());
    }

    public String getFvolumen3() {
        return NVL(CDT_FIELDS.fvolumen3.name());
    }

    public BigDecimal getDifimporte1() {
        return this.difimporte1;
    }

    public BigDecimal getDifimporte2() {
        return this.difimporte2;
    }

    public BigDecimal getDifimporte3() {
        return this.difimporte3;
    }

    public BigDecimal getDifvolumen1() {
        return this.difvolumen1;
    }

    public BigDecimal getDifvolumen2() {
        return this.difvolumen2;
    }

    public BigDecimal getDifvolumen3() {
        return this.difvolumen3;
    }
    public BigDecimal getDiferencia(String valori, String valorf, String tipo) {
        BigDecimal vi = new BigDecimal(valori).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal vf = new BigDecimal(valorf).setScale(2, RoundingMode.HALF_EVEN);

        return vf.compareTo(vi)<0 ? 
                "P".equals(tipo) && vi.subtract(vf).abs().compareTo(CFACTOR)<0 ?
                    BigDecimal.ZERO :
                    vi.compareTo(OVERFLOW_EXCEPTION)<0 ? 
                        OVERFLOW_EXCEPTION.subtract(vi).add(vf) : 
                        overflowLimit.subtract(vi).add(vf) :
                vf.subtract(vi);
    }//getDiferencia

    public BigDecimal getCambioMonto1() {
        return getDiferencia(getImonto1(), getFmonto1(), "P");
    }
    public BigDecimal getCambioVolumen1() {
        return getDiferencia(getIvolumen1(), getFvolumen1(), "V");
    }

    public BigDecimal getCambioMonto2() {
        return getDiferencia(getImonto2(), getFmonto2(), "P");
    }
    public BigDecimal getCambioVolumen2() {
        return getDiferencia(getIvolumen2(), getFvolumen2(), "V");
    }

    public BigDecimal getCambioMonto3() {
        return getDiferencia(getImonto3(), getFmonto3(), "P");
    }
    public BigDecimal getCambioVolumen3() {
        return getDiferencia(getIvolumen3(), getFvolumen3(), "V");
    }
    @Override
    public String toString() {
        return "CorteDetalleVO[posicion = " + NVL(CDT_FIELDS.posicion.name()) + 
                "\n\tmanguera = 1, valores = [" +
                "imonto1 = " + NVL(CDT_FIELDS.imonto1.name()) +
                ", fmonto1 = " + NVL(CDT_FIELDS.fmonto1.name()) +
                ", dmonto1 = " + getCambioMonto1()+
                ", ivolumen1 = " + NVL(CDT_FIELDS.ivolumen1.name()) +
                ", fvolumen1 = " + NVL(CDT_FIELDS.fvolumen1.name()) +
                ", dvolumen1 = "+ getCambioVolumen1() + "], " +
                "\n\tmanguera = 2, valores = [" +
                "imonto2 = " + NVL(CDT_FIELDS.imonto2.name()) +
                ", fmonto2 = " + NVL(CDT_FIELDS.fmonto2.name()) +
                ", dmonto2 = " + getCambioMonto2()+
                ", ivolumen2 = " + NVL(CDT_FIELDS.ivolumen2.name()) +
                ", fvolumen2 = " + NVL(CDT_FIELDS.fvolumen2.name()) +
                ", dvolumen2 = "+ getCambioVolumen2() + "], " +
                "\n\tmanguera = 3, valores = [" +
                "imonto3 = " + NVL(CDT_FIELDS.imonto3.name()) +
                ", fmonto3 = " + NVL(CDT_FIELDS.fmonto3.name()) +
                ", dmonto3 = " + getCambioMonto3()+
                ", ivolumen3 = " + NVL(CDT_FIELDS.ivolumen3.name()) +
                ", fvolumen3 = " + NVL(CDT_FIELDS.fvolumen3.name()) +
                "dvolumen3 = "+ getCambioVolumen3() + "]]";
    }
}//CorteDetalleVO
