package com.detisa.omicrom.integraciones.monederos.gasomatic.preset;

import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.detisa.omicrom.integraciones.monederos.gasomatic.GasoMaticApi;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_PIN_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.mx.detisa.integrations.gasomatic.Combustibles;
import com.mx.detisa.integrations.gasomatic.RespuestaAutorizar;
import com.mx.detisa.integrations.gasomatic.TipoCarga;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import javax.xml.ws.WebServiceException;
import lombok.Getter;

public class ConsumoTarjetaGasoMatic extends ConsumoIntegracion {

    private ClientesVO monedero;
    @Getter private RespuestaAutorizar auth;

    public ConsumoTarjetaGasoMatic() {
        super();
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
    }//init

    @Override
    public boolean validate() throws DetiPOSFault {

        try {
            super.validate();
            auth = new GasoMaticApi(parameters.NVL(Consumo.PRMTR_APP_NAME)).authorize(
                    parameters.NVL(PRMT_ACCOUNT), 
                    parameters.NVL(PRMT_PIN_ACCOUNT), 
                    isImporte() ? TipoCarga.PRECIO : TipoCarga.LITROS, 
                    Combustibles.valueOf(combustible.NVL(CombustibleVO.COM_FIELDS.descripcion.name())),
                    manguera.getCampoAsInt(ManguerasVO.DSP_FIELDS.posicion.name()),
                    getCant(),
                    combustible.getCampoAsDecimal(CombustibleVO.COM_FIELDS.precio.name()), 
                    combustible.getCampoAsDecimal(CombustibleVO.COM_FIELDS.ieps.name()), 
                    parameters.NVL(PRMT_CNS_ECO),
                    Integer.parseInt(parameters.NVL(PRMT_CNS_KM)));
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "AUTHORIZE", auth);

            LogManager.info("Validando Monedero GasoMatic " + auth);
            LogManager.info("Tarjeta GasoMatic " + parameters.NVL(PRMT_ACCOUNT));

            comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), "Consumo GassoMatic | " + parameters.NVL(PRMT_ACCOUNT), false);
            comando.setField(ComandosVO.CMD_FIELDS.idtarea, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
            LogManager.error(comando);
            posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));

        } catch (DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "GESASOMATIC EXCEPTION", ex.getMessage());
            LogManager.error(ex);
            throw ex;
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        }
    
        return true;
    }
}