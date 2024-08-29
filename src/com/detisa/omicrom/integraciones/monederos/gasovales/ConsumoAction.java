/*
 * ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.gasovales;

import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.detisa.omicrom.integraciones.monederos.gasovales.preset.ConsumoTarjetaGasoVales;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.ass2.volumetrico.puntoventa.pattern.ComandoObserver;
import com.ass2.volumetrico.puntoventa.pattern.ComandoSubject;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.actions.ConsumoActionBase;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.logging.LogManager;

public class ConsumoAction extends ConsumoActionBase {

    public ConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        consumo = new ConsumoTarjetaGasoVales();
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(Consumo.PRMT_CNS_MNG)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro MANGUERA"));
        }
        if (parameters.isNVL(Consumo.PRMT_EMPLOYEE)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro EMPLEADO"));
        }
        try {
            if (!VendedoresDAO.existsEmployee(parameters.NVL(Consumo.PRMT_EMPLOYEE))) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Número de empleado no registrado"));
            }
        } catch (DBException ex) {
            LogManager.error(ex);
            throw new DetiPOSFault("Error consultando vendedores");
        }
        return this;
    }

    protected void initObserver() {
        ComandoSubject comando = new ComandoSubject(consumo);
        ComandoObserver puntogasObserver = new ComandoObserver(new GasoValesUpdater().setConsumo((ConsumoTarjetaGasoVales) consumo));
        comando.register(puntogasObserver);
        comando.initConsumo();
    }
}
