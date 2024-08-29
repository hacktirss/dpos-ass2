/*
 * ClientesVO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2017
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class ClientesVO extends BaseVO {

    public static final String ENTITY_NAME = "cli";

    public static final String FLAGS_POSPAGO = "Pospago";
    public static final String FLAGS_CREDITO = "Credito";
    public static final String FLAGS_PREPAGO = "Prepago";

    public static enum CLI_FIELDS {
        id,
        nombre,
        direccion,
        colonia,
        municipio,
        alias,
        telefono,
        activo,
        contacto,
        observaciones,
        tipodepago,
        limite,
        rfc,
        codigo,
        correo,
        numeroext,
        numeroint,
        enviarcorreo,
        cuentaban,
        estado,
        formadepago,
        correo2,
        puntos,
        autorizaCorporativo
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
        Types.VARCHAR,
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
        Types.VARCHAR,
        Types.NUMERIC,
        Types.NUMERIC};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(CLI_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public ClientesVO() {
        super(true);
    }

    public ClientesVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("ClientesVO[" + this + "]");
    }

    public ClientesVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("ClientesVO[" + this + "]");
    }

    public boolean isPospago() {
        return FLAGS_POSPAGO.equals(NVL(CLI_FIELDS.tipodepago.name()));
    }

    public boolean isPrepago() {
        return FLAGS_PREPAGO.equals(NVL(CLI_FIELDS.tipodepago.name()));
    }

    public boolean isCredito() {
        return FLAGS_CREDITO.equals(NVL(CLI_FIELDS.tipodepago.name()));
    }
}
