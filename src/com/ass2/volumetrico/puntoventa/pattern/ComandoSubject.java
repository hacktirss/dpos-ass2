/**
 * ValesAction DetiPOS WEB Service
 * Fuel Voucher based transaction, validates vouchers, voucher credit and sets dispenser command
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
package com.ass2.volumetrico.puntoventa.pattern;

import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.GenericSleeper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComandoSubject implements Subject, Runnable {
    
    public static enum CONSUMO_STATE {
        PRESET,
        PULLBACK,
        DISPATCHING,
        ENDED,
        TIMEDOUT,
        ERROR
    }

    public static final String CMD_INTENTOS = "1";
    public static final String CMD_EJECUCION = "1";

    private final Consumo CONSUMO;
    private CONSUMO_STATE state = CONSUMO_STATE.PRESET;

    private final ComandosVO COMANDO;
    private final EstadoPosicionesVO POSICION;
    private ConsumoVO COMPROBANTE;

    private final List<Observer> observers = new ArrayList<>();

    private Thread thread;

    private long inactive = 0;
    private boolean running = false;

    public ComandoSubject(Consumo consumo) {
        CONSUMO = consumo;
        POSICION = (EstadoPosicionesVO) CONSUMO.getPosicion();
        COMANDO = (ComandosVO) CONSUMO.getComando();
        eventOcurred();
    }

    public void initConsumo() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        running = true;
        checkForStatus();
    }

    public void stopSubject() {
        running = false;
    }

    @Override
    public Object getState() {
        return state;
    }

    @Override
    public void error() {
        state = CONSUMO_STATE.ERROR;
        notifyObservers();
    }//error

    @Override
    public void timeout() {
        state = CONSUMO_STATE.TIMEDOUT;
        notifyObservers();
    }

    private void eventOcurred() {
        inactive = Calendar.getInstance().getTimeInMillis()+(120 * DateUtils.MILIS_POR_SEGUNDO);
    }

    private boolean checkComando() {

        ComandosVO.CMD_FIELDS ejecucion = ComandosVO.CMD_FIELDS.ejecucion;
        ComandosVO.CMD_FIELDS intentos = ComandosVO.CMD_FIELDS.intentos;

        ComandosVO current = ComandosDAO.getComando(COMANDO.NVL(ComandosVO.CMD_FIELDS.id.name()));
        LogManager.debug("Current comando "+current);

        if (!COMANDO.NVL(ejecucion.name()).equals(current.NVL(ejecucion.name()))
                && !COMANDO.NVL(intentos.name()).equals(current.NVL(intentos.name()))) {
            COMANDO.setEntries(current);
            eventOcurred();

            LogManager.info("Comando has changed");
            if (COMANDO.isExecuted()) {
                state = CONSUMO_STATE.PULLBACK;
                LogManager.info("Pulling back sleeve");
                return true;
            } else if (COMANDO.isError()) {
                state = CONSUMO_STATE.ERROR;
                LogManager.info("Something was wrong");
            }
            notifyObservers();
        }
        return false;
    }

    private boolean checkPosicion() {

        EstadoPosicionesVO current = EstadoPosicionesDAO.getByID(POSICION.getId());
        LogManager.debug("Current Posicion "+current);

        if (current.isPumping()) {
            eventOcurred();
            LogManager.debug("Fuel is beign pumped! Current sale "+current.NVL(EstadoPosicionesVO.EP_FIELDS.venta.name()));
        }

        if (!POSICION.getEstado().equals(current.getEstado())) {
            LogManager.info("Posicion has changed: from status " + POSICION.getEstado() + " to " + current.getEstado());
            eventOcurred();

            if (POSICION.isBlocked() && current.isEnabled()) {
                LogManager.info("Position unlocked!");
            } else {
                if (current.isPumping()) {
                    LogManager.info("Sale has started!");
                    state = CONSUMO_STATE.PULLBACK;
                } else if (current.isEnabled() || current.isBlocked()) {
                    LogManager.info("Sale has ended!");
                    state = CONSUMO_STATE.ENDED;
                }
                notifyObservers();
            }
            POSICION.setEntries(current);
            return true;
        }
        return false;
    }

    private boolean checkComprobante() {
        ConsumoVO comprobanteCXC;
        int tries = 0;

        LogManager.info("Looking for rm sale with id " + POSICION);
        
        do {
            COMPROBANTE = ConsumosDAO.getByID(POSICION.getFolio());
            comprobanteCXC = ConsumosDAO.getByIDCXC(POSICION.getPosicion(), POSICION.getFolio());
            LogManager.debug("Current rm " + COMPROBANTE);

            if (COMPROBANTE.isVoid() && comprobanteCXC.isVoid()) {
                GenericSleeper.get().setTimeout(100).sleep();
            } else if (COMPROBANTE.isCompleted()) {
                LogManager.info("Successful operation. Returning sale with id " + COMPROBANTE.getId());
                state = CONSUMO_STATE.ENDED;
                notifyObservers();
                return true;
            }
        } while(tries++ < 10);

        LogManager.info("Can't retrieve rm sale with id " + POSICION.getFolio());
        error();
        return false;
    }

    private void checkForStatus() {

        LogManager.debug("Inited");

        while (running) {
            if (inactive<=Calendar.getInstance().getTimeInMillis()) {
                timeout();
                break;
            }
            LogManager.debug("Pooling " + state);

            switch (state) {
            case PRESET: 
                if (!checkComando()) {
                    checkPosicion();
                }
                break;
            case PULLBACK: 
                checkPosicion();
                break;
            case DISPATCHING:
                checkComprobante();
                break;
            case ENDED:
                return;
            }
            GenericSleeper.get().setTimeout(1000).sleep();
        }
        LogManager.debug("exit");
    }

    @Override
    public void register(Observer observer) {
        if (null==observer) throw new NullPointerException("Null observer");
        synchronized (CONSUMO) {
            if (!observers.contains(observer)) {
                observer.setSubject(this);
                observers.add(observer);
            }
        }
    }

    @Override
    public void unregister(Observer observer) {
        synchronized (CONSUMO) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        observers.forEach(localObserver -> localObserver.update());
    }

    @Override
    public Consumo getConsumo() {
        return CONSUMO;
    }    

    @Override
    public ComandosVO getComando() {
        return (ComandosVO) CONSUMO.getComando();
    }

    @Override
    public EstadoPosicionesVO getPosicion() {
        return (EstadoPosicionesVO) CONSUMO.getPosicion();
    }

    @Override
    public ConsumoVO getComprobante() {
        return COMPROBANTE;
    }
}
