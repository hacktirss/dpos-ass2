/*
 * ConsumoBoleto
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.detisa.fae.CheckBalance;
import com.detisa.fae.DataServer;
import com.detisa.fae.Exception_Exception;
import com.detisa.fae.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.stream.Collectors;

public class ConsumoBoletoCorporativo extends ConsumoBoleto {

    public ConsumoBoletoCorporativo() {
        super();
    }

    public void callRemote() throws DetiPOSFault {

        try {
            DataServer port = RMIClientFactory.getDataServerPort(VariablesDAO.getCorporativo("url_sync_data"), "DataServer", "DataServer", 10000);
            CheckBalance balance = port.validaConsumoVales(VariablesDAO.getIdFAE(), parameters.NVL(PRMT_ACCOUNT), "");
            LogManager.info("Asignando boletos autorizados " + balance.getCodigoVales());
            parameters.setField(PRMT_CNS_CNT, Double.toString(balance.getMontoAutorizado()));
            parameters.setField(PRMT_ACCOUNT, balance.getCodigoVales());
            setImporte(new BigDecimal(balance.getMontoAutorizado()));
        } catch (MalformedURLException | DetiPOSFault | Exception_Exception ex) {
            LogManager.error(ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    @Override
    protected void determineImporte() throws DetiPOSFault {
        parameters.setField(PRMT_CNS_TYP, ComandosVO.TIPO_CONSUMO.IMPORTE.name());
        parameters.setField(PRMT_ACCOUNT, requestedBoletos.stream().collect(Collectors.joining("|")));
        callRemote();
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        super.validate();

        if (getImporte().doubleValue()<=0) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "No se encontraron boletos validos."));
        }
        return true;
    }
}
