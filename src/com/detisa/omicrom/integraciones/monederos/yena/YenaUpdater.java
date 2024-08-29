
package com.detisa.omicrom.integraciones.monederos.yena;

import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.detisa.integrations.scb.api.SCBException;
import com.mx.detisa.integrations.scb.order.OrderResponse;
import com.mx.detisa.integrations.scb.order.SaleResponse;
import com.mx.detisa.integrations.scb.payment.PaymentResponse;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class YenaUpdater extends BaseUpdater implements ComandoUpdater {

    private ComandosVO comando;
    private OrderResponse order;
    private PaymentResponse payment;
    private DinamicVO<String, String> parameters;

    public YenaUpdater setOrder(OrderResponse order) {
        this.order = order;
        return this;
    }

    public YenaUpdater setPayment(PaymentResponse payment) {
        this.payment = payment;
        return this;
    }

    public YenaUpdater setRequest(DinamicVO<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    private void saveAutorizacion(int folio, BigDecimal importe, Calendar fecha, PaymentResponse confirmacion) {
        ObjectMapper mapper = new ObjectMapper();
        String authSequence;

        try {
            authSequence = mapper.writeValueAsString(confirmacion);
            AutorizacionesrmDAO.evento(
                    folio,
                    fecha,
                    importe,
                    authSequence);
        } catch (JsonProcessingException ex) {
            LogManager.error(ex);
        }
    }

    private void confirmYENA() throws DetiPOSFault {
        YenaApi api = new YenaApi(parameters.NVL(Consumo.PRMTR_APP_NAME));

        try {
            EstadoPosicionesVO posicion;
            CombustibleVO combustible;
            ConsumoVO rm;

            posicion    = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
            rm          = ConsumosDAO.getByID(posicion.getFolio());
            combustible = CombustibleDAO.getCombustibleByClavei(rm.NVL(ConsumoVO.RM_FIELDS.producto.name()));

            boolean presetOk = rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()).compareTo(payment.getAmount())<=0;
            BigDecimal total = presetOk ? rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()).setScale(3, RoundingMode.HALF_EVEN) : payment.getAmount();
            BigDecimal subtotal = total.divide(new BigDecimal("1.16"), 3, RoundingMode.HALF_EVEN);

            // El importe del consumo es mayor al importe autorizado
            if (!presetOk) {
                LogManager.error("El consumo supera el monto previamente autorizado");
                ConsumosDAO.setPagoReal(rm.NVL(ConsumoVO.RM_FIELDS.id.name()), total);
            }
            
            PaymentResponse paymentConfirm = api.payentConfirm(
                                                    parameters.NVL(PRMT_ACCOUNT), 
                                                    order.getSessionID(),
                                                    payment.getAuthCode(),
                                                    combustible.NVL(CombustibleVO.COM_FIELDS.clave.name()),
                                                    rm.NVL(ConsumoVO.RM_FIELDS.volumen.name()),
                                                    subtotal.toPlainString(),
                                                    rm.NVL(ConsumoVO.RM_FIELDS.precio.name()),
                                                    total.toPlainString());
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "PAYMENT CONFIRM", paymentConfirm);

            SaleResponse orderConfirm = api.orderConfirm(
                                                    parameters.NVL(PRMT_ACCOUNT), 
                                                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()),
                                                    order.getSessionID());
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "ORDER CONFIRM", orderConfirm);

            saveAutorizacion(
                    rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()), 
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()), 
                    rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()), 
                    paymentConfirm);
        } catch (SCBException ex) {
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "YENA EXCEPTION", ex.getMessage());
            LogManager.error(ex);
            throw new DetiPOSFault(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
        try {
            confirmYENA();
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
    }

    @Override
    public void onError(ComandosVO comando) {
        super.onError(comando);
        BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "ERROR", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        super.onTimeout(comando);
        BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "TIMEOUT", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
    }

    @Override
    public void onPullback(ComandosVO comando) {
        // Do nothing
    }

    @Override
    public void onDispatching(ComandosVO comando) {
        // Do nothing
    }
}
