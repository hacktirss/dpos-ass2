package com.detisa.omicrom.integraciones.monederos.yena.preset;

import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.detisa.omicrom.integraciones.monederos.yena.YenaApi;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.mx.detisa.integrations.scb.api.SCBException;
import com.mx.detisa.integrations.scb.card.CardData;
import com.mx.detisa.integrations.scb.order.OrderResponse;
import com.mx.detisa.integrations.scb.payment.PaymentResponse;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;
import lombok.Getter;

public class ConsumoTarjetaYena extends ConsumoIntegracion {

    private ClientesVO monedero;
    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    @Getter private OrderResponse order;
    @Getter private PaymentResponse payment;

    public ConsumoTarjetaYena() {
        super();
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        YenaApi api = new YenaApi((parameters.NVL(Consumo.PRMTR_APP_NAME)));

        try {
            super.validate();
            parameters.setField("isImporte", isImporte() ? "1" : "0");
            CardData card = api.request(parameters.NVL(PRMT_ACCOUNT)); 
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "REQUEST", card);

            BigDecimal precio = combustible.getCampoAsDecimal(CombustibleVO.COM_FIELDS.precio.name());
            BigDecimal subtotal = getImporte().divide(new BigDecimal("1.16"), 3, RoundingMode.HALF_EVEN);

            order = api.orderCapture(
                            parameters.NVL(PRMT_ACCOUNT), 
                            parameters.NVL(PRMT_PIN_ACCOUNT), 
                            manguera.NVL(ManguerasVO.DSP_FIELDS.posicion.name()), 
                            getVolumen().toPlainString(), 
                            subtotal.toPlainString(), 
                            precio.toPlainString(),
                            getImporte().toPlainString(), 
                            combustible.NVL(CombustibleVO.COM_FIELDS.clave.name()));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "ORDER CAPTURE", order);

            payment = api.payentCapture(
                            parameters.NVL(PRMT_ACCOUNT), 
                            parameters.NVL(PRMT_PIN_ACCOUNT), 
                            order.getSessionID(), 
                            getImporte().toPlainString(), 
                            parameters.NVL(PRMT_CNS_KM, "0"));
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "PAYMENT CAPTURE", payment);

            if (getImporte().compareTo(CARGA_MINIMA)<=0) {
                throw new DetiPOSFault("El importe autorizado es menor a la carga minima definida en " + CARGA_MINIMA + " pesos");
            }

            comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), "Consumo YENA" + "|" + parameters.NVL(PRMT_ACCOUNT), false);
            comando.setField(ComandosVO.CMD_FIELDS.idtarea, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
            posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        } catch (NoSuchAlgorithmException | DatatypeConfigurationException ex) {
            Logger.getLogger(ConsumoTarjetaYena.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SCBException ex) {
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "YENA EXCEPTION", ex.getMessage());
            LogManager.error(ex);
            throw new DetiPOSFault(ex.getMessage());
        }
        return true;
    }
}