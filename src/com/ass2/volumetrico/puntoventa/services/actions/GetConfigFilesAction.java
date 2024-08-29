package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.POSConfigDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.util.Base64;

public class GetConfigFilesAction extends BaseAction {

    public GetConfigFilesAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }
    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TERMINAL)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro POSICION"));
        }
        return this;
    }

    private Comprobante getConfiguration() throws DetiPOSFault {
        Comprobante configuration = new Comprobante();
        try {
            LogManager.info("Buscando archivos de configuración para la terminal " + terminal.NVL("POS_ID"));
            POSConfigDAO.getConfiguration("XML").forEach(item -> {
                    LogManager.debug(item.NVL("fileName"));
                    configuration.append(item.NVL("fileName"), Base64.getEncoder().encodeToString(item.NVL("fileContent").getBytes()));
            });
        } catch (DBException DBE) {
            throw new DetiPOSFault("Error recuperando configuracion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, DBE, "Error recuperando plantilla"));
        }
        return configuration;
    }
    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return getConfiguration();
    }
}
