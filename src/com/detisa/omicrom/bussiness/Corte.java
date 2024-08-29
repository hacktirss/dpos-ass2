/**
 * Corte 
 * DetiPOS WEB Service
 * Ejecuta el corte en la isla indicada
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since Dec 2014
 */
package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO.CMD_FIELDS;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO.COM_FIELDS;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO.RM_FIELDS;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleVO;
import com.ass2.volumetrico.puntoventa.data.CorteVO;
import com.ass2.volumetrico.puntoventa.data.DisplayDAO;
import com.ass2.volumetrico.puntoventa.data.EstacionDAO;
import com.ass2.volumetrico.puntoventa.data.IslaDAO;
import com.ass2.volumetrico.puntoventa.data.LogDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO.DSP_FIELDS;
import com.ass2.volumetrico.puntoventa.data.PosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.PosicionesVO;
import com.ass2.volumetrico.puntoventa.data.TotalizadoresDAO;
import com.ass2.volumetrico.puntoventa.data.TotalizadoresVO;
import com.ass2.volumetrico.puntoventa.data.TurnosDAO;
import com.ass2.volumetrico.puntoventa.data.TurnosVO;
import com.ass2.volumetrico.puntoventa.data.TurnosVO.TUR_FIELDS;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.database.mysql.MySQLDB;
import com.softcoatl.utils.GenericSleeper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Corte extends Thread implements CommandObserver {

    public static final String TITLE = "<font color=\"#990000\"><b>¡Atencion!</b> Ejecutando corte.</font>";
    public static final int MAX_WAITING_TIME = 300;

    public enum STEP {
        locking,
        totalizing,
        matching,
        done,
        error
    }//SETPS

    public enum CLIENT {
        POS,
        OMI,
        AUT
    }

    public enum PROCESS {
        CLOSEANDOPEN,
        CLOSE,
        OPEN
    }

    public static final BigDecimal LIMITE_CONSUMO = new BigDecimal(2500); // Límite máximo de inserción en litros

    private Calendar FINISHED_DATE;

    private boolean running = false;

    private boolean commandError = false;

    protected boolean error = false;
    protected boolean done = false;

    protected TurnosVO turno;

    protected final List <DinamicVO<String, String>> actives = new ArrayList <> ();
    protected final List <ComandosVO> commands = new ArrayList <> ();

    protected final DinamicVO<String, String> estacion;

    protected TotalizadoresCorte totalizadoresCorte;
    protected CorteOmicrom corteOmicrom;

    protected final Map <String, CombustibleVO> productos = new HashMap <> ();

    protected STEP step = STEP.locking;
    protected STEP previousStep = STEP.locking;
    protected STEP errorStep = null;

    protected String errorDetail = "";
    protected String titulo = "";
    
    protected String dispensarios = "";

    protected final CLIENT client;
    protected final PROCESS process;

    public Corte(String isla, CLIENT client, PROCESS process) throws DBException, DetiPOSFault {

        super("CORTE_"+isla);

        this.client = client;
        this.process = process;

        turno = TurnosDAO.getCurrentTurno(isla);
        titulo = TITLE;
        dispensarios = VariablesDAO.getVariable("Dispensarios");
        corteOmicrom = new CorteOmicrom(isla, openOnly());
        estacion = EstacionDAO.getDatosEstacion();
        getActivePositions();

        LogManager.debug("Cerrando Corte " + corteOmicrom.getCorte());
        
        CombustibleDAO.getProductosActivos()
                .forEach((CombustibleVO prod) -> productos.put(prod.NVL("clavei"), prod));
    }//Corte

    protected abstract boolean totalize();
    protected abstract boolean matchTotalizers();

    public boolean isSucessful() {
        return done;
    }

    public boolean isError() {
        return error;
    }

    public boolean hasFinished() {
        return isSucessful() || isError();
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public CorteVO getCorte() {
        return corteOmicrom.getCorte();
    }

    private boolean display(String message) {
        return DisplayDAO.showDisplay(titulo, message);
    }

    private boolean resetDisplay() {
        return DisplayDAO.resetDisplay(CLIENT.AUT.equals(client));
    }

    private void getActivePositions() throws DetiPOSFault {

        actives.addAll(PosicionesDAO.listActives(corteOmicrom.isla()));
    }//getActivePositions

    public String getRunningStatus() {

        String status = "Ejecutando Corte " + corteOmicrom.id() + " en la Isla " + corteOmicrom.isla() + "::";

        switch (step) {
        case locking:    return status + "Bloqueando Dispensarios";
        case totalizing: return status + "Obteniendo Totalizadores";
        case done:       return status + "Corte Finalizado. Desbloqueando Posiciones";
        case error: 
            switch (previousStep) {
                case locking:    return status + "ERROR::Ocurrio un Error Bloqueando los Dispensarios. Por favor reintente.";
                case totalizing: return status + "ERROR::Ocurrio un Error Obteniendo los Totalizadores. Por favor reintente.";
            }
        }
        return status;
    }//getRunningStatus

    private boolean updateDisplay() {

        switch (step) {
        case locking: return display("Bloqueando Dispensarios");
        case totalizing: return display("Obteniendo Totalizadores");
        case done: return display("Corte Finalizado. Desbloqueando Posiciones");
        case error:
            switch (previousStep) {
                case locking: return display("Ocurrió un Error Bloqueando los Dispensarios.");
                case totalizing: return display("Ocurrió un Error Obteniendo los Totalizadores.");
            }
        }
        return false;
    }//updateDisplay

    @Override
    public void handleNotification(Command command) {

        LogManager.info("Notificación del comando " + command.getComando().NVL(CMD_FIELDS.comando.name()));
        if (command.getComando().isError() 
                || command.getComando().isExecuted() 
                || command.getComando().isTimedOut()) {

            if (command.getComando().isError()) {
                LogManager.info("Comando ejecutado con error " + command.getComando().NVL(CMD_FIELDS.comando.name()));
                errorDetail += (errorDetail.isEmpty() ? "" : "|") + command.getComando().NVL(CMD_FIELDS.comando.name());
                LogManager.info(errorDetail);
                if (!command.getComando().isTotalizer()) {
                    commandError = true;
                }
            }

            if (command.getComando().isTimedOut() && command.getComando().isLock()) {
                commandError = true;
            }

            LogManager.info("Deteniendo comando "+command.getComando().NVL(CMD_FIELDS.comando.name()));
            command.stop();
            commands.remove(command.getComando());
        }
    }//handleNotificacion

    private boolean waitCommands() {

        while (running && !commands.isEmpty()) {
            GenericSleeper.get().setTimeout(100).sleep();
        }//while
        return commandError;
    }//checkCommands

    protected void register(ComandosVO comando, DinamicVO<String, String> dispenser) {

        if (!comando.isNew()) {
            commands.add(comando);
            new Command(comando, dispenser).setWaitingTime(Command.MAX_WAITING_TIME*10).register(this).initCommand();
        }
    }//register

    private ComandosVO createCommand(ComandosVO command) {
        try {
            return ComandosDAO.create(command);
        } catch (DetiPOSFault ex) {
            OmicromLogManager.error(ex);
        }
        return command;
    }

    private boolean lock() {

        LogManager.info("Bloqueando " + actives.size() + " posiciones");
        actives.stream()
                .filter(PumpFilter::unlocked)
                .forEach((DinamicVO<String, String> active) -> {
                    LogManager.info("Bloqueando posición " + active.NVL(CMD_FIELDS.posicion.name()));
                    register(createCommand(ComandosVO.newBloqueo(active.NVL(CMD_FIELDS.posicion.name()), corteOmicrom.id())), active);
                });
        actives.stream()
                .filter(PumpFilter::locked)
                .forEach((DinamicVO<String, String> active) -> LogManager.info("Ya se encuentra bloqueada " + active.NVL("posicion")));
        LogManager.info("Posiciones bloqueadas!");
        return true;
    }//lock

    private boolean unlock() {

        if (open()) {
            LogManager.info("Desbloqueando " + actives.size() + " posiciones");
            actives.stream()
                    .filter(active -> (openOnly() || !"B".equalsIgnoreCase(active.NVL("estado"))))
                    .forEach((DinamicVO<String, String> active) -> {
                        createCommand(ComandosVO.newDesbloqueo(active.NVL(CMD_FIELDS.posicion.name()), corteOmicrom.id()));
                    });
            LogManager.info("Posiciones desbloqueadas!");
            resetDisplay();
        } else {
            DisplayDAO.closedDisplay();
        }

        return true;
    }//unlock

    protected final boolean open() {
        return PROCESS.CLOSEANDOPEN.equals(process) || PROCESS.OPEN.equals(process);
    }

    protected final boolean openOnly() {
        return PROCESS.OPEN.equals(process);
    }

    private ConsumoVO getConsumoOmicrom(String posicion, String manguera) throws DetiPOSFault {

        return ConsumosDAO.totalizaCorte(posicion, manguera, corteOmicrom.isla(), corteOmicrom.id());
    }//getImportesOmicrom

    private ConsumoVO newConsumo(ManguerasVO manguera, String posicion, BigDecimal importe, BigDecimal volumen, String idManguera, String idVendedor) {

        CombustibleVO combustible = productos.get(manguera.NVL(DSP_FIELDS.producto.name()));
        ConsumoVO consumo = ConsumoVO.getInstance();

        BigDecimal pesosp;
        BigDecimal volumenp;
        BigDecimal aux = new BigDecimal(100);
        BigDecimal factor = aux.subtract(new BigDecimal(manguera.NVL(DSP_FIELDS.factor.name())));
        BigDecimal cost = new BigDecimal(combustible.NVL(COM_FIELDS.precio.name()));
        
        if (BigDecimal.ONE.compareTo(volumen.multiply(cost).subtract(importe))<0) {
           importe = volumen.multiply(cost).setScale(2, RoundingMode.HALF_EVEN);
        }

        pesosp   = importe.multiply(factor).divide(aux).setScale(2, RoundingMode.HALF_EVEN);
        volumenp = volumen.multiply(factor).divide(aux).setScale(2, RoundingMode.HALF_EVEN);

        consumo.setField(RM_FIELDS.dispensario, manguera.NVL(DSP_FIELDS.dispensario.name()));
        consumo.setField(RM_FIELDS.posicion, posicion);
        consumo.setField(RM_FIELDS.manguera, idManguera);
        consumo.setField(RM_FIELDS.dis_mang, manguera.NVL(DSP_FIELDS.dis_mang.name()));
        consumo.setField(RM_FIELDS.producto, manguera.NVL(DSP_FIELDS.producto.name()));
        consumo.setField(RM_FIELDS.precio, combustible.NVL(COM_FIELDS.precio.name()));
        consumo.setField(RM_FIELDS.pesos, importe.toPlainString());
        consumo.setField(RM_FIELDS.volumen, volumen.toPlainString());
        consumo.setField(RM_FIELDS.pesosp, pesosp.toPlainString());
        consumo.setField(RM_FIELDS.volumenp, volumenp.toPlainString());
        consumo.setField(RM_FIELDS.factor, manguera.NVL(DSP_FIELDS.factor.name()));
        consumo.setField(RM_FIELDS.vendedor, idVendedor);
        consumo.setField(RM_FIELDS.turno, turno.NVL(TUR_FIELDS.id.name()));
        consumo.setField(RM_FIELDS.corte, corteOmicrom.id());
        consumo.setField(RM_FIELDS.iva, combustible.NVL(COM_FIELDS.iva.name()));
        consumo.setField(RM_FIELDS.ieps, combustible.NVL(COM_FIELDS.ieps.name()));

        return consumo;
    }//newConsumo

    private boolean insertRM(ConsumoVO consumo) {
        try {
            LogManager.info("Insertando consumo " + consumo);
            if (consumo.esMenor(LIMITE_CONSUMO)) {
                BaseDAO.insertAutoKey(MySQLDB.DB_NAME, (BaseVO) consumo);
                LogManager.debug(consumo);
                LogManager.info(String.format("Consumo insertado con id %s", consumo.NVL(RM_FIELDS.id.name())));
            } else {
                LogManager.info("Ajuste cancelado. No puede ser mayor que " + LIMITE_CONSUMO + " Litros");
            }
            return true;
        } catch (DBException ex) {
            OmicromLogManager.error("Error insertando consumo", ex);
        }
        return false;
    }//insertRM

    private boolean insertTransaction(BigDecimal difVolumen, BigDecimal difImporte, String posicion, String manguera) throws DetiPOSFault {

        ManguerasVO mangueraVO = new ManguerasVO();
        if (difVolumen.compareTo(BigDecimal.ZERO) > 0
                && difImporte.compareTo(BigDecimal.ZERO) > 0) {
            mangueraVO.setEntries(ManguerasDAO.getDispensarioAtPosicion(posicion, manguera));
            if (mangueraVO.isVoid()) {
                throw  new DetiPOSFault("Error recuperando manguera", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error recuperando manguera", "Posicion "+posicion));
            }
            return insertRM(newConsumo(mangueraVO, posicion, difImporte, difVolumen, manguera, mangueraVO.NVL("VENDEDOR", mangueraVO.NVL(DSP_FIELDS.posicion.name()))));
        }
        return false;
    }

    private boolean insertMissingTransaction(CorteDetalleVO detalle) throws DetiPOSFault {

        insertTransaction(detalle.getDifvolumen1(), detalle.getDifimporte1(), detalle.getPosicion(), "1");
        insertTransaction(detalle.getDifvolumen2(), detalle.getDifimporte2(), detalle.getPosicion(), "2");
        insertTransaction(detalle.getDifvolumen3(), detalle.getDifimporte3(), detalle.getPosicion(), "3");
        return true;
    }//insertMissingTransaction

    private void setDiferencia(CorteDetalleVO detalle) throws DetiPOSFault {

        if ("Bennett".equals(dispensarios)) {
            detalle.setOverflowLimit(CorteDetalleVO.OVERFLOW_BENNETT);
        }

        LogManager.debug(detalle);
        insertMissingTransaction(detalle);
    }//setDiferencia

    private boolean openTurnoIsla(String corteID) { //TODO move to DAO
        return IslaDAO.updateIsla(corteOmicrom.isla(), corteID, turno.NVL(TUR_FIELDS.turno.name()))
            && PosicionesDAO.updateDespachadoresTurno(corteOmicrom.isla());
    }//updateIsla

    protected boolean insertNewCorte() {

        try {
            if (open()) {
                LogManager.info("Creando nuevo corte");
                turno = TurnosDAO.getCurrentTurno(corteOmicrom.isla());
                CorteVO newCorte = CorteDAO.newCorte(turno.NVL(TUR_FIELDS.isla.name()), 
                        String.valueOf(turno.getCampoAsInt(TUR_FIELDS.turno.name())));
                String newCorteId = newCorte.NVL(CorteVO.CT_FIELDS.id.name());

                if (totalizadoresCorte.getTotalizadores().isEmpty()) {
                    TotalizadoresDAO.getTotalizadoresManPro(corteOmicrom.id())
                            .forEach((TotalizadoresVO totalizador) ->
                                CorteDetalleDAO.newDetalle(CorteDetalleVO.instance(totalizador, newCorteId)));
                } else {
                    PosicionesDAO.listDisconnected(turno.NVL(TUR_FIELDS.isla.name())).stream()
                            .forEach((DinamicVO<String, String> desconectado) -> 
                                    CorteDetalleDAO.newDetalle(CorteDetalleVO.instance(newCorteId, desconectado.NVL(PosicionesVO.DSP_FIELDS.posicion.name()))));

                    if (corteOmicrom.getDetalles().size()<totalizadoresCorte.getTotalizadores().size()) {
                        totalizadoresCorte.getTotalizadores().stream()
                                .forEach((TotalizadoresVO totalizador) -> CorteDetalleDAO.newDetalle(CorteDetalleVO.instance(totalizador, newCorte.NVL(CorteVO.CT_FIELDS.id.name()))));
                    } else {
                        corteOmicrom.getDetalles().stream()
                                .forEach((CorteDetalleVO detalle) ->
                                    CorteDetalleDAO.newDetalle(
                                            totalizadoresCorte.hasTotalizador(detalle.NVL(CorteDetalleVO.CDT_FIELDS.posicion.name())) ? 
                                                CorteDetalleVO.instance(totalizadoresCorte.getTotalizador(detalle.NVL(CorteDetalleVO.CDT_FIELDS.posicion.name())), newCorteId) : 
                                                corteOmicrom.cloneDetalle(newCorteId, detalle.NVL(CorteDetalleVO.CDT_FIELDS.posicion.name()))));
                    }
                }

                openTurnoIsla(newCorte.NVL(CorteVO.CT_FIELDS.id.name()));
                LogManager.info("Corte creado con éxito");
            } else {
                IslaDAO.closeIsla(corteOmicrom.isla());
                LogManager.info("La Isla cerrada con éxito");
            }
        } catch (DBException | DetiPOSFault ex) {
            OmicromLogManager.error("Error creando corte", ex);
        }
        return false;
    }//insetNewCorte

    protected void updateDetalle(CorteDetalleVO detalle) throws DBException, DetiPOSFault {

        try {
            setDiferencia(detalle);
        } catch (DetiPOSFault ex) {
            OmicromLogManager.error("Error insertando transaccion en RM", ex);
        }
    }

    private boolean insertWindow(CorteDetalleVO detalle, String idManguera) {
        ManguerasVO manguera = new ManguerasVO ();
        int dpos_window_size = Integer.parseInt(VariablesDAO.getCorporativo("dpos_corte_window_size", "5"));
        try {
            LogManager.debug("Insertando ventana "+idManguera);
            for (int i=0; i<dpos_window_size; i++) {
                manguera.setEntries(ManguerasDAO.getDispensarioAtPosicion(detalle.getPosicion(), idManguera));
                insertRM(newConsumo(manguera, detalle.getPosicion(), BigDecimal.ZERO, BigDecimal.ZERO, idManguera, manguera.NVL(DSP_FIELDS.posicion.name())));
            }
        } catch (DetiPOSFault ex) {
            OmicromLogManager.error("Error insertando ventana " + idManguera, ex);
            return false;
        }
        return true;
    }
    protected boolean insertConciliationWindow(CorteDetalleVO detalle) {
        insertWindow(detalle, "1");
        insertWindow(detalle, "2");
        insertWindow(detalle, "3");
        return true;
    }//insertConciliationWindow

    protected boolean executeAutoconsumo() {
        try {
            LogManager.info("Ejecutando función de autoconsumo");
            return BaseDAO.forceExecute(MySQLDB.DB_NAME, "{ call insertVentasAutoconsumo("+corteOmicrom.id()+") }");
        } catch (DBException ex) {
            OmicromLogManager.error("Error ejecutando función de autoconsumo", ex);
        }
        return false;
    }

    protected void waitTotalizadores() {
        Calendar waiting = Calendar.getInstance();
        waiting.add(Calendar.SECOND, 30);
        try {
            LogManager.info("Esperando totalizadores");
            while (TotalizadoresDAO.getTotalizadores(corteOmicrom.id()).size()<actives.size()
                        && DateUtils.fncbCompare(waiting, Calendar.getInstance(), DateUtils.MASK_GREATER)) {
                GenericSleeper.get().sleep();
            }
            if (TotalizadoresDAO.getTotalizadores(corteOmicrom.id()).size()<actives.size()) {
                LogManager.info("No se obtuvieron todos los totalizadores, algunos valores pueden ser calculados");
            }
        } catch (DBException ex) {
            OmicromLogManager.error("Error consultando totalizadores", ex);
            Thread.currentThread().interrupt();
        }
    }

    protected void initNotificationWindow() {

        if (client==CLIENT.POS) {
            if (null==FINISHED_DATE) {
                FINISHED_DATE = Calendar.getInstance();
                LogManager.info("El proceso ha terminado. Esperando al proceso padre "+DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", FINISHED_DATE, DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));
            }
        } else {
            stopCorte();
        }
    }//initNotificationWindow

    private boolean timeOut() {
        return (MAX_WAITING_TIME*DateUtils.MILIS_POR_SEGUNDO)<(Calendar.getInstance().getTimeInMillis()-FINISHED_DATE.getTimeInMillis());
    }

    private boolean autoStop() {

        if (null!=FINISHED_DATE && timeOut()) {
            LogManager.error("El proceso ha finalizado por tiempo de espera");
            stopCorte();
            return true;
        }
        return !running;
    }//autoStop

    private boolean handleError() {
        initNotificationWindow();
        return true;
    }

    private void next() {

        if (!autoStop()) {
            if (error) {
                step = STEP.error;
            }

            switch (step) {
            case locking:
                step = STEP.totalizing;
                previousStep = step;
                break;
            case totalizing: 
                step = STEP.matching;
                previousStep = step;
                break;
            case matching: 
                step = STEP.done;
                previousStep = step;
                break;
            }
        }//while 
    }//next

    private void logStartStatus() {
        LogManager.info("Corte Actual: ");
        LogManager.info(this.corteOmicrom);
        LogManager.info("Posiciones Activas: ");
        LogManager.info(actives);
    }

    private void executeCorte() {

        Calendar init = Calendar.getInstance();
        try {
            LogManager.info("Iniciando proceso de Corte " + DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", init));
            logStartStatus();
            while (running) {

                updateDisplay();

                switch (step) {
                case locking:
                    lock();
                    if ((error = waitCommands())) {
                        errorStep = STEP.error;
                    }
                    break;
                case totalizing: 
                    totalize();
                    if ((error = waitCommands())) {
                        errorStep = STEP.totalizing;
                    }
                    break;
                case matching:
                    matchTotalizers();
                    break;
                case error:
                    handleError();
                    break;
                }
                if (done) {
                    GenericSleeper.get().sleep();
                }
                next();
            }//while 
        } finally {
            if (null!=errorStep) {
                LogDAO.log("ct", corteOmicrom.id(), "ERROR: " + errorStep.name(), "corte_dpos");
            }
            unlock();
            Calendar end = Calendar.getInstance();
            LogManager.info("Proceso de Corte finalizado " + DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
            LogManager.info("Tiempo de ejecución " + ( end.getTimeInMillis()-init.getTimeInMillis() ) + " ms");
        }
    }

    @Override
    public void run() {

        running = true;
        executeCorte();
    }

    public void stopCorte() {

        LogManager.info("Finalizando proceso. Detenido por el proceso padre.");
        running = false;
    }

    public boolean isRunning() {

        return running;
    }
}
