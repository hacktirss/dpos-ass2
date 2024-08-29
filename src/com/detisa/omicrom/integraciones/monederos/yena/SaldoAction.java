/*
 * ConsumoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.yena;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.services.actions.BaseAction;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.mx.detisa.integrations.scb.api.SCBException;
import com.mx.detisa.integrations.scb.card.CardData;
import com.softcoatl.utils.logging.LogManager;
import java.security.NoSuchAlgorithmException;

public class SaldoAction extends BaseAction {
    public SaldoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
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

        YenaApi api = new YenaApi((parameters.NVL(Consumo.PRMTR_APP_NAME)));
        try {
            Comprobante comprobante = super.getComprobante();
            CardData cardData = api.request(parameters.NVL(PRMT_ACCOUNT));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "SALDO", cardData);
            comprobante
                    .append("API", "YENA")
                    .append("TIPO", cardData.getBenefitName())
                    .append("TKEY", "Monedero".equals(cardData.getBenefitName()) ? "A" : "N")
                    .append("SALDO", cardData.getCurrentMoney().toPlainString());
            
            return comprobante;
        } catch (SCBException ex) {
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "YENA EXCEPTION", ex.getMessage());
            LogManager.error(ex);
            throw new DetiPOSFault(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }
}
