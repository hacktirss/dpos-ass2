/*
 * BancosDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.Types;
import java.util.Map;

/**
 *
 * @author ROLANDO
 */
public class BoletosVO extends BaseVO {

    public static final String ENTITY_NAME = "boletos";

    public static enum B_FIELDS {

        id,
        idnvo,
        secuencia,
        codigo,
        importe,
        vigente,
        ticket,
        ticket2,
        importe1,
        importe2,
        importecargado
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
        0};

    private static final int[] TYPES = new int[]{Types.NUMERIC,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC,
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(B_FIELDS.class), TYPES, FLAGS);

    public BoletosVO() {
        super(true);
    }

    public BoletosVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("BoletosVO[" + this + "]");
    }

    public BoletosVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("BoletosVO[" + this + "]");
    }

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public String getId() {
        return NVL(B_FIELDS.id.name());
    }

    public void setId(String id) {
        setField(B_FIELDS.id, id);
    }

    public String getIdnvo() {
        return NVL(B_FIELDS.idnvo.name());
    }

    public void setIdnvo(String idnvo) {
        setField(B_FIELDS.idnvo, idnvo);
    }

    public String getSecuencia() {
        return NVL(B_FIELDS.secuencia.name());
    }

    public void setSecuencia(String secuencia) {
        setField(B_FIELDS.secuencia, secuencia);
    }

    public String getCodigo() {
        return NVL(B_FIELDS.codigo.name());
    }

    public void setCodigo(String codigo) {
        setField(B_FIELDS.codigo, codigo);
    }

    public String getImporte() {
        return NVL(B_FIELDS.importe.name());
    }

    public void setImporte(String importe) {
        setField(B_FIELDS.importe, importe);
    }

    public String getVigente() {
        return NVL(B_FIELDS.vigente.name());
    }

    public void setVigente(String vigente) {
        setField(B_FIELDS.vigente, vigente);
    }

    public String getTicket() {
        return NVL(B_FIELDS.ticket.name());
    }

    public void setTicket(String ticket) {
        setField(B_FIELDS.ticket, ticket);
    }

    public String getTicket2() {
        return NVL(B_FIELDS.ticket2.name());
    }

    public void setTicket2(String ticket2) {
        setField(B_FIELDS.ticket2, ticket2);
    }

    public String getImporte1() {
        return NVL(B_FIELDS.importe1.name());
    }

    public void setImporte1(String importe1) {
        setField(B_FIELDS.importe1, importe1);
    }

    public String getImporte2() {
        return NVL(B_FIELDS.importe2.name());
    }

    public void setImporte2(String importe2) {
        setField(B_FIELDS.importe2, importe2);
    }

    public String getImportecargado() {
        return NVL(B_FIELDS.importecargado.name());
    }

    public void setImportecargado(String importecargado) {
        setField(B_FIELDS.importecargado, importecargado);
    }
    
    public boolean isVigente() {
        return "Si".equals(NVL(B_FIELDS.vigente.name()));
    }
    public BigDecimal getSaldo(String prefix) {
        return new BigDecimal(NVL(prefix+B_FIELDS.importe, "0")).subtract(new BigDecimal(NVL(prefix+B_FIELDS.importecargado, "0")));
    }
    
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("detisa.dyndns.org", 50000);
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream()) {
            os.write("$$A59,861074025313928,E91,MVT340_EV165_20140414,41542421241*CC".getBytes());
        }
    }
}//BoletosVO
