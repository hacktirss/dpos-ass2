
package com.detisa.omicrom.integraciones.monederos.yena;

import com.mx.detisa.integrations.scb.card.CardData;
import com.mx.detisa.integrations.scb.card.CardFind;
import com.mx.detisa.integrations.scb.order.OrderRequest;
import com.mx.detisa.integrations.scb.order.OrderResponse;
import com.mx.detisa.integrations.scb.order.RequestObjectOfOrderRequest;
import com.ass2.volumetrico.puntoventa.data.EndpointDAO;
import com.ass2.volumetrico.puntoventa.data.EndpointVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.mx.detisa.integrations.scb.api.Commons;
import com.mx.detisa.integrations.scb.api.SCBCardService;
import com.mx.detisa.integrations.scb.api.SCBException;
import com.mx.detisa.integrations.scb.api.SCBOrderService;
import com.mx.detisa.integrations.scb.api.SCBPaymentService;
import com.mx.detisa.integrations.scb.card.RequestObjectOfCardFind;
import com.mx.detisa.integrations.scb.order.ItemRequest;
import com.mx.detisa.integrations.scb.order.OrderDirect;
import com.mx.detisa.integrations.scb.order.PaymentType;
import com.mx.detisa.integrations.scb.order.RequestObjectOfOrderDirect;
import com.mx.detisa.integrations.scb.order.RequestObjectOfSaleRequest;
import com.mx.detisa.integrations.scb.order.SalePayment;
import com.mx.detisa.integrations.scb.order.SalePost;
import com.mx.detisa.integrations.scb.order.SaleRequest;
import com.mx.detisa.integrations.scb.order.SaleResponse;
import com.mx.detisa.integrations.scb.payment.PaymentRequest;
import com.mx.detisa.integrations.scb.payment.PaymentResponse;
import com.mx.detisa.integrations.scb.payment.PaymentUpdate;
import com.mx.detisa.integrations.scb.payment.RequestObjectOfPaymentRequest;
import com.mx.detisa.integrations.scb.payment.RequestObjectOfPaymentUpdate;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;

public class YenaApi {

    EndpointVO endpoint;
    @Getter private final String wallet;

    public YenaApi(String wallet) throws DetiPOSFault {
        this.wallet = wallet;
        configure();
    }
    
    private void configure() throws DetiPOSFault {
        endpoint = EndpointDAO.get(wallet, "WALLET");
    }

    public CardData request(String cardNumber) throws NoSuchAlgorithmException, SCBException {

        SCBCardService service = new SCBCardService(endpoint.getUrl_webservice());

        String uuid = UUID.randomUUID().toString();
        com.mx.detisa.integrations.scb.card.ObjectFactory of = new com.mx.detisa.integrations.scb.card.ObjectFactory();

        RequestObjectOfCardFind request = of.createRequestObjectOfCardFind();
        request.setSignature(Commons.signature(endpoint.getUsuario(), uuid, endpoint.getPassword()));
        request.setUserName(endpoint.getUsuario());
        request.setToken(uuid);

        CardFind cf = of.createCardFind();
        cf.setNumber(cardNumber);
        cf.setCampaignID("1");
        request.setContent(cf);

        LogManager.info("YENA Card Request " + request);
        CardData cardData = service.find(request);
        LogManager.info("YENA Card " + cardData);
        
        return cardData;
    }

    public OrderResponse orderCapture(
            String cardNumber, 
            String cardKey, 
            String posicion,
            String cantidad,
            String subtotal,
            String precio,
            String total,
            String combustible) throws NoSuchAlgorithmException, SCBException, DatatypeConfigurationException {

        SCBOrderService service = new SCBOrderService(endpoint.getUrl_webservice());

        String uuid = UUID.randomUUID().toString();
        com.mx.detisa.integrations.scb.order.ObjectFactory of = new com.mx.detisa.integrations.scb.order.ObjectFactory();

        RequestObjectOfOrderRequest request = of.createRequestObjectOfOrderRequest();
        request.setSignature(Commons.signature(endpoint.getUsuario(), uuid, endpoint.getPassword()));
        request.setUserName(endpoint.getUsuario());
        request.setToken(uuid);

        OrderRequest cf = new OrderRequest();
        cf.setCampaignID("1");
        cf.setCardNumber(cardNumber);
        cf.setCardKey(cardKey);
        cf.setCardSource(com.mx.detisa.integrations.scb.order.CardSource.MagneticStripe);
        cf.setTypeName(com.mx.detisa.integrations.scb.order.OrderType.Redemption);
        cf.setTerminalCode(posicion);
        cf.setCashierCode("Posicion" + posicion);
        cf.setTicketNumber(DateUtils.fncsFormat("yyyyMMdd'|'HHmmss"));
        cf.setTransactionDate(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

        ItemRequest ir = of.createItemRequest();
        ir.setBarCode(combustible);
        ir.setQuantity(new BigDecimal(cantidad).setScale(3, RoundingMode.HALF_EVEN));
        ir.setPrice(new BigDecimal(precio).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTaxRate(new BigDecimal("0.16").setScale(3, RoundingMode.HALF_EVEN));
        ir.setSubTotal(new BigDecimal(subtotal).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTaxAmount(new BigDecimal(subtotal).multiply(new BigDecimal("0.16")).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTotal(new BigDecimal(total).setScale(3, RoundingMode.HALF_EVEN));
        LogManager.info(ir);

        List<ItemRequest> itemsRequest = new ArrayList<>();
        itemsRequest.add(ir);

        cf.setItems(itemsRequest);
        request.setContent(cf);

        LogManager.info("YENA Card Request " + request);
        OrderResponse order = service.capture(request);
        LogManager.info("YENA Card " + order);

        return order;
    }
    
    public SalePost orderDirect(
            String cardNumber, 
            String cardKey, 
            String posicion,
            String ticket,
            Calendar fecha,
            String cantidad,
            String subtotal,
            String precio,
            String total,
            String combustible) throws NoSuchAlgorithmException, SCBException, DatatypeConfigurationException {

        SCBOrderService service = new SCBOrderService(endpoint.getUrl_webservice());

        String uuid = UUID.randomUUID().toString();
        com.mx.detisa.integrations.scb.order.ObjectFactory of = new com.mx.detisa.integrations.scb.order.ObjectFactory();

        RequestObjectOfOrderDirect request = of.createRequestObjectOfOrderDirect();
        request.setSignature(Commons.signature(endpoint.getUsuario(), uuid, endpoint.getPassword()));
        request.setUserName(endpoint.getUsuario());
        request.setToken(uuid);

        String ticketNumber = DateUtils.fncsFormat("yyDDD", fecha) + StringUtils.fncsLeftPadding(ticket, '0', 12);

        LogManager.debug("Requesting Ticket " + ticketNumber);

        OrderDirect cf = new OrderDirect();
        cf.setCampaignID("1");
        cf.setCardNumber(cardNumber);
        cf.setCardKey(cardKey);
        cf.setCardSource(com.mx.detisa.integrations.scb.order.CardSource.MagneticStripe);
        cf.setTypeName(com.mx.detisa.integrations.scb.order.OrderType.Accumulation);
        cf.setTerminalCode(posicion);
        cf.setCashierCode("Posicion" + posicion);
        cf.setTicketNumber(ticketNumber);
        cf.setTransactionDate(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) fecha));

        ItemRequest ir = of.createItemRequest();
        ir.setBarCode(combustible);
        ir.setQuantity(new BigDecimal(cantidad).setScale(3, RoundingMode.HALF_EVEN));
        ir.setPrice(new BigDecimal(precio).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTaxRate(new BigDecimal("0.16").setScale(3, RoundingMode.HALF_EVEN));
        ir.setSubTotal(new BigDecimal(subtotal).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTaxAmount(new BigDecimal(subtotal).multiply(new BigDecimal("0.16")).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTotal(new BigDecimal(total).setScale(3, RoundingMode.HALF_EVEN));

        List<ItemRequest> itemsRequest = new ArrayList<>();
        itemsRequest.add(ir);
        cf.setItems(itemsRequest);

        SalePayment sp = of.createSalePayment();
        sp.setMethodName(PaymentType.EFECTIVO);
        sp.setAmount(new BigDecimal(total).setScale(3, RoundingMode.HALF_EVEN));
        sp.setReferenceCode(ticketNumber);

        List<SalePayment> paymentRequest = new ArrayList<>();
        paymentRequest.add(sp);
        cf.setPaymentMethods(paymentRequest);

        request.setContent(cf);

        LogManager.info("YENA Card Request " + request);
        SalePost order = service.post(request);
        LogManager.info("YENA Card " + order);

        return order;
    }

    public PaymentResponse payentCapture(
            String cardNumber,
            String cardKey, 
            String sessionID,
            String importe,
            String odometro) throws NoSuchAlgorithmException, SCBException {

        SCBPaymentService service = new SCBPaymentService(endpoint.getUrl_webservice());

        String uuid = UUID.randomUUID().toString();
        com.mx.detisa.integrations.scb.payment.ObjectFactory of = new com.mx.detisa.integrations.scb.payment.ObjectFactory();

        RequestObjectOfPaymentRequest request = of.createRequestObjectOfPaymentRequest();
        request.setSignature(Commons.signature(endpoint.getUsuario(), uuid, endpoint.getPassword()));
        request.setUserName(endpoint.getUsuario());
        request.setToken(uuid);

        PaymentRequest payment = of.createPaymentRequest();
        payment.setCampaignID("1");
        payment.setCardNumber(cardNumber);
        payment.setCardKey(cardKey);
        payment.setSessionID(sessionID);
        payment.setAmount(new BigDecimal(importe).setScale(3, RoundingMode.HALF_EVEN));
        payment.setPin("0");
        payment.setPlates("---");
        payment.setMileage(Integer.valueOf(odometro));
        payment.setCardSource(com.mx.detisa.integrations.scb.payment.CardSource.MagneticStripe);

        request.setContent(payment);

        LogManager.info("YENA Card Request " + request);
        PaymentResponse paymentResponse = service.capture(request);
        LogManager.info("YENA Card " + paymentResponse);

        return paymentResponse;
    }

    public SaleResponse orderConfirm(
            String cardNumber, 
            BigDecimal importe,
            String session) throws NoSuchAlgorithmException, SCBException {

        SCBOrderService service = new SCBOrderService(endpoint.getUrl_webservice());

        String uuid = UUID.randomUUID().toString();
        com.mx.detisa.integrations.scb.order.ObjectFactory of = new com.mx.detisa.integrations.scb.order.ObjectFactory();

        RequestObjectOfSaleRequest request = of.createRequestObjectOfSaleRequest();
        request.setSignature(Commons.signature(endpoint.getUsuario(), uuid, endpoint.getPassword()));
        request.setUserName(endpoint.getUsuario());
        request.setToken(uuid);

        SaleRequest cf = new SaleRequest();
        cf.setCampaignID("1");
        cf.setCardNumber(cardNumber);
        cf.setSessionID(session);

        SalePayment sp = of.createSalePayment();
        sp.setMethodName(PaymentType.MONEDERO_YENA);
        sp.setAmount(importe);
        sp.setReferenceCode("5");
        List<SalePayment> paymentsArray = new ArrayList<>();
        paymentsArray.add(sp);
        cf.setPaymentMethods(paymentsArray);
        request.setContent(cf);

        LogManager.info("YENA Card Request " + request);
        SaleResponse order = service.confirm(request);
        LogManager.info("YENA Card " + order);

        return order;
    }
    
    public PaymentResponse payentConfirm(
            String cardNumber,
            String sessionID,
            String authCode,
            String combustible,
            String cantidad,
            String importe,
            String precio,
            String total) throws NoSuchAlgorithmException, SCBException {

        SCBPaymentService service = new SCBPaymentService(endpoint.getUrl_webservice());

        String uuid = UUID.randomUUID().toString();
        com.mx.detisa.integrations.scb.payment.ObjectFactory of = new com.mx.detisa.integrations.scb.payment.ObjectFactory();

        RequestObjectOfPaymentUpdate request = of.createRequestObjectOfPaymentUpdate();
        request.setSignature(Commons.signature(endpoint.getUsuario(), uuid, endpoint.getPassword()));
        request.setUserName(endpoint.getUsuario());
        request.setToken(uuid);

        PaymentUpdate paymentUpdate = of.createPaymentUpdate();
        paymentUpdate.setCampaignID("1");
        paymentUpdate.setCardNumber(cardNumber);
        paymentUpdate.setSessionID(sessionID);
        paymentUpdate.setAuthCode(authCode); 
        paymentUpdate.setAmount(new BigDecimal(total).setScale(3, RoundingMode.HALF_EVEN));

        com.mx.detisa.integrations.scb.payment.ItemRequest ir = of.createItemRequest();
        ir.setBarCode(combustible);
        ir.setQuantity(new BigDecimal(cantidad).setScale(3, RoundingMode.HALF_EVEN));
        ir.setPrice(new BigDecimal(precio).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTaxRate(new BigDecimal("0.16").setScale(3, RoundingMode.HALF_EVEN));
        ir.setSubTotal(new BigDecimal(importe).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTaxAmount(new BigDecimal(importe).multiply(new BigDecimal("0.16")).setScale(3, RoundingMode.HALF_EVEN));
        ir.setTotal(new BigDecimal(total).setScale(3, RoundingMode.HALF_EVEN));
        List<com.mx.detisa.integrations.scb.payment.ItemRequest> itemsRequest = new ArrayList<>();
        itemsRequest.add(ir);

        paymentUpdate.setItems(itemsRequest);
        request.setContent(paymentUpdate);

        LogManager.info("YENA Payment Confirm Request " + request);
        PaymentResponse paymentResponse = service.update(request);
        LogManager.info("YENA Payment Confirm " + paymentResponse);

        return paymentResponse;
    }
}
