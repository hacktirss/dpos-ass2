/*
 * AcumulaAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.omicrom;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.ass2.volumetrico.puntoventa.services.actions.ImprimeTransaccionAction;
import com.softcoatl.utils.logging.LogManager;

public class AcumulaAction extends ImprimeTransaccionAction  {

    public AcumulaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        if (parameters.isNVL(PRMT_ACCOUNT)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro NUMERO DE CUENTA"));
        }
        if (parameters.isNVL(WS_PRMT_TRANSACCION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TRANSACCION"));
        }
        return this;
    }//validateRequest


    private Comprobante determinePuntos(int id, String account) {
        try {
            String result = PuntosOmicromApi.getTipoMonedero(id, "P", account);
            return new Comprobante()
                        .append("PNT_CONSUMO", result);
        } catch (DetiPOSFault ex) {
            LogManager.info("Error consultando puntos");
            LogManager.debug(ex);
        }
        return new Comprobante();
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        int idConsumo = Integer.parseInt(parameters.NVL(WS_PRMT_TRANSACCION));
        return new Comprobante()
                    .append(determinePuntos(idConsumo, parameters.NVL(PRMT_ACCOUNT)))
                    .append(super.getComprobante());
    }
}