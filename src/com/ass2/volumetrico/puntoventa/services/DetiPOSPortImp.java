/**
 * DetiPOSPortImp 
 * DetiPOS WEB Service Implementation DetiPOS Omicrom Services
 * ® 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
package com.ass2.volumetrico.puntoventa.services;

import com.detisa.omicrom.bussiness.CorteV2;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.IntegracionDAO;
import com.ass2.volumetrico.puntoventa.data.IntegracionVO;
import com.detisa.omicrom.integraciones.FactoryBuilder;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.preset.ConsumoAditivos;
import com.ass2.volumetrico.puntoventa.services.actions.ActionFactory;
import com.ass2.volumetrico.puntoventa.services.actions.DposActionFactory;
import com.ass2.volumetrico.puntoventa.services.actions.DposActionFactory.ACTIONS;
import com.ass2.volumetrico.puntoventa.services.actions.BaseAction;
import com.ass2.volumetrico.puntoventa.services.actions.CapturaDepositoAction;
import com.ass2.volumetrico.puntoventa.services.actions.CobroCODIAction;
import com.ass2.volumetrico.puntoventa.services.actions.CobroServicioAction;
import com.ass2.volumetrico.puntoventa.services.actions.CorteAction;
import com.ass2.volumetrico.puntoventa.services.actions.ImprimeCorteAction;
import com.ass2.volumetrico.puntoventa.services.actions.ImprimeDepositoAction;
import com.ass2.volumetrico.puntoventa.services.actions.ImprimeJarreoAction;
import com.ass2.volumetrico.puntoventa.services.actions.ListMontosTAAction;
import com.ass2.volumetrico.puntoventa.services.actions.ListServiciosAction;
import com.ass2.volumetrico.puntoventa.services.actions.MarcaCobroConsumoAction;
import com.ass2.volumetrico.puntoventa.services.actions.RegistroTarjetaAction;
import com.ass2.volumetrico.puntoventa.services.actions.TiempoAireAction;
import com.ass2.volumetrico.puntoventa.services.actions.UpdateBoletoAction;
import com.ass2.volumetrico.puntoventa.services.actions.ValesAction;
import com.ass2.volumetrico.puntoventa.services.actions.VentaDivisasAction;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.util.List;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@WebService(
        serviceName = "DetiPOS",
        portName = "DetiPOS",
        targetNamespace = DetiPOSPort.DPOS_WS_NAMESPACE,
        endpointInterface = "com.detisa.omicrom.services.DetiPOSPort")
public class DetiPOSPortImp implements DetiPOSPort {

    private static class ParametersFactory {
        private static String parseSerialNumber(String complexSerialNumber) {
            return complexSerialNumber.contains("@") ? complexSerialNumber.split("@")[0] : complexSerialNumber;
        }
        private static String parseRegisteredVersion(String complexSerialNumber) {
            return complexSerialNumber.contains("@") ? complexSerialNumber.split("@")[1] : "";
        }
        public static DinamicVO<String, String> getParameters(String idTerminal, String ipClient) {
            DinamicVO<String, String> parameters = new DinamicVO<>();
            parameters.setField(BaseAction.WS_PRMT_IP_CLIENT, ipClient);
            if (!StringUtils.isNVL(idTerminal)) {
                parameters.setField(BaseAction.WS_PRMT_TERMINAL, parseSerialNumber(idTerminal));
                parameters.setField(BaseAction.WS_PRMT_VERSION, parseRegisteredVersion(idTerminal));
            }
            return parameters;
        }
    }//ParametersFactory

    private static final ActionFactory FACTORY = new DposActionFactory();

    @Resource(name = "wsContext")
    WebServiceContext wsContext;

    /**
     * Gets client IP adress
     *
     * @return Client IP adress
     */
    private String getClientIP() {
        MessageContext mc = wsContext.getMessageContext();
        String ipClient = "";
        try {
            ipClient = ((HttpExchange) mc.get(JAXWSProperties.HTTP_EXCHANGE)).getRemoteAddress().getAddress().getHostAddress();
        } finally {
            LogManager.info("Invoked " + mc.get(MessageContext.WSDL_OPERATION) +  " from " + ipClient);
        }
        return ipClient;
    }//getClientIP

    /**
     * Get last transaction at the requested position
     *
     * @param idTerminal POS serial number
     * @param posicion Fuel dispenser (logical id)
     * @param password User password (if required)
     * @return
     * @throws DetiPOSFault
     */
    @Override
    public String imprimeComprobante(String idTerminal, String posicion, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_POSICION, posicion);
        return FACTORY.getPOSAction(ACTIONS.IMPRIME_TICKET, parameters).getComprobante().serialize();
    }//imprimeComprobante

    @Override
    public String imprimePuntos(String idTerminal, String posicion, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_POSICION, posicion);
        return FACTORY.getPOSAction(ACTIONS.IMPRIME_PUNTOS, parameters).getComprobante().serialize();
    }//imprimeComprobante

    @Override
    public String imprimeTransaccion(String idTerminal, String transaccion, String efectivo, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(BaseAction.WS_PRMT_EFECTIVO, efectivo);
        return FACTORY.getPOSAction(ACTIONS.IMPRIME_TRANSACCION, parameters).getComprobante().serialize();
    }//imprimeTransaccion

    @Override
    public String imprimeJarreo(String idTerminal, String transaccion, String tipo, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(ImprimeJarreoAction.WS_PRMT_TIPO, tipo);
        return FACTORY.getPOSAction(ACTIONS.IMPRIME_JARREO, parameters).getComprobante().serialize();
    }//imprimeJarreo

    @Override
    public String imprimeDeposito(String idTerminal, String corte, String vendedor, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_DESP, vendedor);
        parameters.setField(ImprimeDepositoAction.WS_DEP_CTID, corte);
        return FACTORY.getPOSAction(DposActionFactory.ACTIONS.IMPRIME_DEPOSITO, parameters).getComprobante().serialize();
    }

    @Override
    public String marcaCobroConsumo(String idTerminal, String transaccion, String cadena) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(MarcaCobroConsumoAction.WS_PRMTR_CADENA_AUTH, cadena);
        return FACTORY.getPOSAction(ACTIONS.MARCA_COBRO, parameters).getComprobante().serialize();
    }

    @Override
    public String cobroCODIConsumo(String idTerminal, String transaccion, String telefono) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(CobroCODIAction.WS_PRMTR_CODI_TELEFONO, telefono);
        return FACTORY.getPOSAction(ACTIONS.MARCA_COBRO, parameters).getComprobante().serialize();
    }
    
    @Override
    public String marcaConsumoTarjeta(String idTerminal, String transaccion, String tarjeta, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(BaseAction.WS_PRMT_CLIENTE, tarjeta);
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        return FACTORY.getPOSAction(ACTIONS.MARCA_TARJETA, parameters).getComprobante().serialize();
    }

    @Override
    public String capturaDeposito(String idTerminal, String corte, String importe, String vendedor, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(CapturaDepositoAction.WS_DEP_DESPACHADOR, vendedor);
        parameters.setField(CapturaDepositoAction.WS_DEP_CTID, corte);
        parameters.setField(CapturaDepositoAction.WS_DEP_IMPORTE, importe);
        return FACTORY.getPOSAction(DposActionFactory.ACTIONS.CAPTURA_DEPOSITO, parameters).getComprobante().serialize();
    }

    @Override
    public String ventaDivisas(String idTerminal, String transaccion, String divisa, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(VentaDivisasAction.WS_PRMT_DIVISA, divisa);
        return FACTORY.getPOSAction(ACTIONS.VENTA_DIVISAS, parameters).getComprobante().serialize();
    }//ventaDivisas

    @Override
    public String imprimeTransaccionComplex(String idTerminal, String posicion, String transaccion, String efectivo, String password, List<VentaAditivo> aditivos) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        DinamicVO<String, String> pAditivo = ParametersFactory.getParameters(idTerminal, getClientIP());

        LogManager.debug(aditivos);
        pAditivo.setField(ConsumoAditivos.PRMT_PIN_EMPLOYEE, password);
        pAditivo.setField(ConsumoAditivos.PRMT_CNS_POS, posicion);
        pAditivo.setField(ConsumoAditivos.PRMT_REFERENCIA, transaccion);

        for (VentaAditivo aditivo : aditivos) {
            LogManager.debug(aditivo);
            pAditivo.setField(ConsumoAditivos.PRMT_CLAVE_ADITIVO, aditivo.getClaveAditivo());
            pAditivo.setField(ConsumoAditivos.PRMT_CODIGO_ADITIVO, aditivo.getCodigoAditivo());
            pAditivo.setField(ConsumoAditivos.PRMT_CNS_CNT, aditivo.getCantidad());
            FACTORY.getPOSAction(ACTIONS.VENTA_ADITIVOS, pAditivo).getComprobante();
        }

        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
        parameters.setField(BaseAction.WS_PRMT_EFECTIVO, efectivo);
        return FACTORY.getPOSAction(ACTIONS.IMPRIME_TRANSACCION, parameters).getComprobante().serialize();
    }

    @Override
    public String recuperaUltimosConsumos(String idTerminal, String posicion, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_POSICION, posicion);
        return FACTORY.getPOSAction(ACTIONS.RECUPERA_CONSUMOS, parameters).getComprobante().serialize();
    }

    @Override
    public String cobroTarjetaPosicion(String idTerminal, String posicion, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_POSICION, posicion);
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        return FACTORY.getPOSAction(ACTIONS.RECUPERA_CONSUMO_COBRO, parameters).getComprobante().serialize();
    }

    @Override
    public String inventarioTanques(String idTerminal, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        return FACTORY.getPOSAction(ACTIONS.INVENTARIO_TANQUES, parameters).getComprobante().serialize();
    }

    @Override
    public String inventarioAditivos(String idTerminal,String isla, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_ISLA, isla);
        return FACTORY.getPOSAction(ACTIONS.INVENTARIO_ADITIVOS, parameters).getComprobante().serialize();
    }//inventarioAditivos

    @Override
    public String condicionesTarjeta(String idTerminal, String idTarjeta, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_TARJETA, idTarjeta);
        return FACTORY.getPOSAction(ACTIONS.CONDICIONES_TARJETA, parameters).getComprobante().serialize();
    }//condicionesTarjeta

    @Override
    public String saldoCliente(String idTerminal, String cliente, String idTarjeta, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_CLIENTE, cliente);
        if (idTarjeta!=null) {
            parameters.setField(BaseAction.WS_PRMT_TARJETA, idTarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
        }
        return FACTORY.getPOSAction(ACTIONS.SALDO_CLIENTE, parameters).getComprobante().serialize();
    }

    @Override
    public String registroTarjeta(String idTerminal, List <RegistroTarjetasDTO> tarjetas, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        Comprobante comprobante = new Comprobante();
        int idx = 1;
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        for (RegistroTarjetasDTO tarjeta : tarjetas) {
            parameters.setField(RegistroTarjetaAction.WS_TARJETA_ID, tarjeta.getIdTarjeta().trim().replaceAll("\n", "").replaceAll("\r", ""));
            parameters.setField(RegistroTarjetaAction.WS_TARJETA_IMPRESO, tarjeta.getImpreso().trim().replaceAll("\n", "").replaceAll("\r", ""));
            comprobante.append("TR" + idx, tarjeta.idTarjeta);
            comprobante.append("IMP@TR" + idx, tarjeta.impreso);
            comprobante.append("STAT@TR" + idx++, 
                    FACTORY.getPOSAction(ACTIONS.REGISTRO_TARJETA, parameters).getComprobante().serialize("", " "));
        }//foreach tarjeta
        return comprobante.serialize();
    }

    @Override
    public String ventaAditivos(String idTerminal, String posicion, String claveAditivo, String codigoAditivo, String cantidad, String claveBanco, String idTarjeta, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(ConsumoAditivos.PRMT_PIN_EMPLOYEE, password);
        parameters.setField(ConsumoAditivos.PRMT_BANK_ID, claveBanco);
        parameters.setField(ConsumoAditivos.PRMT_CNS_POS, posicion);
        parameters.setField(ConsumoAditivos.PRMT_CLAVE_ADITIVO, claveAditivo);

        if (idTarjeta != null) {
            parameters.setField(ConsumoAditivos.PRMT_ACCOUNT, idTarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
            parameters.setField(ConsumoAditivos.PRMT_PIN_ACCOUNT, password);
        }

        if (codigoAditivo != null) {
            parameters.setField(ConsumoAditivos.PRMT_CODIGO_ADITIVO, codigoAditivo.trim().replaceAll("\n", "").replaceAll("\r", ""));
        }

        parameters.setField(ConsumoAditivos.PRMT_CNS_CNT, cantidad);
        return FACTORY.getPOSAction(ACTIONS.VENTA_ADITIVOS, parameters).getComprobante().serialize();
    }

    @Override
    public String consumo(String idTerminal, String tipo, String cantidad, 
            String posicion, String manguera, String idTarjeta, String claveBanco, 
            String formaPago, String despachador, String passwordDespachador,
            String password, String odometro, String eco) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        if (idTarjeta != null) {
            parameters.setField(Consumo.PRMT_ACCOUNT, idTarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
            parameters.setField(Consumo.PRMT_PIN_ACCOUNT, password);
        }
        parameters.setField(Consumo.PRMT_CNS_TYP, tipo);
        parameters.setField(Consumo.PRMT_CNS_CNT, cantidad);
        parameters.setField(Consumo.PRMT_CNS_POS, posicion);
        parameters.setField(Consumo.PRMT_CNS_MNG, manguera);
        parameters.setField(Consumo.PRMT_BANK_ID, claveBanco);
        parameters.setField(Consumo.PRMT_CNS_FP, formaPago);
        parameters.setField(Consumo.PRMT_CNS_KM, odometro);
        parameters.setField(Consumo.PRMT_CNS_ECO, eco);
        parameters.setField(Consumo.PRMT_EMPLOYEE, despachador);
        parameters.setField(Consumo.PRMT_PIN_EMPLOYEE, passwordDespachador);
        return FACTORY.getPOSAction(ACTIONS.CONSUMO, parameters).getComprobante().serialize();
    }//consumo

    @Override
    public String callAutorizador(String idIntegracion, String idTerminal, 
                            String action, String tipo, String cantidad, 
                            String posicion, String manguera, String idTarjeta, 
                            String employee, String password, 
                            String odometro, String numeco, String transaccion) throws DetiPOSFault {
        BaseVO integracion = IntegracionDAO.getIntegracion(idIntegracion);

        if ("0".equals(integracion.NVL(IntegracionVO.FIELDS.status, "0"))) {
            throw new DetiPOSFault("Error de configuracion", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error conectando con " + idIntegracion, "La integracion " + idIntegracion + " no esta activa."));
        }   

        try {
            LogManager.info("Solicitud de " + action + " para la integracion " + idIntegracion + "(" + integracion.NVL(IntegracionVO.FIELDS.descripcion.name()) + ")");
            ActionFactory factory = FactoryBuilder.getFactory(integracion.NVL(IntegracionVO.FIELDS.factory.name()));
            DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
            if (idTarjeta != null) {
                parameters.setField(Consumo.PRMT_ACCOUNT, idTarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
                parameters.setField(Consumo.PRMT_PIN_ACCOUNT, password);
            }
            parameters.setField(Consumo.PRMTR_APP_STATUS, integracion.NVL(IntegracionVO.FIELDS.status.name()));
            parameters.setField(Consumo.PRMTR_APP_NAME, integracion.NVL(IntegracionVO.FIELDS.clave.name()));
            parameters.setField(Consumo.PRMT_CNS_TYP, tipo);
            parameters.setField(Consumo.PRMT_CNS_CNT, cantidad);
            parameters.setField(Consumo.PRMT_CNS_POS, posicion);
            parameters.setField(Consumo.PRMT_CNS_MNG, manguera);
            parameters.setField(Consumo.PRMT_CNS_KM, odometro);
            parameters.setField(Consumo.PRMT_CNS_ECO, numeco);
            parameters.setField(Consumo.PRMT_EMPLOYEE, employee);
            parameters.setField(BaseAction.WS_PRMT_TRANSACCION, transaccion);
            return factory.getPOSAction(action, parameters).getComprobante().serialize();
        } catch (ReflectiveOperationException ex) {
            throw new DetiPOSFault("Error conectando con la integracion", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error conectando con " + idIntegracion, "No existe el Factory o no esta definido"), ex);
        }
    }//consumoIntegracion

    @Override
    public String consumoIntegracion(String idIntegracion, String idTerminal, 
                            String tipo, String cantidad, String posicion, 
                            String manguera, String idTarjeta, String claveBanco, 
                            String formaPago,  String despachador, String passwordDespachador,
                            String password, String odometro) throws DetiPOSFault {
        BaseVO integracion = IntegracionDAO .getIntegracion(idIntegracion);

        if ("0".equals(integracion.NVL(IntegracionVO.FIELDS.status, "0"))) {
            throw new DetiPOSFault("Error de configuracion", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error conectando con " + idIntegracion, "La integracion " + idIntegracion + " no esta activa."));
        }   
        
        try {
            LogManager.info("Solicitud de consumo para la integracion " + idIntegracion + "(" + integracion.NVL(IntegracionVO.FIELDS.descripcion.name()) + ")");
            ActionFactory factory = FactoryBuilder.getFactory(integracion.NVL(IntegracionVO.FIELDS.factory.name()));
            DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
            if (idTarjeta != null) {
                parameters.setField(Consumo.PRMT_ACCOUNT, idTarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
                parameters.setField(Consumo.PRMT_PIN_ACCOUNT, password);
            }
            parameters.setField(Consumo.PRMTR_APP_STATUS, integracion.NVL(IntegracionVO.FIELDS.status.name()));
            parameters.setField(Consumo.PRMT_CNS_TYP, tipo);
            parameters.setField(Consumo.PRMT_CNS_CNT, cantidad);
            parameters.setField(Consumo.PRMT_CNS_POS, posicion);
            parameters.setField(Consumo.PRMT_CNS_MNG, manguera);
            parameters.setField(Consumo.PRMT_BANK_ID, claveBanco);
            parameters.setField(Consumo.PRMT_CNS_FP, formaPago);
            parameters.setField(Consumo.PRMT_CNS_KM, odometro);
            parameters.setField(Consumo.PRMT_EMPLOYEE, despachador);
            parameters.setField(Consumo.PRMT_PIN_EMPLOYEE, passwordDespachador);
            return factory.getPOSAction("CONSUMO", parameters).getComprobante().serialize();
        } catch (ReflectiveOperationException ex) {
            throw new DetiPOSFault("Error conectando con la integracion", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error conectando con " + idIntegracion, "No existe el Factory o no esta definido"), ex);
        }
    }//consumoIntegracion

    @Override
    public String corte(String idTerminal, String posicion, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(CorteAction.WS_PRMT_PROCESS, CorteV2.PROCESS.CLOSEANDOPEN.name());
        return FACTORY.getPOSAction(ACTIONS.CORTE, parameters).getComprobante().serialize();
    }//corte

    @Override
    public String corteOmicrom(String isla, String proceso) throws DetiPOSFault {
        DinamicVO<String, String> parameters = new DinamicVO<>();
        parameters.setField(BaseAction.WS_PRMT_IP_CLIENT, getClientIP());
        parameters.setField(CorteAction.WS_PRMT_PROCESS, proceso);
        return FACTORY.getAnonymousAction(ACTIONS.CORTE, parameters).getComprobante().serialize();
    }//corteOmicrom

    @Override
    public String recuperaUltimosCortes(String idTerminal, String posicion, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_POSICION, posicion);
        return FACTORY.getPOSAction(ACTIONS.RECUPERA_CORTES, parameters).getComprobante().serialize();
    }//recuperaUltimosCortes

    @Override
    public String imprimeCorte(String idTerminal, String isla, String posicion, String corteID, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_ISLA, isla);
        parameters.setField(BaseAction.WS_PRMT_POSICION, posicion);
        parameters.setField(ImprimeCorteAction.WS_CT_ID, corteID);
        return FACTORY.getPOSAction(ACTIONS.IMPRIME_CORTE, parameters).getComprobante().serialize();
    }//imprimeCorte

    @Override
    public String corteParcial(String idTerminal, String isla, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(BaseAction.WS_PRMT_ISLA, isla);
        return FACTORY.getPOSAction(ACTIONS.CORTE_PARCIAL, parameters).getComprobante().serialize();
    }//imprimeCorte

    @Override
    public String vales(String idTerminal, String idVales, String posicion, String manguera, String password) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_AUTH, password);
        parameters.setField(ValesAction.PRMT_VALE, idVales.trim().replaceAll("\n", "").replaceAll("\r", ""));
        parameters.setField(Consumo.PRMT_ACCOUNT, idVales.trim().replaceAll("\n", "").replaceAll("\r", ""));
        parameters.setField(Consumo.PRMT_CNS_POS, posicion);
        parameters.setField(Consumo.PRMT_CNS_MNG, manguera);
        return FACTORY.getPOSAction(ACTIONS.VALES, parameters).getComprobante().serialize();
    }//vales

    @Override
    public String consultaSaldoCliente(String cliente) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_CLIENTE, cliente);
        return FACTORY.getAnonymousAction(ACTIONS.SALDO_CLIENTE, parameters).getComprobante().serialize();
    }//consultaSaldoCliente

    @Override
    public String consultaSaldoTarjeta(String tarjeta) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_TARJETA, tarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
        return FACTORY.getAnonymousAction(ACTIONS.SALDO_TARJETA, parameters).getComprobante().serialize();
    }//consultaSaldoTarjeta

    @Override
    public String consultaTarjeta(String tarjeta) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_TARJETA, tarjeta.trim().replaceAll("\n", "").replaceAll("\r", ""));
        return FACTORY.getAnonymousAction(ACTIONS.TARJETA, parameters).getComprobante().serialize();
    }//consultaTarjeta

    @Override
    public String consultaCliente(String cliente) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null,getClientIP());
        parameters.setField(BaseAction.WS_PRMT_CLIENTE, cliente);
        return FACTORY.getAnonymousAction(ACTIONS.CLIENTE, parameters).getComprobante().serialize();
    }//consultaCliente

    @Override
    public String consultaClienteByRFC(String rfc) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_CLIENTE, rfc);
        return FACTORY.getAnonymousAction(ACTIONS.CLIENTE_RFC, parameters).getComprobante().serialize();
    }//consultaCliente

    @Override
    public String consultaBoleto(String boleto) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_BOLETO, boleto.trim().replaceAll("\n", "").replaceAll("\r", ""));
        return FACTORY.getAnonymousAction(ACTIONS.BOLETO, parameters).getComprobante().serialize();
    }//consultaBoleto

    @Override
    public String updateBoleto(String boleto, String cargo, String ticket1, String ticket2, String activo) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_BOLETO, boleto.trim().replaceAll("\n", "").replaceAll("\r", ""));
        parameters.setField(UpdateBoletoAction.BLT_PRMTR_CARGO, cargo);
        parameters.setField(UpdateBoletoAction.BLT_PRMTR_TICKET_1, ticket1);
        parameters.setField(UpdateBoletoAction.BLT_PRMTR_TICKET_2, ticket2);
        parameters.setField(UpdateBoletoAction.BLT_PRMTR_ACTIVO, activo);
        return FACTORY.getAnonymousAction(ACTIONS.UPDATE_BOLETO, parameters).getComprobante().serialize();
    }//updateBoleto

    @Override
    public String getEstacion(String idTerminal, String lanMacAdd, String wlanMacAdd, String kernelVersion) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_LAN_MAC_ADD, lanMacAdd);
        parameters.setField(BaseAction.WS_PRMT_WLAN_MAC_ADD, wlanMacAdd);
        parameters.setField(BaseAction.WS_PRMT_KERNEL_VERSION, kernelVersion);
        return FACTORY.getPOSAction(ACTIONS.CONF_ESTACION, parameters).getComprobante().serialize();
    }//getEstacion

    @Override
    public String getLogo(String idTerminal) throws DetiPOSFault {
        return FACTORY.getPOSAction(ACTIONS.CONF_LOGO, ParametersFactory.getParameters(idTerminal, getClientIP())).getComprobante().serialize("&", "#");
    }//getLogo

    @Override
    public String getLogoFacturacion(String idTerminal) throws DetiPOSFault {
        return FACTORY.getPOSAction(ACTIONS.CONF_LOGO_FACTURACION, ParametersFactory.getParameters(idTerminal, getClientIP())).getComprobante().serialize("&", "#");
    }//getLogo

    @Override
    public String getConfigFiles(String idTerminal) throws DetiPOSFault {
        return FACTORY.getPOSAction(ACTIONS.CONF_FILES, ParametersFactory.getParameters(idTerminal, getClientIP())).getComprobante().serialize("&", "#");
    }//getConfigFiles

    @Override
    public String getFonts(String idTerminal) throws DetiPOSFault {
        return FACTORY.getPOSAction(ACTIONS.CONF_FONTS, ParametersFactory.getParameters(idTerminal, getClientIP())).getComprobante().serialize("&", "#");
    }//idTerminal

    @Override
    public UpdateDTO checkUpdates(String idTerminal) throws DetiPOSFault {
        return (UpdateDTO) FACTORY.getPOSAction(ACTIONS.CONF_UPDATE, ParametersFactory.getParameters(idTerminal, getClientIP())).getResponse();
    }//checkUpdates

    @Override
    public String listFormaPago() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_PAYMENTS, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listFormaPago

    @Override
    public String listMenu() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_MENU, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listMenu

    @Override
    public String listMonederos() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_AUTORIZADORES, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listTarjetas

    @Override
    public String listTarjetas() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_BANK, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listTarjetas

    @Override
    public String listTarjetasFlotillas() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_BANK_FLOTILLA, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listTarjetas

    @Override
    public String listOperadoresTA() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_OPER_TA, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listOperadoresTA

    @Override
    public String listOperadoresServicios() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_OPER_SERVICIOS, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listOperadoresServicios

    @Override
    public String listServicios(String codigoOper) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(ListServiciosAction.PRMTR_CODIGO_OPER, codigoOper);
        return FACTORY.getAnonymousAction(ACTIONS.LIST_SERVICIOS, parameters).getComprobante().serialize();
    }//listServicios

    @Override
    public String listMontosTA(String codigoOper) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(ListMontosTAAction.PRMTR_CODIGO_OPER, codigoOper);
        return FACTORY.getAnonymousAction(ACTIONS.LIST_MONTO_TA, parameters).getComprobante().serialize();
    }//listMontosTA

    @Override
    public String cobroServicio(
            String idTerminal,
            String codigoServicio,
            String codigoProducto,
            String lineaDeCaptura,
            String importe,
            String password,
            String formaPago,
            String authBancaria
    ) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(CobroServicioAction.PRMTR_CODIGO_SERVICIO, codigoServicio);
        parameters.setField(CobroServicioAction.PRMTR_CODIGO_PRODUCTO, codigoProducto);
        parameters.setField(CobroServicioAction.PRMTR_LINEA_DE_CAPTURA, lineaDeCaptura);
        parameters.setField(CobroServicioAction.PRMTR_IMPORTE, importe);
        parameters.setField(CobroServicioAction.PRMTR_AUTH, password);
        parameters.setField(CobroServicioAction.PRMTR_FORMA_PAGO, formaPago);
        parameters.setField(CobroServicioAction.PRMTR_AUTH_BANCO, authBancaria);
        return FACTORY.getPOSAction(ACTIONS.COBRO_SERVICIO, parameters).getComprobante().serialize();
    }//cobroServicio
    @Override
    public String cobroServicioA(
            String codigoServicio,
            String codigoProducto,
            String lineaDeCaptura,
            String importe,
            String formaPago,
            String authBancaria
    ) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(CobroServicioAction.PRMTR_CODIGO_SERVICIO, codigoServicio);
        parameters.setField(CobroServicioAction.PRMTR_CODIGO_PRODUCTO, codigoProducto);
        parameters.setField(CobroServicioAction.PRMTR_LINEA_DE_CAPTURA, lineaDeCaptura);
        parameters.setField(CobroServicioAction.PRMTR_IMPORTE, importe);
        parameters.setField(CobroServicioAction.PRMTR_AUTH, "VENTANILLA");
        parameters.setField(CobroServicioAction.PRMTR_FORMA_PAGO, formaPago);
        parameters.setField(CobroServicioAction.PRMTR_AUTH_BANCO, authBancaria);
        return FACTORY.getAnonymousAction(ACTIONS.COBRO_SERVICIO, parameters).getComprobante().serialize();
    }//cobroServicioA
    @Override
    public String tiempoAire(
            String idTerminal,
            String codigoOperador,
            String numero,
            String importe,
            String password,
            String formaPago,
            String authBancaria
    ) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(BaseAction.WS_PRMT_TERMINAL, idTerminal);
        parameters.setField(TiempoAireAction.PRMTR_NUMERO, numero);
        parameters.setField(TiempoAireAction.PRMTR_CODIGO_OPERADOR, codigoOperador);
        parameters.setField(TiempoAireAction.PRMTR_IMPORTE, importe);
        parameters.setField(TiempoAireAction.PRMTR_AUTH, password);
        parameters.setField(TiempoAireAction.PRMTR_FORMA_PAGO, formaPago);
        parameters.setField(TiempoAireAction.PRMTR_AUTH_BANCO, authBancaria);
        return FACTORY.getPOSAction(ACTIONS.TIEMPO_AIRE, parameters).getComprobante().serialize();
    }//tiempoAire
    @Override
    public String tiempoAireA(
            String codigoOperador,
            String numero,
            String importe,
            String formaPago,
            String authBancaria
    ) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(null, getClientIP());
        parameters.setField(TiempoAireAction.PRMTR_NUMERO, numero);
        parameters.setField(TiempoAireAction.PRMTR_CODIGO_OPERADOR, codigoOperador);
        parameters.setField(TiempoAireAction.PRMTR_IMPORTE, importe);
        parameters.setField(TiempoAireAction.PRMTR_AUTH, "VENTANILLA");
        parameters.setField(TiempoAireAction.PRMTR_FORMA_PAGO, formaPago);
        parameters.setField(TiempoAireAction.PRMTR_AUTH_BANCO, authBancaria);
        parameters.setField(BaseAction.WS_PRMT_IP_CLIENT, getClientIP());
        return FACTORY.getAnonymousAction(ACTIONS.TIEMPO_AIRE, parameters).getComprobante().serialize();
    }//tiempoAireA
    @Override
    public String consultaTiempoAire(String idTerminal,
            String idTransaccion
    ) throws DetiPOSFault {
        DinamicVO<String, String> parameters = ParametersFactory.getParameters(idTerminal, getClientIP());
        parameters.setField(TiempoAireAction.PRMTR_TRANSACCION, idTransaccion);
        return FACTORY.getPOSAction(ACTIONS.CONSULTA_TIEMPO_AIRE, parameters).getComprobante().serialize();
    }//consultaTiempoAire

    @Override
    public String listMenus() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.LIST_MENUS, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//listMenus

    @Override
    public String checkDispensarios() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.CHECK_PUMP, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//checkDispensarios

    @Override
    public String catalogoMangueras() throws DetiPOSFault {
        return FACTORY.getAnonymousAction(ACTIONS.CAT_MANGUERA, ParametersFactory.getParameters(null, getClientIP())).getComprobante().serialize();
    }//catalogoMangueras
}//DetiPOSPortImp
