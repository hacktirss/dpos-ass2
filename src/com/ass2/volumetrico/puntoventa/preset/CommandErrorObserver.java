package com.ass2.volumetrico.puntoventa.preset;

import com.detisa.omicrom.bussiness.Command;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.softcoatl.utils.logging.LogManager;

public class CommandErrorObserver implements com.detisa.omicrom.bussiness.CommandObserver {

    @Override
    public void handleNotification(Command command) {

        LogManager.info("Notificación del comando " + command.getComando().NVL(ComandosVO.CMD_FIELDS.comando.name()));
        if (command.getComando().isError()) {
            AutorizacionesDAO.vencido(command.getComando().getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
        }
    }
}
