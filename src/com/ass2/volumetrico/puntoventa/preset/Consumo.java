/*
 * DetiPOSListener
 * ASS2PuntoVenta®
 * © 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2019
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.detisa.omicrom.bussiness.Command;
import com.detisa.omicrom.bussiness.CommandObserver;
import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.IslaDAO;
import com.ass2.volumetrico.puntoventa.data.IslaVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.GenericSleeper;
import com.softcoatl.utils.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.Setter;

public abstract class Consumo implements IConsumo, CommandObserver {

    protected static final String ERR_PRESET = "Error estableciendo el consumo";

    public static final BigDecimal IMPORTE_MAXIMO = new BigDecimal("9989.00");
    public static final BigDecimal IMPORTE_MAXIMO_FULL = new BigDecimal("99989.00");

    public static final String PRMT_CNS_TYP         = "tipo";
    public static final String PRMT_CNS_CNT         = "cantidad";
    public static final String PRMT_CNS_POS         = "posicion";
    public static final String PRMT_CNS_MNG         = "manguera";
    public static final String PRMT_CNS_FP          = "formaPago";
    public static final String PRMT_CNS_KM          = "kilometraje";
    public static final String PRMT_CNS_ECO         = "eco";
    public static final String PRMT_BANK_ID         = "claveBanco";
    public static final String PRMT_ACCOUNT         = "tarjeta";
    public static final String PRMT_PIN_ACCOUNT     = "pinTarjeta";
    public static final String PRMT_EMPLOYEE        = "vendedor";
    public static final String PRMT_PIN_EMPLOYEE    = "pinVendedor";
    public static final String PRMTR_APP_STATUS     = "appStatus";
    public static final String PRMTR_APP_NAME       = "appName";

    public static final int MAX_WAITING_TIME = 30;

    @Setter @Getter private String saldo = "";

    // Request parameters
    @Getter protected DinamicVO<String, String> parameters;

    // Transaction objects
    @Getter protected BaseVO manguera;
    @Getter protected BaseVO isla;
    @Getter protected BaseVO combustible;
    @Getter protected BaseVO comando;
    @Getter protected BaseVO posicion;

    @Setter @Getter private BigDecimal importe;
    @Getter private BigDecimal volumen;

    private BigDecimal max;
    
    @Getter protected Command command;

    private boolean executed;
    private boolean waiting;
    private String executionStatus;
    
    private boolean fullPreset = false;

    public Consumo() {
        LogManager.info("Ejecutando " + this.getClass().getName());
    }

    public BigDecimal getCant() {
        return isVolumen() ? volumen : importe;
    }

    public boolean isLleno() {
        return "LLENO".equals(parameters.NVL(PRMT_CNS_TYP));
    }

    public boolean isVolumen() {
        return "VOLUMEN".equals(parameters.NVL(PRMT_CNS_TYP));
    }

    public boolean isImporte() {
        return "IMPORTE".equals(parameters.NVL(PRMT_CNS_TYP));
    }

    public String getClaveTipo() {
        if(isLleno()) {
            return "T";
        } else if(isVolumen()) {
            return "V";
        } else if(isImporte()) {
            return "I";
        }
        return "";
    }

    public String getAutorizadoTexto() {
        if (isLleno()) {
            return "TANQUE LLENO";
        } else if (isImporte()) {
            return getCant().toPlainString() + " PESOS";
        } else {
            return getCant().toPlainString() + " LITROS";
        }
    }
 
    protected void determineImporte() throws DetiPOSFault {
        if (isVolumen()) {
            volumen = new BigDecimal(parameters.NVL(PRMT_CNS_CNT, "0.00")).setScale(3, RoundingMode.HALF_EVEN);
            importe = volumen.multiply(new BigDecimal(combustible.NVL(CombustibleVO.COM_FIELDS.precio.name()))).setScale(3, RoundingMode.HALF_EVEN);
        } else {
            importe = new BigDecimal(parameters.NVL(PRMT_CNS_CNT, max.toPlainString())).setScale(3, RoundingMode.HALF_EVEN);
            volumen = importe.divide(new BigDecimal(combustible.NVL(CombustibleVO.COM_FIELDS.precio.name())), 3, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_EVEN);
        }

        if (importe.compareTo(max)> 0 ) {
            importe = max;
            volumen = importe.divide(new BigDecimal(combustible.NVL(CombustibleVO.COM_FIELDS.precio.name())), 3, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_EVEN);
        }

        LogManager.info(String.format("Estableciendo valores del consumo %s, importe %s, volumen %s", parameters.NVL(PRMT_CNS_TYP), importe.toPlainString(), volumen.toPlainString()));
    }//setImporte

    protected void loadManguera() throws DetiPOSFault {
        manguera = ManguerasDAO.getDispensarioPosicionManguera(parameters.NVL(PRMT_CNS_POS), parameters.NVL(PRMT_CNS_MNG));
        if (manguera.isVoid()) {
            throw new DetiPOSFault("Error en el Consumo", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Posición inactiva", "La posicion solicitada no esta activa"));
        }
        fullPreset = "7".equals(VariablesDAO.getCorporativo("preset_length", "6"));
        posicion = EstadoPosicionesDAO.getByPosicion(manguera.NVL(ManguerasVO.DSP_FIELDS.posicion.name()));
        isla = IslaDAO.getIslaByID(manguera.NVL(ManguerasVO.DSP_FIELDS.isla.name()));
        combustible = CombustibleDAO.getCombustibleByClavei(manguera.NVL(ManguerasVO.DSP_FIELDS.producto.name()));
        LogManager.info("Preset " + (fullPreset ? "FULL" : "NORMAL"));
        LogManager.info("Preset " + ("1".equals(manguera.NVL("acceptFull")) ? "FULL" : "NORMAL"));
        max = fullPreset && "1".equals(manguera.NVL("acceptFull")) ? IMPORTE_MAXIMO_FULL : IMPORTE_MAXIMO;
    }//setManguera

    protected void determineComando() {
        comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), "Envio de prefijado", fullPreset && "1".equals(manguera.NVL("acceptFull")));
        posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)));
        posicion.setField(EstadoPosicionesVO.EP_FIELDS.kilometraje, !parameters.isNVL(PRMT_CNS_KM) && StringUtils.isNumber(parameters.NVL(PRMT_CNS_KM)) ? parameters.NVL(PRMT_CNS_KM) : "0");
        LogManager.debug("Estableciendo comando de prefijado " + comando.NVL(ComandosVO.CMD_FIELDS.comando.name()));
    }//setComando

    protected void populate(DinamicVO<String, String> values) {
        parameters = values;
    }

    protected VendedoresVO validateNIPDespachador(String nipDespachador, String posicion) throws DetiPOSFault {
        VendedoresVO ven;
        try {
            ven = VendedoresDAO.getNameByNIPI(nipDespachador, posicion);
            LogManager.error("Validando despachador con nip (" + nipDespachador + ") " + ven);
            if (!parameters.NVL(PRMT_PIN_EMPLOYEE).isEmpty()) {
                if (ven.isInvalidLogin()) {
                        throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error validando despachador", "Password incorrecto"));
                }
                if (!ven.isAssigned(parameters.NVL(Consumo.PRMT_CNS_POS))) {
                    throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error estableciendo el consumo", ven.NVL("nombre") + " no está asignado a la posición " + parameters.NVL(Consumo.PRMT_CNS_POS)));
                }
                if (!ven.isAssigned(parameters.NVL(Consumo.PRMT_CNS_POS))) {
                    throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error estableciendo el consumo", ven.NVL("nombre") + " no está asignado a la posición " + parameters.NVL(Consumo.PRMT_CNS_POS)));
                }
            }
            LogManager.debug(ven);
            return ven;
        } catch (DBException DBE) {
            throw new DetiPOSFault(DBE.getMessage());        
        }
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        if (isla.isVoid() || !"Abierta".equals(isla.NVL(IslaVO.ISL_FIELDS.status.name()))) {
            throw new DetiPOSFault("Error estableciendo el consumo", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Isla cerrada", "El turno en la isla " + isla.NVL(IslaVO.ISL_FIELDS.isla.name()) + " esta cerrado"));
        }
        LogManager.info("Validando posición " + posicion);
        if (!(this instanceof ConsumoAditivos)) {
            if (!("E".equalsIgnoreCase(posicion.NVL("estado")) 
                        || "B".equalsIgnoreCase(posicion.NVL("estado")))) {
                throw new DetiPOSFault("Error estableciendo el consumo", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Manguera descolgada", "La posicion esta descolgada. Intente cuando la manguera este colgada"));
            }
            if (EstadoPosicionesDAO.presetInProgress(manguera.NVL(ManguerasVO.DSP_FIELDS.posicion.name()))) {
                throw new DetiPOSFault("Error estableciendo el consumo", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Manguera descolgada", "La posicion tiene un prefijado pendiente de ejecucion. Intente mas tarde"));
            } 
        }
        
        return true;
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        populate(parameters);
        loadManguera();
        determineImporte();
        determineComando();
    }

    protected void transform(String tipo, String cantidad) throws DetiPOSFault {
        parameters.setField(PRMT_CNS_TYP, tipo);
        parameters.setField(PRMT_CNS_CNT, cantidad);
        determineImporte();
        determineComando();
    }

    @Override
    public boolean exec() throws DetiPOSFault {
        insert();
        if (!executed) {
            cancel();
            throw new DetiPOSFault(executionStatus);
        }
        return true;
    }

    @Override
    public void handleNotification(Command command) {

        LogManager.info("Notificación del comando " + command.getComando().NVL(ComandosVO.CMD_FIELDS.comando.name()));
        try {
            if (command.getComando().isExecuted() || command.getComando().isSetted()) {
                    executed = true;
                    EstadoPosicionesDAO.updateStatus(posicion.NVL(EstadoPosicionesVO.EP_FIELDS.posicion.name()),
                            posicion.NVL(EstadoPosicionesVO.EP_FIELDS.codigo.name(), "."),
                            posicion.NVL(EstadoPosicionesVO.EP_FIELDS.kilometraje.name()),
                            parameters.NVL(PRMT_CNS_ECO, ""));
            } else if(command.getComando().isError()) {
                throw new DetiPOSFault(command.getComando().getField("descripcion"));
            } else if(command.getComando().isTimedOut()) {
                throw new DetiPOSFault("Tiempo de espera vencido. " + command.getComando().getField("descripcion"));
            }
        } catch (DetiPOSFault ex) {
            executionStatus = "Error ejecutando preset. " + ex.getMessage();
            OmicromLogManager.error("Error ejecutando preset", ex);
        }
        waiting = false;
    }

    protected void insert() throws DetiPOSFault {
        command = new Command(ComandosDAO.create((ComandosVO) comando), manguera).setWaitingTime(90);
        waiting = true;
        command.register(this).initCommand();
        while( waiting ) {
            GenericSleeper.get().sleep();
        }
        command.stop();
    }//insert

    public boolean cancel() {
        return ComandosDAO.cancelComando(comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
    }

    @Override
    public String toString() {
        return new StringBuilder("Consumo ").append(this.getClass().getName()).append("[MANGUERA[").append(manguera)
                .append("], ISLA[").append(isla)
                .append("], POSICION[").append(posicion)
                .append("], COMBUSTIBLE[").append(combustible)
                .append("], COMANDO[").append(comando).append("]]").toString();
    }//toString
}//Consumo
