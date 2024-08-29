package com.detisa.omicrom.integraciones.monederos.puntogas.preset;

import com.detisa.integrations.puntogas.SaldoRetun;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.EstacionDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.detisa.omicrom.integraciones.monederos.puntogas.CombustiblesPuntoGAS;
import com.detisa.omicrom.integraciones.monederos.puntogas.PuntoGASApi;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_PIN_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.xml.ws.WebServiceException;

public class ConsumoTarjetaPuntoGas extends ConsumoIntegracion {

    private ClientesVO monedero;
    private DinamicVO<String, String> estacion;
    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    public ConsumoTarjetaPuntoGas() {
        super();
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
        estacion = EstacionDAO.getDatosEstacion();
        parseAccountNumber();
    }//init

    private void parseAccountNumber() {
        String track = parameters.NVL(PRMT_ACCOUNT);

        if (track.contains("=")) {
            parameters.setField(PRMT_ACCOUNT, track.split("=")[0]);
        } else if (track.contains("^")) {
            parameters.setField(PRMT_ACCOUNT, track.split("\\^")[0]);
        }
    }

    @Override
    public boolean validate() throws DetiPOSFault {

        PuntoGASApi api = new PuntoGASApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
        SaldoRetun cardBalance;
        SaldoRetun cardData;

        try {
            super.validate();
            parameters.setField("isImporte", isImporte() ? "1" : "0");
            cardBalance = api.request(
                    parameters.NVL(PRMT_ACCOUNT), 
                    parameters.NVL(PRMT_PIN_ACCOUNT), 
                    estacion.NVL("LNUMESTACION").substring(1));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "REQUEST", cardBalance);

            cardData = api.requestKM(
                    parameters.NVL(PRMT_ACCOUNT), 
                    parameters.NVL(PRMT_PIN_ACCOUNT), 
                    parameters.NVL(PRMT_CNS_KM, "0"), 
                    estacion.NVL("LNUMESTACION").substring(1));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "REQUEST KM", cardData);

            LogManager.info("Validando Monedero GES " + cardData);
            String tipoSaldo = Character.toString((char) cardData.getTipoSaldo());
            LogManager.info("Tarjeta PuntoGAS " + parameters.NVL(PRMT_ACCOUNT) + " Tipo de Consumo " + tipoSaldo);

            if ("L".equals(tipoSaldo)) {
                transform("VOLUMEN", getVolumen().setScale(2, RoundingMode.HALF_EVEN).toPlainString());
            } else if  ("S".equals(tipoSaldo))  {
                transform("IMPORTE", getImporte().setScale(2, RoundingMode.HALF_EVEN).toPlainString());
            }

            SaldoRetun balance = api.authorize(
                                        parameters.NVL(PRMT_ACCOUNT), 
                                        parameters.NVL(PRMT_PIN_ACCOUNT),
                                        manguera.NVL(ManguerasVO.DSP_FIELDS.posicion.name()),
                                        getCant().toPlainString(), 
                                        estacion.NVL("LNUMESTACION").substring(1), 
                                        parameters.NVL(PRMT_CNS_KM, "0"),
                                        isImporte());
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "AUTHORIZE", balance);

            if (balance.getSaldoCliente().compareTo(getCant())<0) {
                if ("S".equals(tipoSaldo)) {
                    transform("IMPORTE", balance.getSaldoCliente().toPlainString());
                } else if ("L".equals(tipoSaldo)) {
                    transform("VOLUMEN", balance.getSaldoCliente().toPlainString());
                }
            }

            if (!CombustiblesPuntoGAS.match(balance.getCombustibles().value(), combustible.NVL(CombustibleVO.COM_FIELDS.clave.name()))) {
                throw new DetiPOSFault("Combustible no autorizado. Combustibles válidos " + balance.getCombustibles().name());
            }

            if (getImporte().compareTo(CARGA_MINIMA)<=0) {
                throw new DetiPOSFault("El importe autorizado es menor a la carga minima definida en " + CARGA_MINIMA + " pesos");
            }

            comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), "Consumo PuntoGas | " + parameters.NVL(PRMT_ACCOUNT), false);
            comando.setField(ComandosVO.CMD_FIELDS.idtarea, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
            LogManager.error(comando);
            posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));

        } catch (DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "GES EXCEPTION", ex.getMessage());
            LogManager.error(ex);
            throw ex;
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        }
    
        return true;
    }
}