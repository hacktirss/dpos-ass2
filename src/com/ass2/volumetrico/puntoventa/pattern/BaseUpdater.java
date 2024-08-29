package com.ass2.volumetrico.puntoventa.pattern;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.utils.logging.LogManager;

public abstract class BaseUpdater implements ComandoUpdater {

    @Override
    public void onError(ComandosVO comando) {
        try {
            LogManager.info("Actualizando comando con error id " + comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
            LogManager.debug(comando);
            EstadoPosicionesDAO.updateStatus(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name()), "0", "", "0");
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        try {
            LogManager.info("Actualizando comando vencido id " + comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
            LogManager.debug(comando);
            EstadoPosicionesDAO.updateStatus(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name()), "0", "", "0");
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
    }
    
}
