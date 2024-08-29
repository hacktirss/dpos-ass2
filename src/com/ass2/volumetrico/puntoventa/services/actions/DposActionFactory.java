package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public class DposActionFactory extends ActionFactory {

    public static enum ACTIONS {

        CONFIGURATION, //TODO implements POS initial configuratrion
        CONDICIONES_TARJETA,
        IMPRIME_TICKET,
        IMPRIME_PUNTOS,
        RECUPERA_CONSUMOS,
        RECUPERA_CONSUMO_COBRO,
        IMPRIME_TRANSACCION,
        IMPRIME_JARREO,
        IMPRIME_DEPOSITO,
        CAPTURA_DEPOSITO, 
        VENTA_DIVISAS,
        INVENTARIO_TANQUES,
        INVENTARIO_ADITIVOS,
        VENTA_ADITIVOS,
        CONSUMO,
        CLIENTE,
        CLIENTE_RFC,
        TARJETA,
        REGISTRO_TARJETA,
        SALDO_TARJETA,
        SALDO_CLIENTE,
        BOLETO,
        UPDATE_BOLETO,
        CAT_MANGUERA,
        CHECK_PUMP,        
        LIST_MENU,
        LIST_AUTORIZADORES,
        LIST_BANK,
        LIST_BANK_FLOTILLA,
        LIST_PAYMENTS,
        LIST_OPER_TA,
        LIST_OPER_SERVICIOS,
        LIST_SERVICIOS,
        LIST_MONTO_TA,
        MARCA_COBRO,
        MARCA_TARJETA,
        COBRO_SERVICIO,
        COBRO_CODI,
        TIEMPO_AIRE,
        CONSULTA_TIEMPO_AIRE,
        LIST_MENUS,
        CORTE,
        RECUPERA_CORTES,
        IMPRIME_CORTE,
        CORTE_PARCIAL,
        VALES,
        CONF_ESTACION,
        CONF_LOGO,
        CONF_LOGO_FACTURACION,
        CONF_FONTS,
        CONF_FILES,
        CONF_UPDATE
    }//ACTIONS

    @Override
    public DetiPOSAction getPOSAction(Object action, DinamicVO<String, String> parameters) throws DetiPOSFault {
        switch ((ACTIONS) action) {
            case CONDICIONES_TARJETA:
                return new CondicionesTarjetaAction(parameters).validateRequest().validatePOS();
            case IMPRIME_TICKET:
                return new ImprimeTicketAction(parameters).validateRequest().validatePOS();
            case IMPRIME_PUNTOS:
                return new ImprimePuntosAction(parameters).validateRequest().validatePOS();
            case RECUPERA_CONSUMOS:
                return new RecuperaConsumosAction(parameters).validateRequest().validatePOS();
            case RECUPERA_CONSUMO_COBRO:
                return new RecuperaConsumoCobroAction(parameters).validateRequest().validatePOS();
            case IMPRIME_TRANSACCION:
                return new ImprimeTransaccionAction(parameters).validateRequest().validatePOS();
            case IMPRIME_JARREO:
                return new ImprimeJarreoAction(parameters).validateRequest().validatePOS();
            case IMPRIME_DEPOSITO:
                return new ImprimeDepositoAction(parameters).validateRequest().validatePOS();
            case CAPTURA_DEPOSITO:
                return new CapturaDepositoAction(parameters).validateRequest().validatePOS();
            case MARCA_COBRO: 
                return new MarcaCobroConsumoAction(parameters).validateRequest().validatePOS();
            case MARCA_TARJETA: 
                return new MarcaConsumoTarjetaAction(parameters).validateRequest().validatePOS();
            case VENTA_DIVISAS:
                return new VentaDivisasAction(parameters).validateRequest().validatePOS();
            case INVENTARIO_TANQUES:
                return new InventarioTanquesAction(parameters).validateRequest().validatePOS();
            case INVENTARIO_ADITIVOS:
                return new InventarioAditivosAction(parameters).validateRequest().validatePOS();
            case VENTA_ADITIVOS:
                return new VentaAditivosAction(parameters).validateRequest().validatePOS();
            case CONSUMO:
                return new ConsumoAction(parameters).validateRequest().validatePOS();
            case CAT_MANGUERA:
                return new CatalogoManguerasAction(parameters).validateRequest().validatePOS();
            case CHECK_PUMP:
                return new CheckDispensariosAction(parameters).validateRequest().validatePOS();
            case LIST_AUTORIZADORES:
                return new ListMonederosAction(parameters).validateRequest().validatePOS();
            case LIST_BANK:
                return new ListTarjetasAction(parameters).validateRequest().validatePOS();
            case LIST_BANK_FLOTILLA:
                return new ListTarjetasFlotillasAction(parameters).validateRequest().validatePOS();
            case UPDATE_BOLETO:
                return new UpdateBoletoAction(parameters).validateRequest().validatePOS();
            case BOLETO:
                return new ConsultaBoletoAction(parameters).validateRequest().validatePOS();
            case CLIENTE:
                return new ConsultaClienteAction(parameters).validateRequest().validatePOS();
            case TARJETA:
                return new ConsultaTarjetaAction(parameters).validateRequest().validatePOS();
            case COBRO_CODI:
                return new CobroCODIAction(parameters).validateRequest().validatePOS();
            case REGISTRO_TARJETA:
                return new RegistroTarjetaAction(parameters).validateRequest().validatePOS();
            case SALDO_TARJETA:
                return new ConsultaSaldoTarjetaAction(parameters).validateRequest().validatePOS();
            case SALDO_CLIENTE:
                return new ConsultaSaldoClienteAction(parameters).validateRequest().validatePOS();
            case CORTE:
                return new CorteAction(parameters).validateRequest().validatePOS();
            case IMPRIME_CORTE:
                return new ImprimeCorteAction(parameters).validateRequest().validatePOS();
            case CORTE_PARCIAL:
                return new CorteParcialAction(parameters).validateRequest().validatePOS();
            case RECUPERA_CORTES:
                return new RecuperaUltimosCortesAction(parameters).validateRequest().validatePOS();
            case VALES:
                return new ValesAction(parameters).validateRequest().validatePOS();
            case CONF_ESTACION:
                return new GetEstacionAction(parameters).validateRequest().validatePOS();
            case CONF_LOGO:
                return new GetLogoAction(parameters).validateRequest().validatePOS();
            case CONF_LOGO_FACTURACION:
                return new GetLogoFacturacionAction(parameters).validateRequest().validatePOS();
            case CONF_FILES:
                return new GetConfigFilesAction(parameters).validateRequest().validatePOS();
            case CONF_FONTS:
                return new GetFontsAction(parameters).validateRequest().validatePOS();
            case CONF_UPDATE:
                return new CheckUpdatesAction(parameters).validateRequest().validatePOS();
            case COBRO_SERVICIO:
                return new CobroServicioAction(parameters).validateRequest().validatePOS();
            case TIEMPO_AIRE:
                return new TiempoAireAction(parameters).validateRequest().validatePOS();
            case CONSULTA_TIEMPO_AIRE:
                return new ConsultaTiempoAireAction(parameters).validateRequest().validatePOS();
        }
        return null;
    }//getAction
    @Override
    public DetiPOSAction getAnonymousAction(Object action, DinamicVO<String, String> parameters) throws DetiPOSFault {
        switch ((ACTIONS) action) {
            case CONDICIONES_TARJETA:
                return new CondicionesTarjetaAction(parameters).validateRequest();
            case IMPRIME_TICKET:
                return new ImprimeTicketAction(parameters).validateRequest();
            case RECUPERA_CONSUMOS:
                return new RecuperaConsumosAction(parameters).validateRequest();
            case IMPRIME_TRANSACCION:
                return new ImprimeTransaccionAction(parameters).validateRequest();
            case INVENTARIO_TANQUES:
                return new InventarioTanquesAction(parameters).validateRequest();
            case INVENTARIO_ADITIVOS:
                return new InventarioAditivosAction(parameters).validateRequest();
            case VENTA_ADITIVOS:
                return new VentaAditivosAction(parameters).validateRequest();
            case CONSUMO:
                return new ConsumoAction(parameters).validateRequest();
            case CAT_MANGUERA:
                return new CatalogoManguerasAction(parameters).validateRequest();
            case CHECK_PUMP:
                return new CheckDispensariosAction(parameters).validateRequest();
            case LIST_MENU:
                return new ListMenuAction(parameters).validateRequest();
            case LIST_AUTORIZADORES:
                return new ListMonederosAction(parameters).validateRequest();
            case LIST_BANK:
                return new ListTarjetasAction(parameters).validateRequest();
            case LIST_BANK_FLOTILLA:
                return new ListTarjetasFlotillasAction(parameters).validateRequest();
            case LIST_PAYMENTS:
                return new ListFormaPagoAction(parameters).validateRequest();
            case LIST_OPER_TA:
                return new ListOperadoresTAAction(parameters).validateRequest();
            case LIST_OPER_SERVICIOS:
                return new ListOperadoresServiciosAction(parameters).validateRequest();
            case LIST_SERVICIOS:
                return new ListServiciosAction(parameters).validateRequest();
            case LIST_MONTO_TA:
                return new ListMontosTAAction(parameters).validateRequest();
            case LIST_MENUS:
                return new ListMenusAction(parameters).validateRequest();
            case UPDATE_BOLETO:
                return new UpdateBoletoAction(parameters).validateRequest();
            case BOLETO:
                return new ConsultaBoletoAction(parameters).validateRequest();
            case CLIENTE:
                return new ConsultaClienteAction(parameters).validateRequest();
            case CLIENTE_RFC:
                return new ConsultaClienteByRFCAction(parameters).validateRequest();
            case TARJETA:
                return new ConsultaTarjetaAction(parameters).validateRequest();
            case SALDO_TARJETA:
                return new ConsultaSaldoTarjetaAction(parameters).validateRequest();
            case SALDO_CLIENTE:
                return new ConsultaSaldoClienteAction(parameters).validateRequest();
            case CORTE:
                return new CorteAction(parameters).validateRequest();
            case IMPRIME_CORTE:
                return new ImprimeCorteAction(parameters).validateRequest();
            case CORTE_PARCIAL:
                return new CorteParcialAction(parameters).validateRequest();
            case RECUPERA_CORTES:
                return new RecuperaUltimosCortesAction(parameters).validateRequest();
            case VALES:
                return new ValesAction(parameters).validateRequest();
            case CONF_ESTACION:
                return new GetEstacionAction(parameters).validateRequest();
            case CONF_LOGO:
                return new GetLogoAction(parameters).validateRequest();
            case CONF_LOGO_FACTURACION:
                return new GetLogoFacturacionAction(parameters).validateRequest();
            case CONF_FILES:
                return new GetConfigFilesAction(parameters).validateRequest();
            case CONF_FONTS:
                return new GetFontsAction(parameters).validateRequest();
            case CONF_UPDATE:
                return new CheckUpdatesAction(parameters).validateRequest();
            case COBRO_SERVICIO:
                return new CobroServicioAction(parameters).validateRequest();
            case TIEMPO_AIRE:
                return new TiempoAireAction(parameters).validateRequest();
        }
        return null;
    }
}//DposActionFactory
