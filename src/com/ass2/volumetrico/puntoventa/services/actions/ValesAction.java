/**
 * ValesAction DetiPOS WEB Service
 * Fuel Coupon based transaction, validates coupon's balance and sets dispenser command
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BoletosDAO;
import com.ass2.volumetrico.puntoventa.data.BoletosVO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ValesUpdater;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.pattern.ComandoObserver;
import com.ass2.volumetrico.puntoventa.pattern.ComandoSubject;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.preset.ConsumoBoleto;
import com.ass2.volumetrico.puntoventa.preset.ConsumoBoletoCorporativo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.util.stream.IntStream;

public class ValesAction extends BaseAction {

    public static final String PRMT_VALE = "idVale";

    private Consumo consumo;

    public ValesAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//ValesAction

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();

        if (!BoletosDAO.verifyUniqueClientID(parameters.NVL(PRMT_VALE).split("[|]"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se recibieron boletos de mas de un cliente"));
        }
        if (parameters.isNVL(PRMT_VALE)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro VALE"));
        }

        return this;
    }//validateRequest

    private boolean executeConsumo() throws DetiPOSFault {
        ClientesVO cliente = ClientesDAO.getClienteByID(BoletosDAO.getClientID(parameters.NVL(PRMT_VALE).split("[|]")));

        if ("S".equals(VariablesDAO.getCorporativo("boleto_corporativo", "N")) && "1".equals(cliente.NVL("CORPORATIVO"))) {
            consumo = new ConsumoBoletoCorporativo();
        } else {
            consumo = new ConsumoBoleto();
        }

        consumo.init(parameters);
        LogManager.debug(consumo);
        return consumo.validate() && consumo.exec();
    }

    private void initObserver() {
        ComandoSubject comando = new ComandoSubject(consumo);
        ComandoObserver valesObserver = new ComandoObserver(new ValesUpdater().setBoletos(((ConsumoBoleto) consumo).getRequestedBoletos()));
        comando.register(valesObserver);
        comando.initConsumo();
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {

        Comprobante comprobante = super.getComprobante();
        if (executeConsumo()) {
            initObserver();
            IntStream.range(0, ((ConsumoBoleto) consumo).getBoletos().size())
                    .forEach(idx -> {
                        BoletosVO boleto = ((ConsumoBoleto) consumo).getBoletos().get(idx);
                        comprobante.append("BOLETO" + idx, boleto.NVL("BOL_"+BoletosVO.B_FIELDS.codigo));
                        comprobante.append("VALID@BOLETO" + idx, boleto.NVL("BOL_"+BoletosVO.B_FIELDS.vigente));
                        comprobante.append("SALDO@BOLETO" + idx, boleto.getSaldo("BOL_").toPlainString());
                    });

            comprobante.append("PRESET", "VENTA AUTORIZADA");
        } else {
            comprobante.append("PRESET", "ERROR AUTORIZANDO VENTA");
        }
        return comprobante;
    }
}