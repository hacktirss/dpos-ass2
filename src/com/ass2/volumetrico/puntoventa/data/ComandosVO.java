/*
 * ComandosVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.DBField;
import com.softcoatl.utils.StringUtils;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

public class ComandosVO extends BaseVO {

    public static final String LOCKING_MESSAGE = "Bloquear para finalizar turno";
    public static final String UNLOCKING_MESSAGE = "Fin corte y debloqueo posiciones";
    public static final String TOTAL_MESSAGE = "Totalizadores";

    public static final String PFX_VOLUMEN      = "V";
    public static final String PFX_IMPORTE      = "$";
    public static final String PFX_BLOQUEO      = "B";
    public static final String PFX_DESBLOQUEO   = "D";
    public static final String PFX_TOTALIZADOR  = "T";

    public static final String ENTITY_NAME = "comandos";

    public static enum CMD_FIELDS {

        id,
        posicion, manguera, comando,
        fecha_insercion, fecha_programada, fecha_ejecucion,
        intentos, ejecucion, replica, 
        descripcion, idtarea, es_interes
    }

    private static final int[] FLAGS = new int[]{
        DBField.PRIMARY_KEY | DBField.AUTO,
        0, 0, 0,
        0, 0, 0,
        0, 0, 0, 
        0, 0, 0 };

    private static final int[] TYPES = new int[]{
        Types.NUMERIC,
        Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
        Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
        Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, 
        Types.VARCHAR, Types.NUMERIC, Types.NUMERIC};

    public static final DBMapping MAPPING = 
            new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(CMD_FIELDS.class), TYPES, FLAGS);

    public static enum TIPO_CONSUMO {
        IMPORTE,
        VOLUMEN,
        LLENO;
        
        public static void validate(String value) throws DetiPOSFault {
            try {
                valueOf(value);
            } catch (IllegalArgumentException iae) {
                throw new DetiPOSFault(new DetiPOSParametersFaultInfo("El parametro TIPO DE CONSUMO es incorrecto"));
            }
        }
    }

    public ComandosVO() {
        super(true);
        LogManager.debug("ComandosVO[" + this + "]");
    }

    public ComandosVO(DinamicVO<String, String> vo) {
        super(vo, true);
        LogManager.debug("ComandosVO[" + this + "]");
    }

    public ComandosVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("ComandosVO[" + this + "]");
    }

    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

    @Override
    public DBMapping getMapping() {
        return MAPPING;
    }

    public static String getComandoConsumo(String tipo, String posicion, String manguera, String cantidad, boolean full) {
        TIPO_CONSUMO tipoConsumo = TIPO_CONSUMO.valueOf(tipo);
        StringBuilder comando = new StringBuilder();

        switch (tipoConsumo) {
            case LLENO:
            case IMPORTE:
                comando.append(PFX_IMPORTE);
                break;
            case VOLUMEN:
                comando.append(PFX_VOLUMEN);
                break;
        }

        comando.append(StringUtils.fncsFormat("00", Double.parseDouble(posicion)));
        comando.append(StringUtils.fncsFormat("0", Double.parseDouble(manguera)));
        comando.append(StringUtils.fncsFormat(full ? "00000.00" : "0000.00", Double.parseDouble(cantidad)).replaceAll("[.]", ""));

        return comando.toString();
    }
    public static String getComandoInteres(String posicion, String manguera, String interes) {
        LogManager.getRootLogger().warn("Comando interés " + posicion + " " + manguera + " " + interes);
        return new StringBuilder("P")
                            .append(StringUtils.fncsFormat("00", Double.parseDouble(posicion)))
                            .append(StringUtils.fncsFormat("0", Double.parseDouble(manguera)))
                            .append("00")
                            .append(StringUtils.fncsFormat("00", Double.parseDouble(interes))).toString();
    }
    public static String getComandoBloqueo(String posicion) {
        return PFX_BLOQUEO+StringUtils.fncsFormat("00", Double.parseDouble(posicion));
    }
    public static String getComandoDesbloqueo(String posicion) {
        return PFX_DESBLOQUEO+StringUtils.fncsFormat("00", Double.parseDouble(posicion));
    }
    public static String getComandoTotalizador(String posicion) {
        return PFX_TOTALIZADOR+StringUtils.fncsFormat("00", Double.parseDouble(posicion));
    }

    public static ComandosVO newBloqueo(String posicion, String corte) {

        ComandosVO bloqueo = new ComandosVO ();

        LogManager.debug("New Bloqueo At "+posicion);

        bloqueo.setField(ComandosVO.CMD_FIELDS.posicion, posicion);
        bloqueo.setField(ComandosVO.CMD_FIELDS.manguera, "1");
        bloqueo.setField(ComandosVO.CMD_FIELDS.comando, getComandoBloqueo(posicion));
        bloqueo.setField(ComandosVO.CMD_FIELDS.fecha_insercion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        bloqueo.setField(ComandosVO.CMD_FIELDS.fecha_programada, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        bloqueo.setField(ComandosVO.CMD_FIELDS.intentos, "0");
        bloqueo.setField(ComandosVO.CMD_FIELDS.ejecucion, "0");
        bloqueo.setField(ComandosVO.CMD_FIELDS.replica, "0");
        bloqueo.setField(ComandosVO.CMD_FIELDS.descripcion, LOCKING_MESSAGE);
        bloqueo.setField(ComandosVO.CMD_FIELDS.idtarea, corte);
        bloqueo.setField(ComandosVO.CMD_FIELDS.es_interes, "0");

        LogManager.debug(bloqueo);

        return bloqueo;
    }

    public static ComandosVO newDesbloqueo(String posicion, String corte) {

        ComandosVO desbloqueo = new ComandosVO ();

        LogManager.debug("New Desbloqueo At "+posicion);

        desbloqueo.setField(ComandosVO.CMD_FIELDS.posicion, posicion);
        desbloqueo.setField(ComandosVO.CMD_FIELDS.manguera, "1");
        desbloqueo.setField(ComandosVO.CMD_FIELDS.comando, getComandoDesbloqueo(posicion));
        desbloqueo.setField(ComandosVO.CMD_FIELDS.fecha_insercion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        desbloqueo.setField(ComandosVO.CMD_FIELDS.fecha_programada, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        desbloqueo.setField(ComandosVO.CMD_FIELDS.intentos, "0");
        desbloqueo.setField(ComandosVO.CMD_FIELDS.ejecucion, "0");
        desbloqueo.setField(ComandosVO.CMD_FIELDS.replica, "0");
        desbloqueo.setField(ComandosVO.CMD_FIELDS.descripcion, UNLOCKING_MESSAGE);
        desbloqueo.setField(ComandosVO.CMD_FIELDS.idtarea, corte);
        desbloqueo.setField(ComandosVO.CMD_FIELDS.es_interes, "0");

        LogManager.debug(desbloqueo);

        return desbloqueo;
    }

    public static ComandosVO newTotalizador(String posicion, String corte) {

        ComandosVO totalizador = new ComandosVO ();

        LogManager.debug("New Totalizador At "+posicion);

        totalizador.setField(ComandosVO.CMD_FIELDS.posicion, posicion);
        totalizador.setField(ComandosVO.CMD_FIELDS.manguera, "1");
        totalizador.setField(ComandosVO.CMD_FIELDS.comando, getComandoTotalizador(posicion));
        totalizador.setField(ComandosVO.CMD_FIELDS.fecha_insercion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        totalizador.setField(ComandosVO.CMD_FIELDS.fecha_programada, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        totalizador.setField(ComandosVO.CMD_FIELDS.intentos, "0");
        totalizador.setField(ComandosVO.CMD_FIELDS.ejecucion, "0");
        totalizador.setField(ComandosVO.CMD_FIELDS.replica, "0");
        totalizador.setField(ComandosVO.CMD_FIELDS.descripcion, TOTAL_MESSAGE);
        totalizador.setField(ComandosVO.CMD_FIELDS.idtarea, corte);
        totalizador.setField(ComandosVO.CMD_FIELDS.es_interes, "0");

        LogManager.debug(totalizador);

        return totalizador;
    }

    public static ComandosVO parse(BaseVO dispensario, String tipo, String importe, String descripcion, boolean full) {
        ComandosVO preset = new ComandosVO();
        String comando = ComandosVO.getComandoConsumo(
                tipo, 
                dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()),
                dispensario.NVL(ManguerasVO.DSP_FIELDS.manguera.name()), 
                importe,
                full);

        LogManager.debug("New Preset At "+dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));

        preset.setField(ComandosVO.CMD_FIELDS.posicion, dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));
        preset.setField(ComandosVO.CMD_FIELDS.manguera, dispensario.NVL(ManguerasVO.DSP_FIELDS.manguera.name()));
        preset.setField(ComandosVO.CMD_FIELDS.comando, comando);
        preset.setField(ComandosVO.CMD_FIELDS.fecha_insercion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        preset.setField(ComandosVO.CMD_FIELDS.fecha_programada, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        preset.setField(ComandosVO.CMD_FIELDS.fecha_ejecucion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        preset.setField(ComandosVO.CMD_FIELDS.intentos, "0");
        preset.setField(ComandosVO.CMD_FIELDS.ejecucion, "0");
        preset.setField(ComandosVO.CMD_FIELDS.replica, "0");
        preset.setField(ComandosVO.CMD_FIELDS.descripcion, StringUtils.NVL(descripcion, "."));
        preset.setField(ComandosVO.CMD_FIELDS.idtarea, "0");
        preset.setField(ComandosVO.CMD_FIELDS.es_interes, "0");

        LogManager.debug(preset);

        return preset;
    }
    public static ComandosVO gerencial(BaseVO dispensario) {
        ComandosVO gerencial = new ComandosVO();
        String comando = "P9750000";

        LogManager.debug("New Gerencial At "+dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));

        gerencial.setField(ComandosVO.CMD_FIELDS.posicion, dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));
        gerencial.setField(ComandosVO.CMD_FIELDS.manguera, dispensario.NVL(ManguerasVO.DSP_FIELDS.manguera.name()));
        gerencial.setField(ComandosVO.CMD_FIELDS.comando, comando);
        gerencial.setField(ComandosVO.CMD_FIELDS.fecha_insercion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        gerencial.setField(ComandosVO.CMD_FIELDS.fecha_programada, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        gerencial.setField(ComandosVO.CMD_FIELDS.fecha_ejecucion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        gerencial.setField(ComandosVO.CMD_FIELDS.intentos, "0");
        gerencial.setField(ComandosVO.CMD_FIELDS.ejecucion, "0");
        gerencial.setField(ComandosVO.CMD_FIELDS.replica, "0");
        gerencial.setField(ComandosVO.CMD_FIELDS.descripcion, ".");
        gerencial.setField(ComandosVO.CMD_FIELDS.idtarea, "0");
        gerencial.setField(ComandosVO.CMD_FIELDS.es_interes, "1");

        LogManager.debug(gerencial);

        return gerencial;
    }
    public static ComandosVO interes(BaseVO dispensario, String factor) {
        ComandosVO interes = new ComandosVO();
        String comando = ComandosVO.getComandoInteres(dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()),
                dispensario.NVL("manguera_int"), factor);

        LogManager.debug("New Interes At "+dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));

        interes.setField(ComandosVO.CMD_FIELDS.posicion, dispensario.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));
        interes.setField(ComandosVO.CMD_FIELDS.manguera, dispensario.NVL(ManguerasVO.DSP_FIELDS.manguera.name()));
        interes.setField(ComandosVO.CMD_FIELDS.comando, comando);
        interes.setField(ComandosVO.CMD_FIELDS.fecha_insercion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        interes.setField(ComandosVO.CMD_FIELDS.fecha_programada, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        interes.setField(ComandosVO.CMD_FIELDS.fecha_ejecucion, DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(DateUtils.REGIONAL_MEXICO), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
        interes.setField(ComandosVO.CMD_FIELDS.intentos, "0");
        interes.setField(ComandosVO.CMD_FIELDS.ejecucion, "0");
        interes.setField(ComandosVO.CMD_FIELDS.replica, "0");
        interes.setField(ComandosVO.CMD_FIELDS.descripcion, ".");
        interes.setField(ComandosVO.CMD_FIELDS.idtarea, "0");
        interes.setField(ComandosVO.CMD_FIELDS.es_interes, "1");

        LogManager.debug(interes);

        return interes;
    }

    public boolean isNew() {
        return isNVL(CMD_FIELDS.id.name());
    }

    public boolean isLock() {
        return NVL(CMD_FIELDS.comando.name()).startsWith(PFX_BLOQUEO);
    }

    public boolean isUnlock() {
        return NVL(CMD_FIELDS.comando.name()).startsWith(PFX_DESBLOQUEO);
    }

    public boolean isTotalizer() {
        return NVL(CMD_FIELDS.comando.name()).startsWith(PFX_TOTALIZADOR);
    }

    public boolean isVolumen() {
        return NVL(CMD_FIELDS.comando.name()).startsWith(PFX_VOLUMEN);
    }

    public boolean isImporte() {
        return NVL(CMD_FIELDS.comando.name()).startsWith(PFX_IMPORTE);
    }
    
    public boolean isSetted() {
        return "15".equals(NVL(CMD_FIELDS.intentos.name())) && "3".equals(NVL(CMD_FIELDS.ejecucion.name()));
    }

    public boolean isTimedOut() {
        return "-1".equals(NVL(CMD_FIELDS.ejecucion.name()));
    }

    public boolean isExecuted() {
        return "1".equals(NVL(CMD_FIELDS.intentos.name())) && "1".equals(NVL(CMD_FIELDS.ejecucion.name()));
    }
    
    public boolean isError() {
        return "2".equals(NVL(CMD_FIELDS.ejecucion.name()));
    }

    public boolean isPending() {
        return "0".equals(NVL(CMD_FIELDS.ejecucion.name()));
    }
}//ComandosVO
