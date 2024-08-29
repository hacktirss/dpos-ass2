package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.common.OmicromVO;
import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.GenericSleeper;
import com.softcoatl.utils.logging.LogManager;
import java.util.Calendar;

public class Command implements Runnable {

    public static final int MAX_WAITING_TIME = 30;
    private final int COMMAND_POOLING_TIME = 10;

    private Thread thread;
    private boolean running = false;

    private Calendar inited_date;

    private final ComandosVO comando;
    private final OmicromVO dispenser;
    private CommandObserver observer;

    private int tryCount = 0;
    private int waitingTime = MAX_WAITING_TIME;

    public Command(ComandosVO comando, DinamicVO<String, String> dispenser) {
        this.comando = comando;
        this.dispenser = new OmicromVO(dispenser);
        LogManager.debug("Monitor creado para el comando " + comando.NVL("id"));
    }//Constructor
    public ComandosVO getComando() {

        return comando;
    }//getComando
    public DinamicVO<String, String> getDispenser() {
        return dispenser;
    }
    public Command register(CommandObserver observer) {
        this.observer = observer;
        return this;
    }//register
    public Command setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
        return this;
    }
    public void notifyObserver() {
        observer.handleNotification(this);
    }//notifyObserver

    private boolean timeOut() {
        return (waitingTime*DateUtils.MILIS_POR_SEGUNDO)<(Calendar.getInstance().getTimeInMillis()-inited_date.getTimeInMillis());
    }

    private void checkComando() {

        ComandosVO.CMD_FIELDS ejecucion = ComandosVO.CMD_FIELDS.ejecucion;
        ComandosVO.CMD_FIELDS intentos = ComandosVO.CMD_FIELDS.intentos;

        ComandosVO current = ComandosDAO.getComando(comando.NVL(ComandosVO.CMD_FIELDS.id.name()));

        LogManager.debug("Esperando ejecución del comando " + comando.NVL("id"));
        if (timeOut()) {
            LogManager.info("Se venció el tiempo de espera del comando " + comando.NVL("id"));
            comando.setEntries(current);
            comando.setField(ComandosVO.CMD_FIELDS.ejecucion, "-1");
            notifyObserver();
        } else if (!comando.NVL(ejecucion.name()).equals(current.NVL(ejecucion.name()))
                && !comando.NVL(intentos.name()).equals(current.NVL(intentos.name()))) {
            comando.setEntries(current);

            LogManager.info(String.format("Cambió el estatus del comando %s, intentos %s, ejecución %s", comando.NVL("id"), comando.NVL("intentos"), comando.NVL("ejecucion")));
            if (comando.isError()) {
                LogManager.debug("Comando ejecutado con error");
                if (tryCount<2) {
                    LogManager.debug("Reiniciando comando");
                    ComandosDAO.resetComando(comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
                    current = ComandosDAO.getComando(comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
                    comando.setEntries(current);
                    tryCount++;
                } else {
                    LogManager.debug("Comando erróneo después de 2 reintentos.");                        
                    notifyObserver();
                }
            } else {
                notifyObserver();
            }
        }//Execution cheking

        GenericSleeper.get().setTimeout(COMMAND_POOLING_TIME).sleep();
    }//checkComando

    public void initCommand() {
        thread = new Thread(this, comando.NVL(ComandosVO.CMD_FIELDS.comando.name()));
        thread.start();
    }//initConsumo

    @Override
    public void run() {
        running = true;
        inited_date = Calendar.getInstance();
         while (running) {
            checkComando();
        }
        LogManager.info("Comando ejecutado " + comando.NVL("id"));
    }//run

    public void stop() {
        LogManager.debug("Comando detenido por el proceso principal");
        running = false;
    }//stop
}//Command
