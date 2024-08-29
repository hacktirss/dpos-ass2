package com.detisa.omicrom.integraciones.monederos.gasovales.preset;

import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.detisa.omicrom.integraciones.monederos.gasovales.GasoValesApi;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.mx.detisa.integrations.gasovales.GESSession;
import com.mx.detisa.integrations.gasovales.GasovalesValeAplicado;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.WebServiceException;
import lombok.Getter;

public class ConsumoTarjetaGasoVales extends ConsumoIntegracion {

    private ClientesVO monedero;
    private GasoValesApi gasoValesApi;
    @Getter private List<GasovalesValeAplicado> gasovales;
    @Getter private GESSession session;

    public ConsumoTarjetaGasoVales() throws DetiPOSFault {
        super();
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
        gasoValesApi = new GasoValesApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
        session = gasoValesApi.createSession(manguera.getCampoAsInt(ManguerasVO.DSP_FIELDS.posicion.name()), parameters.NVL(Consumo.PRMT_EMPLOYEE));
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        try {
            super.validate();
            List<String> vales = Arrays.stream(parameters.NVL(PRMT_ACCOUNT).split("[|]")).distinct().collect(Collectors.toList());
            gasovales = gasoValesApi.authorize(session, vales);
            BigDecimal saldo = gasovales.stream().map(GasovalesValeAplicado::getImporte).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (saldo.compareTo(BigDecimal.ZERO)>0) {
                transform("IMPORTE", saldo.toPlainString());
                comando = ComandosVO.parse(manguera, "IMPORTE", saldo.toPlainString(), "Consumo GASOVALES", false);
                comando.setField(ComandosVO.CMD_FIELDS.idtarea, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
                LogManager.error(comando);
                posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
            } else {
                throw new DetiPOSFault("TODOS LOS VALES HAN SIDO APLICADOS");
            }
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        }
    
        return true;
    }
}