/*
 * ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.controlcard;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.detisa.omicrom.integraciones.controlcard.preset.ConsumoPisoControlCard;
import com.detisa.omicrom.integraciones.controlcard.preset.ConsumoPisoControlCardLocal;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.detisa.omicrom.integraciones.controlcard.preset.ConsumoTarjetaControlCard;
import com.ass2.volumetrico.puntoventa.services.actions.ConsumoActionBase;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.utils.logging.LogManager;

public class ConsumoAction extends ConsumoActionBase {

    public ConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        if ("S".equals(param.NVL(PRMTR_APP_STATUS))) {
            LogManager.info("Venta modo autónomo");
            consumo = new ConsumoPisoControlCardLocal();
        } else if (param.isNVL(PRMT_EMPLOYEE)) {
            LogManager.info("Venta con Tarjeta");
            consumo = new ConsumoTarjetaControlCard();
        } else {
            LogManager.info("Venta de Piso");
            consumo = new ConsumoPisoControlCard();
        }
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if ("S".equals(parameters.NVL(PRMTR_APP_STATUS)) && parameters.isNVL(PRMT_EMPLOYEE)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Error de Invocacion", "El sistema trabaja en MODO AUTONOMO y no es posible autorizar ventas con tarjeta"));
        }
        if (parameters.isNVL(PRMT_ACCOUNT) && parameters.isNVL(PRMT_EMPLOYEE)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro NUMERO DE CUENTA"));
        }
        if (parameters.isNVL(PRMT_CNS_TYP)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro TIPO DE CONSUMO"));
        }
        ComandosVO.TIPO_CONSUMO.validate(parameters.NVL(PRMT_CNS_TYP));
        if (!ComandosVO.TIPO_CONSUMO.LLENO.toString().equals(parameters.NVL(PRMT_CNS_TYP)) && parameters.isNVL(PRMT_CNS_CNT)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro CANTIDAD"));
        }
        return this;
    }

    @Override
    protected void initObserver() {
        LogManager.debug("No observer defined.");
    }
}
