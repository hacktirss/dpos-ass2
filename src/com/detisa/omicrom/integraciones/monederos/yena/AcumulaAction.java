/*
 * AcumulaAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.yena;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.ass2.volumetrico.puntoventa.services.actions.ImprimeTransaccionAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.detisa.integrations.scb.api.SCBException;
import com.mx.detisa.integrations.scb.card.CardData;
import com.mx.detisa.integrations.scb.order.SalePost;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import javax.xml.datatype.DatatypeConfigurationException;

public class AcumulaAction extends ImprimeTransaccionAction  {

    private ConsumoVO consumo;
    private CombustibleVO combustible;
    private ClientesVO cliente;
    private SalePost post;

    public AcumulaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        if (parameters.isNVL(PRMT_ACCOUNT)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro NUMERO DE CUENTA"));
        }
        if (parameters.isNVL(WS_PRMT_TRANSACCION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TRANSACCION"));
        }
        return this;
    }//validateRequest

    private void loadConsumo(int id) throws DetiPOSFault {
        consumo = ConsumosDAO.getByID(id);
        cliente = ClientesDAO.getClienteByID(consumo.NVL(ConsumoVO.RM_FIELDS.cliente.name()));
        combustible = CombustibleDAO.getCombustibleByClavei(consumo.NVL(ConsumoVO.RM_FIELDS.producto.name()));
    }

    private void saveAutorizacion(int folio, BigDecimal importe, Calendar fecha, SalePost confirmacion) {
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

    private void invoqueYena() throws DetiPOSFault {

        YenaApi api = new YenaApi((parameters.NVL(Consumo.PRMTR_APP_NAME)));
        BigDecimal subtotal = consumo.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()).divide(new BigDecimal("1.16"), 3, RoundingMode.HALF_EVEN);

        try {
            CardData card = api.request(parameters.NVL(PRMT_ACCOUNT));
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "REQUEST", card);
            LogManager.info(card);

            post = api.orderDirect(
                    parameters.NVL(PRMT_ACCOUNT),
                    parameters.NVL(PRMT_PIN_ACCOUNT),
                    consumo.NVL(ConsumoVO.RM_FIELDS.posicion.name()),
                    consumo.NVL(ConsumoVO.RM_FIELDS.id.name()),
                    consumo.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()),
                    consumo.NVL(ConsumoVO.RM_FIELDS.volumen.name()),
                    subtotal.toPlainString(),
                    consumo.NVL(ConsumoVO.RM_FIELDS.precio.name()),
                    consumo.NVL(ConsumoVO.RM_FIELDS.pesos.name()),
                    combustible.NVL(CombustibleVO.COM_FIELDS.clave.name()));
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "ORDER DIRECT", post);
            LogManager.info(post);

            if ("Descuento".equalsIgnoreCase(card.getBenefitName())) {
                int idConsumo = Integer.parseInt(parameters.NVL(WS_PRMT_TRANSACCION));
                ConsumosDAO.applyDescuento(idConsumo, post.getPreviousMoney());
            }

            post.setCampaignID(post.getCampaignID() + card.getBenefitName());

            saveAutorizacion(
                    consumo.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()), 
                    consumo.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()), 
                    consumo.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()), 
                    post);            
        } catch (NoSuchAlgorithmException | DatatypeConfigurationException | SCBException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
        
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        int idConsumo = Integer.parseInt(parameters.NVL(WS_PRMT_TRANSACCION));
        loadConsumo(idConsumo);

        if (AutorizacionesrmDAO.registered(idConsumo)) {
            throw new DetiPOSFault("El consumo no esta disponible para aplicar beneficios");
        }

        LogManager.info("Tipodepago " + cliente.NVL(ClientesVO.CLI_FIELDS.tipodepago.name()));
        LogManager.info("Formadepago " + cliente.NVL(ClientesVO.CLI_FIELDS.formadepago.name()));
        if (!"D".equals(consumo.NVL(ConsumoVO.RM_FIELDS.tipo_venta.name()))
                || (!"0".equals(consumo.NVL(ConsumoVO.RM_FIELDS.cliente.name()))
                    && (!"Tarjeta".equals(cliente.NVL(ClientesVO.CLI_FIELDS.tipodepago.name()))
                        || (
                            !"01".equals(cliente.NVL(ClientesVO.CLI_FIELDS.formadepago.name()))
                        && !"04".equals(cliente.NVL(ClientesVO.CLI_FIELDS.formadepago.name()))
                        && !"28".equals(cliente.NVL(ClientesVO.CLI_FIELDS.formadepago.name())))))) {
            throw new DetiPOSFault("El consumo no esta disponible para aplicar beneficios");
        }

        invoqueYena();
        return super.getComprobante();
    }//getComprobante
}//ConsumoAction
