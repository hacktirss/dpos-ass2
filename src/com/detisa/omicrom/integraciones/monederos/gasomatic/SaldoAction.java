/*
 * SaldoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.gasomatic;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.actions.BaseAction;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.mx.detisa.integrations.gasomatic.RespuestaSaldoTarjeta;
import com.softcoatl.utils.logging.LogManager;

public class SaldoAction extends BaseAction {

    public SaldoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        parameters.setField(PRMT_ACCOUNT, parameters.NVL(PRMT_ACCOUNT).replaceAll("=", ""));
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(PRMT_ACCOUNT)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro NUMERO DE CUENTA"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {

        Comprobante comprobante = super.getComprobante();
        GasoMaticApi api = new GasoMaticApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
        
        try {
            RespuestaSaldoTarjeta saldo = api.request(parameters.NVL(PRMT_ACCOUNT));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "SALDO", saldo);

            comprobante.append("TKEY", "I").append("TIPO", "IMPORTE");
            comprobante.append("API", "Monedero GES");
            comprobante.append("SALDO", saldo.getSaldo().toPlainString());
        
            return comprobante;
        } catch (DetiPOSFault ex) {
             BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "GES EXCEPTION", ex.getMessage());
             LogManager.error(ex);
             throw ex;
       }
    }
}