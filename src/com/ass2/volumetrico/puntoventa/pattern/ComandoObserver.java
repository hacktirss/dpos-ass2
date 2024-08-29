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

import com.softcoatl.utils.logging.LogManager;

/**
 *
 * @author ROLANDO
 */
public class ComandoObserver implements Observer {
    
    public static final String CMD_INTENTOS = "1";
    public static final String CMD_EJECUCION = "1";

    private ComandoSubject subject;
    private ComandoUpdater updater;

    public ComandoObserver(ComandoUpdater updater) {
        this.updater = updater;
    }

    @Override
    public void update() {
        LogManager.info("Something happens");
        LogManager.info(subject.getComando());

        if (ComandoSubject.CONSUMO_STATE.PULLBACK.equals(subject.getState())) {
            LogManager.info("Manguera Descolgada");
            updater.onPullback(subject.getComando());
        } else if (ComandoSubject.CONSUMO_STATE.DISPATCHING.equals(subject.getState())) {
            LogManager.info("Despacho Iniciado");
            updater.onDispatching(subject.getComando());
        } else if (ComandoSubject.CONSUMO_STATE.ERROR.equals(subject.getState())) {
            LogManager.info("Error");
            subject.stopSubject();
            if (null!=updater) {
                updater.onError(subject.getComando());
            }
        } else if (ComandoSubject.CONSUMO_STATE.TIMEDOUT.equals(subject.getState())) {
            LogManager.info("Timeout");
            subject.stopSubject();
            if (null!=updater) {
                updater.onTimeout(subject.getComando());
            }
        } else {
            LogManager.info("All OK");
            subject.stopSubject();
            updater.onSuccess(subject.getComando());
        }
    }

    @Override
    public void setSubject(Subject subject) {
        this.subject = (ComandoSubject) subject;
    }    
    
    public void setUpdater(ComandoUpdater updater) {
        this.updater = updater;
    }
}
