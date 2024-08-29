/*
 * EstadoPosicionesVO
 * ASS2PuntoVenta®
 * © 2015, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 2015
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

public class EstadoPosicionesVO extends BaseVO {

    public static final String ENTITY_NAME = "estado_posiciones";

    public static enum EP_FIELDS {

        id,
        posicion,
        estado,
        venta,
        volumen,
        folio,
        producto,
        codigo,
        eco,
        kilometraje,
        actualizacion,
        preset
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
        0,
        0,
        0};

    private static final int[] TYPES = new int[]{
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.FLOAT,
        Types.TIMESTAMP,
        Types.TIMESTAMP};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(EP_FIELDS.class), TYPES, FLAGS);

    public EstadoPosicionesVO() {
        super(true);
    }

    public EstadoPosicionesVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug(this.entries.getClass().getName());
        LogManager.debug("EstadoPosicionesVO[" + this + "]");
    }

    public EstadoPosicionesVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("EstadoPosicionesVO[" + this + "]");
    }

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public boolean isPumping() {
        return "d".equals(NVL(EP_FIELDS.estado, ""));
    }

    public boolean isBlocked() {
        return "b".equals(NVL(EP_FIELDS.estado, ""));
    }

    public boolean isEnabled() {
        return "e".equals(NVL(EP_FIELDS.estado, ""));
    }
    
    public int getId() {
        return getCampoAsInt(EP_FIELDS.id.name());
    }

    public void setId(int id) {
        setField(EP_FIELDS.id, String.valueOf(id));
    }

    public int getPosicion() {
        return getCampoAsInt(EP_FIELDS.posicion.name());
    }

    public void setPosicion(int posicion) {
        setField(EP_FIELDS.posicion, String.valueOf(posicion));
    }

    public String getEstado() {
        return NVL(EP_FIELDS.estado, "");
    }

    public void setEstado(String estado) {
        setField(EP_FIELDS.estado, estado);
    }

    public BigDecimal getVenta() {
        return getCampoAsDecimal(EP_FIELDS.venta.name());
    }

    public void setVenta(BigDecimal venta) {
        setField(EP_FIELDS.venta, venta.toPlainString());
    }

    public BigDecimal getVolumen() {
        return getCampoAsDecimal(EP_FIELDS.volumen.name());
    }

    public void setVolumen(BigDecimal volumen) {
        setField(EP_FIELDS.volumen, volumen.toPlainString());
    }

    public int getFolio() {
        return getCampoAsInt(EP_FIELDS.folio.name());
    }

    public void setFolio(int folio) {
        setField(EP_FIELDS.folio, String.valueOf(folio));
    }

    public String getProducto() {
        return NVL(EP_FIELDS.producto.name());
    }

    public void setProducto(String producto) {
        setField(EP_FIELDS.producto, producto);
    }

    public String getCodigo() {
        return NVL(EP_FIELDS.codigo.name());
    }

    public void setCodigo(String producto) {
        setField(EP_FIELDS.codigo, producto);
    }

    public String getEco() {
        return NVL(EP_FIELDS.eco.name());
    }

    public void setEco(String eco) {
        setField(EP_FIELDS.eco, eco);
    }

    public int getKilometraje() {
        return getCampoAsInt(EP_FIELDS.kilometraje.name());
    }

    public void setKilometraje(int kilometraje) {
        setField(EP_FIELDS.kilometraje, String.valueOf(kilometraje));
    }

    public Calendar getActualizacion() {
        return getCampoAsCalendar(EP_FIELDS.actualizacion.name());
    }

    public Calendar getPreset() {
        return getCampoAsCalendar(EP_FIELDS.preset.name());
    }

    public String getLogInfo() {
        return String.format("Estado actual posición %s, %s. Código %s, Eco, %s, Kilometraje %s"
                , NVL(EP_FIELDS.posicion.name())
                , NVL(EP_FIELDS.estado.name())
                , NVL(EP_FIELDS.codigo.name())
                , NVL(EP_FIELDS.eco.name())
                , NVL(EP_FIELDS.kilometraje.name()));
    }
}
