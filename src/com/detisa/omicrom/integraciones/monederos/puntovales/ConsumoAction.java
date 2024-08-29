/*
 * ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.puntovales;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.monederos.puntovales.preset.ConsumoPuntoVales;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.ass2.volumetrico.puntoventa.pattern.ComandoObserver;
import com.ass2.volumetrico.puntoventa.pattern.ComandoSubject;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.detisa.omicrom.puntogas.vales.Vale;
import com.ass2.volumetrico.puntoventa.services.actions.ConsumoActionBase;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSInternalFaultInfo;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.util.stream.Collectors;

public class ConsumoAction extends ConsumoActionBase {

    public ConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        consumo = new ConsumoPuntoVales();
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        return this;
    }

    protected void initObserver() {
        ComandoSubject comando = new ComandoSubject(consumo);
        ComandoObserver puntogasObserver = new ComandoObserver(new PuntoValesUpdater().setConsumo((ConsumoPuntoVales) consumo));
        comando.register(puntogasObserver);
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

            String aprobados = ((ConsumoPuntoVales) consumo).getResponse().getVales().stream().filter(item -> item.getStatus().equals("Activo")).map(Vale::getVale).collect(Collectors.joining("|"));
            return new Comprobante()
                    .append("SALDO", StringUtils.isNVL(consumo.getSaldo()) ? "0" : consumo.getSaldo())
                    .append("PRESET", executed ? 
                                    "VENTA AUTORIZADA POR " + consumo.getAutorizadoTexto() + " (APROBADOS " + aprobados + ")" : 
                                    "ERROR AUTORIZANDO VENTA");
        } catch (DetiPOSFault dpf) {
            String detail = ( dpf.getFaultInfo() != null ? 
                                    dpf.getFaultInfo().getErrorMessage() + ":" + dpf.getFaultInfo().getErrorDetail() : 
                                    dpf.getMessage() ).toUpperCase().replaceAll(System.lineSeparator(), "");
            String error = ( estacion.isVoid() ? "" : ( estacion.NVL("CIA") + " (" + estacion.NVL("ESTACION") + "). " ) ) 
                                + detail + " Codigo " + parameters.NVL(PRMT_ACCOUNT) + " " + DateUtils.fncsFormat("YYYY-MM-dd HH:mm:ss");
            LogManager.error(error);
            throw new DetiPOSFault("ERROR AUTORIZANDO VENTA :: " + error, new DetiPOSInternalFaultInfo(detail, error));
        }
    }
}
