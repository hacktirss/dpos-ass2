/*
 * ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.gasngo;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.detisa.omicrom.integraciones.monederos.gasngo.preset.ConsumoCodeGasngo;
import com.detisa.omicrom.integraciones.monederos.gasngo.preset.ConsumoGasngo;
import com.detisa.omicrom.integraciones.monederos.gasngo.preset.ConsumoTAGGasngo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.pattern.ComandoObserver;
import com.ass2.volumetrico.puntoventa.pattern.ComandoSubject;
import com.ass2.volumetrico.puntoventa.services.actions.ConsumoActionBase;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSInternalFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;

public class ConsumoAction extends ConsumoActionBase {

    public ConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        consumo = param.NVL(PRMT_PIN_ACCOUNT, "").length() == 0 ? new ConsumoCodeGasngo() : new ConsumoTAGGasngo();
    }

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
        ComandoObserver gngObserver = new ComandoObserver(
                new GasngoUpdater().setConsumo((ConsumoGasngo) consumo));
        comando.register(gngObserver);
        comando.initConsumo();
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        try {
            executed = executeConsumo();
            if (executed)  {
                initObserver();
            } else {
                consumo.cancel();
            }

            return new Comprobante()
                    .append("SALDO", StringUtils.isNVL(consumo.getSaldo()) ? "0" : consumo.getSaldo())
                    .append("PRESET", executed ? 
                                    "VENTA AUTORIZADA POR " + consumo.getAutorizadoTexto() : 
                                    "ERROR AUTORIZANDO VENTA");
        } catch (DetiPOSFault dpf) {
            String detail = (dpf.getFaultInfo() != null ? 
                                    dpf.getFaultInfo().getErrorMessage() + ":" + dpf.getFaultInfo().getErrorDetail() : 
                                    dpf.getMessage() ).toUpperCase().replaceAll(System.lineSeparator(), "");
            String error = ( estacion.isVoid() ? "" : ( estacion.NVL("CIA") + " (" + estacion.NVL("ESTACION") + "). " ) ) 
                                + detail + " " + DateUtils.fncsFormat("YYYY-MM-dd HH:mm:ss");
            LogManager.error(error);
            throw new DetiPOSFault("ERROR AUTORIZANDO VENTA :: " + error, new DetiPOSInternalFaultInfo(detail, error));
        }
    }
}
