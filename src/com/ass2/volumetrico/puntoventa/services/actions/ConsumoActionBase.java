/*
 * ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSInternalFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;

public abstract class ConsumoActionBase extends BaseAction {

    public static final String CVE_ACTIVO = "Si";
    public static final int MAX_WAITING_TIME = 6000; //Seconds

    protected Consumo consumo;
    protected boolean executed = false;

    protected ConsumoActionBase(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    protected abstract void initObserver();

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(PRMT_ACCOUNT)) {
            throw new DetiPOSFault("Error", new DetiPOSParametersFaultInfo("Se esperaba el parametro NUMERO DE CUENTA"));
        }
        if (parameters.isNVL(PRMT_CNS_MNG)) {
            throw new DetiPOSFault("Error", new DetiPOSParametersFaultInfo("Se esperaba el parametro MANGUERA"));
        }
        return this;
    }

    protected boolean executeConsumo() throws DetiPOSFault {
        consumo.init(parameters);
        LogManager.debug(consumo);
        return consumo.validate() && consumo.exec();
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
            String detail = ( dpf.getFaultInfo() != null ? 
                                    dpf.getFaultInfo().getErrorMessage() + ":" + dpf.getFaultInfo().getErrorDetail() : 
                                    dpf.getMessage() ).toUpperCase().replaceAll(System.lineSeparator(), "");
            String error = ( estacion.isVoid() ? "" : ( estacion.NVL("CIA") + " (" + estacion.NVL("ESTACION") + "). " ) ) 
                                + detail + " Codigo " + parameters.NVL(PRMT_ACCOUNT) + " " + DateUtils.fncsFormat("YYYY-MM-dd HH:mm:ss");
            LogManager.error(detail);
            LogManager.error(error);
            throw new DetiPOSFault("ERROR AUTORIZANDO VENTA :: " + error, new DetiPOSInternalFaultInfo(detail, error));
        }
    }
}
