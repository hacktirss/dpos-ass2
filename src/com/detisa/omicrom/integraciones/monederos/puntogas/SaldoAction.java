/*
 * SaldoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.puntogas;

import com.detisa.integrations.puntogas.SaldoRetun;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.actions.BaseAction;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.softcoatl.utils.logging.LogManager;

public class SaldoAction extends BaseAction {

    public SaldoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        parseAccountNumber();
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(PRMT_ACCOUNT)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro NUMERO DE CUENTA"));
        }
        return this;
    }

    private void parseAccountNumber() {
        String track = parameters.NVL(PRMT_ACCOUNT);

        if (track.contains("=")) {
            parameters.setField(PRMT_ACCOUNT, track.split("=")[0]);
        } else if (track.contains("^")) {
            parameters.setField(PRMT_ACCOUNT, track.split("\\^")[0]);
        }
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {

        Comprobante comprobante = super.getComprobante();
        PuntoGASApi api = new PuntoGASApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
        
        try {
            SaldoRetun cardData = api.request(
                                    parameters.NVL(PRMT_ACCOUNT), 
                                    parameters.NVL(PRMT_PIN_ACCOUNT), 
                                    estacion.NVL("LNUMESTACION").substring(1));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "SALDO", cardData);

            String tipoSaldo = Character.toString((char) cardData.getTipoSaldo());
            LogManager.info("Tipo de Saldo " + tipoSaldo);
            switch (tipoSaldo) {
                case "L":
                    comprobante.append("TKEY", "L").append("TIPO", "LITROS");
                    break;
                case "S":
                    comprobante.append("TKEY", "I").append("TIPO", "IMPORTE");
                    break;
                default:
                    throw new AssertionError();
            }

            comprobante.append("API", "Monedero GES");
            comprobante.append("SALDO", cardData.getSaldoCliente().toPlainString());
        
            return comprobante;
        } catch (DetiPOSFault ex) {
             BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "GES EXCEPTION", ex.getMessage());
             LogManager.error(ex);
             throw ex;
       }
    }
}