/*
* ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.omicrom;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.detisa.omicrom.integraciones.monederos.omicrom.preset.ConsumoPuntosOmicrom;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.pattern.ComandoObserver;
import com.ass2.volumetrico.puntoventa.pattern.ComandoSubject;
import com.ass2.volumetrico.puntoventa.services.actions.ConsumoActionBase;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;

public class ConsumoAction extends ConsumoActionBase {

    public ConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        consumo = new ConsumoPuntosOmicrom();
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(PRMT_CNS_TYP)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro TIPO DE CONSUMO"));
        }
        ComandosVO.TIPO_CONSUMO.validate(parameters.NVL(PRMT_CNS_TYP));
        if (!ComandosVO.TIPO_CONSUMO.LLENO.toString().equals(parameters.NVL(PRMT_CNS_TYP)) && parameters.isNVL(PRMT_CNS_CNT)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro CANTIDAD"));
        }
        return this;
    }

    protected void initObserver() {
        ComandoSubject comando = new ComandoSubject(consumo);
        ComandoObserver puntogasObserver = new ComandoObserver(new PuntosOmicromUpdater().setRequest(parameters));
        comando.register(puntogasObserver);
        comando.initConsumo();
    }
}
