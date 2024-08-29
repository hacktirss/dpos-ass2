/**
 * DepositoVO
 * ® 2020, ASS2
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2020
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.logging.LogManager;
import java.util.Map;

public class DepositoVO extends BaseVO {

    public static final String ENTITY_NAME = "ctdep";

    public enum DEP_FIELDS {
        id, 
        fecha, corte, despachador, 
        cincuentac, peso, dos, cinco, diez, 
        veinte, cincuenta, cien, doscientos, 
        quinientos, mil, total, 
        posicion, tipo_cambio;
    }

    private static final int[] FLAGS = new int[]{
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0};

    private static final int[] TYPES = new int[]{
        2, 93, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2};

    public static final DBMapping MAPPING = new DBMapping("ctdep", CollectionsUtils.fncsEnumAsArray(DEP_FIELDS.class), TYPES, FLAGS);

    @Override
    public String getEntity() {
        return "ctdep";
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public DepositoVO() {
        super(true);
    }

    public DepositoVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("DepositoVO[" + this + "]");
    }

    public DepositoVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("DepositoVO[" + this + "]");
    }

    
    public static DepositoVO getInstance() {
        DepositoVO deposito = new DepositoVO();
        deposito.setField(DEP_FIELDS.fecha, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
        deposito.setField(DEP_FIELDS.cincuentac, "0");
        deposito.setField(DEP_FIELDS.peso, "0");
        deposito.setField(DEP_FIELDS.dos, "0");
        deposito.setField(DEP_FIELDS.cinco, "0");
        deposito.setField(DEP_FIELDS.diez, "0");
        deposito.setField(DEP_FIELDS.veinte, "0");
        deposito.setField(DEP_FIELDS.cincuenta, "0");
        deposito.setField(DEP_FIELDS.cien, "0");
        deposito.setField(DEP_FIELDS.doscientos, "0");
        deposito.setField(DEP_FIELDS.quinientos, "0");
        deposito.setField(DEP_FIELDS.mil, "0");
        deposito.setField(DEP_FIELDS.total, "0");
        deposito.setField(DEP_FIELDS.posicion, "0");
        deposito.setField(DEP_FIELDS.tipo_cambio, "1");
        return deposito;
    }
}
