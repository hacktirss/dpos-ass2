/**
 * DetiPOSPort
 * DetiPOS® WEB Service
 * DetiPOS® Omicrom Services
 * © 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
package com.ass2.volumetrico.puntoventa.services;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import static com.ass2.volumetrico.puntoventa.services.DetiPOSPort.DPOS_WS_NAMESPACE;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;

@WebService(name = "DetiPOS", targetNamespace = DPOS_WS_NAMESPACE)
@XmlSeeAlso(
        ObjectFactory.class)
public interface DetiPOSPort {

    public static final String DPOS_WS_NAMESPACE = "http://detisa.com/omicrom/services/";

    @WebMethod(operationName = "ImprimeComprobante", action = "detipos:ImprimeComprobante")
    @RequestWrapper(localName = "ImprimeComprobanteRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimeComprobanteRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimeComprobante(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ImprimePuntos", action = "detipos:ImprimePuntos")
    @RequestWrapper(localName = "ImprimePuntosRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimePuntosRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimePuntos(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ImprimeTransaccion", action = "detipos:ImprimeTransaccion")
    @RequestWrapper(localName = "ImprimeTransaccionRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimeTransaccionRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimeTransaccion(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "efectivo", targetNamespace = DPOS_WS_NAMESPACE) String efectivo,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ImprimeJarreo", action = "detipos:ImprimeJarreo")
    @RequestWrapper(localName = "ImprimeJarreoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimeJarreoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimeJarreo(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "tipo", targetNamespace = DPOS_WS_NAMESPACE) String tipo,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ImprimeDeposito", action = "detipos:ImprimeDeposito")
    @RequestWrapper(localName = "ImprimeDepositoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimeDepositoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimeDeposito(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "corte", targetNamespace = DPOS_WS_NAMESPACE) String corte,
            @WebParam(name = "vendedor", targetNamespace = DPOS_WS_NAMESPACE) String vendedor,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "MarcaCobroConsumo", action = "detipos:MarcaCobroConsumo")
    @RequestWrapper(localName = "MarcaCobroConsumoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.MarcaCobroConsumoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String marcaCobroConsumo(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "cadena", targetNamespace = DPOS_WS_NAMESPACE) String cadena) throws DetiPOSFault;

    @WebMethod(operationName = "CobroCODIConsumo", action = "detipos:CobroCODIConsumo")
    @RequestWrapper(localName = "CobroCODIConsumoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CobroCODIConsumoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String cobroCODIConsumo(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "telefono", targetNamespace = DPOS_WS_NAMESPACE) String telefono) throws DetiPOSFault;

    @WebMethod(operationName = "MarcaConsumoTarjeta", action = "detipos:MarcaConsumoTarjeta")
    @RequestWrapper(localName = "MarcaConsumoTarjetaRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.MarcaConsumoTarjetaRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String marcaConsumoTarjeta(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "tarjeta", targetNamespace = DPOS_WS_NAMESPACE) String tarjeta,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "CapturaDeposito", action = "detipos:CapturaDeposito")
    @RequestWrapper(localName = "CapturaDepositoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CapturaDepositoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String capturaDeposito(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "corte", targetNamespace = DPOS_WS_NAMESPACE) String corte,
            @WebParam(name = "importe", targetNamespace = DPOS_WS_NAMESPACE) String importe,
            @WebParam(name = "vendedor", targetNamespace = DPOS_WS_NAMESPACE) String vendedor,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "VentaDivisas", action = "detipos:VentaDvisas")
    @RequestWrapper(localName = "VentaDivisasRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.VentaDivisasRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String ventaDivisas(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "divisa", targetNamespace = DPOS_WS_NAMESPACE) String divis,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ImprimeTransaccionComplex", action = "detipos:ImprimeTransaccionComplex")
    @RequestWrapper(localName = "ImprimeTransaccionComplexRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimeTransaccionComplexRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimeTransaccionComplex(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion,
            @WebParam(name = "efectivo", targetNamespace = DPOS_WS_NAMESPACE) String efectivo,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password,
            @WebParam(name = "aditivos", targetNamespace = DPOS_WS_NAMESPACE) List<VentaAditivo> aditivos
    ) throws DetiPOSFault;

    @WebMethod(operationName = "RecuperaUltimosConsumos", action = "detipos:RecuperaUltimosConsumos")
    @RequestWrapper(localName = "RecuperaUltimosConsumosRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.RecuperaUltimosConsumosRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String recuperaUltimosConsumos(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "CobroTarjetaPosicion", action = "detipos:CobroTarjetaPosicion")
    @RequestWrapper(localName = "CobroTarjetaPosicionRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CobroTarjetaPosicionRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String cobroTarjetaPosicion(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "InventarioTanques", action = "detipos:InventarioTanques")
    @RequestWrapper(localName = "InventarioTanquesRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.InventarioTanquesRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String inventarioTanques(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "InventarioAditivos", action = "detipos:InventarioAditivos")
    @RequestWrapper(localName = "InventarioAditivosRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.InventarioAditivosRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String inventarioAditivos(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "isla", targetNamespace = DPOS_WS_NAMESPACE) String isla,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "CondicionesTarjeta", action = "detipos:CondicionesTarjeta")
    @RequestWrapper(localName = "CondicionesTarjetaRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CondicionesTarjetaRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String condicionesTarjeta(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "idTarjeta", targetNamespace = DPOS_WS_NAMESPACE) String idTarjeta,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "SaldoCliente", action = "detipos:SaldoCliente")
    @RequestWrapper(localName = "SaldoClienteRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.SaldoClienteRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String saldoCliente(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "cliente", targetNamespace = DPOS_WS_NAMESPACE) String cliente,
            @WebParam(name = "idTarjeta", targetNamespace = DPOS_WS_NAMESPACE) String idTarjeta,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "RegistroTarjeta", action = "detipos:RegistroTarjeta")
    @RequestWrapper(localName = "RegistroTarjetaRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.RegistroTarjetaRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String registroTarjeta(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "tarjetas", targetNamespace = DPOS_WS_NAMESPACE) List<RegistroTarjetasDTO> tarjetas,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "VentaAditivos", action = "detipos:VentaAditivos")
    @RequestWrapper(localName = "VentaAditivosRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.VentaAditivosRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String ventaAditivos(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "claveAditivo", targetNamespace = DPOS_WS_NAMESPACE) String claveAditivo,
            @WebParam(name = "codigoAditivo", targetNamespace = DPOS_WS_NAMESPACE) String codigoAditivo,
            @WebParam(name = "cantidad", targetNamespace = DPOS_WS_NAMESPACE) String cantidad,
            @WebParam(name = "claveBanco", targetNamespace = DPOS_WS_NAMESPACE) String claveBanco,
            @WebParam(name = "idTarjeta", targetNamespace = DPOS_WS_NAMESPACE) String idTarjeta,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "Consumo", action = "detipos:Consumo")
    @RequestWrapper(localName = "ConsumoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsumoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consumo(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "tipo", targetNamespace = DPOS_WS_NAMESPACE) String tipo,
            @WebParam(name = "cantidad", targetNamespace = DPOS_WS_NAMESPACE) String cantidad,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "manguera", targetNamespace = DPOS_WS_NAMESPACE) String manguera,
            @WebParam(name = "idTarjeta", targetNamespace = DPOS_WS_NAMESPACE) String idTarjeta,
            @WebParam(name = "claveBanco", targetNamespace = DPOS_WS_NAMESPACE) String claveBanco,
            @WebParam(name = "formaPago", targetNamespace = DPOS_WS_NAMESPACE) String formaPago,
            @WebParam(name = "despachador", targetNamespace = DPOS_WS_NAMESPACE) String despachador,
            @WebParam(name = "passwordDespachador", targetNamespace = DPOS_WS_NAMESPACE) String passwordDespachador,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password,
            @WebParam(name = "odometro", targetNamespace = DPOS_WS_NAMESPACE) String odometro,
            @WebParam(name = "eco", targetNamespace = DPOS_WS_NAMESPACE) String eco) throws DetiPOSFault;

    @WebMethod(operationName = "ConsumoIntegracion", action = "detipos:ConsumoIntegracion")
    @RequestWrapper(localName = "ConsumoIntegracionRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsumoIntegracionRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consumoIntegracion(
            @WebParam(name = "idIntegracion", targetNamespace = DPOS_WS_NAMESPACE) String idIntegracion,
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "tipo", targetNamespace = DPOS_WS_NAMESPACE) String tipo,
            @WebParam(name = "cantidad", targetNamespace = DPOS_WS_NAMESPACE) String cantidad,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "manguera", targetNamespace = DPOS_WS_NAMESPACE) String manguera,
            @WebParam(name = "idTarjeta", targetNamespace = DPOS_WS_NAMESPACE) String idTarjeta,
            @WebParam(name = "claveBanco", targetNamespace = DPOS_WS_NAMESPACE) String claveBanco,
            @WebParam(name = "formaPago", targetNamespace = DPOS_WS_NAMESPACE) String formaPago,
            @WebParam(name = "despachador", targetNamespace = DPOS_WS_NAMESPACE) String despachador,
            @WebParam(name = "passwordDespachador", targetNamespace = DPOS_WS_NAMESPACE) String passwordDespachador,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password,
            @WebParam(name = "odometro", targetNamespace = DPOS_WS_NAMESPACE) String odometro) throws DetiPOSFault;

    @WebMethod(operationName = "CallAutorizador", action = "detipos:CallAutorizador")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String callAutorizador(
            @WebParam(name = "idIntegracion", targetNamespace = DPOS_WS_NAMESPACE) String idIntegracion,
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "action", targetNamespace = DPOS_WS_NAMESPACE) String action,
            @WebParam(name = "tipo", targetNamespace = DPOS_WS_NAMESPACE) String tipo,
            @WebParam(name = "cantidad", targetNamespace = DPOS_WS_NAMESPACE) String cantidad,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "manguera", targetNamespace = DPOS_WS_NAMESPACE) String manguera,
            @WebParam(name = "idTarjeta", targetNamespace = DPOS_WS_NAMESPACE) String idTarjeta,
            @WebParam(name = "employee", targetNamespace = DPOS_WS_NAMESPACE) String employee,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password,
            @WebParam(name = "odometro", targetNamespace = DPOS_WS_NAMESPACE) String odometro,
            @WebParam(name = "numeco", targetNamespace = DPOS_WS_NAMESPACE) String numeco,
            @WebParam(name = "transaccion", targetNamespace = DPOS_WS_NAMESPACE) String transaccion) throws DetiPOSFault;

    @WebMethod(operationName = "Corte", action = "detipos:Corte")
    @RequestWrapper(localName = "CorteRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CorteRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String corte(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "CorteOmicrom", action = "detipos:CorteOmicrom")
    @RequestWrapper(localName = "CorteOmicromRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CorteOmicromRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String corteOmicrom(
            @WebParam(name = "isla", targetNamespace = DPOS_WS_NAMESPACE) String isla,
            @WebParam(name = "proceso", targetNamespace = DPOS_WS_NAMESPACE) String proceso) throws DetiPOSFault;

    @WebMethod(operationName = "RecuperaUltimosCortes", action = "detipos:RecuperaUltimosCortes")
    @RequestWrapper(localName = "RecuperaUltimosCortesRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.RecuperaUltimosCortesRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String recuperaUltimosCortes(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ImprimeCorte", action = "detipos:ImprimeCorte")
    @RequestWrapper(localName = "ImprimeCorteRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ImprimeCorteRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String imprimeCorte(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "isla", targetNamespace = DPOS_WS_NAMESPACE) String isla,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "corteID", targetNamespace = DPOS_WS_NAMESPACE) String corteID,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "CorteParcial", action = "detipos:CorteParcial")
    @RequestWrapper(localName = "CorteParcialRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CorteParcialRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String corteParcial(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "isla", targetNamespace = DPOS_WS_NAMESPACE) String isla,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "Vales", action = "detipos:Vales")
    @RequestWrapper(localName = "ValesRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ValesRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String vales(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "idVale", targetNamespace = DPOS_WS_NAMESPACE) String idVale,
            @WebParam(name = "posicion", targetNamespace = DPOS_WS_NAMESPACE) String posicion,
            @WebParam(name = "manguera", targetNamespace = DPOS_WS_NAMESPACE) String manguera,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password) throws DetiPOSFault;

    @WebMethod(operationName = "ListMenu", action = "detipos:ListMenu")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listMenu() throws DetiPOSFault;

    @WebMethod(operationName = "ListMonederos", action = "detipos:ListMonederos")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listMonederos() throws DetiPOSFault;

    @WebMethod(operationName = "ListTarjetas", action = "detipos:ListTarjetas")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listTarjetas() throws DetiPOSFault;

    @WebMethod(operationName = "ListTarjetasFlotillas", action = "detipos:ListTarjetasFlotillas")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listTarjetasFlotillas() throws DetiPOSFault;

    @WebMethod(operationName = "ListFormaPago", action = "detipos:ListFormaPago")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listFormaPago() throws DetiPOSFault;

    @WebMethod(operationName = "ListOperadoresTA", action = "detipos:ListOperadoresTA")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listOperadoresTA() throws DetiPOSFault;

    @WebMethod(operationName = "ListOperadoresServicios", action = "detipos:ListOperadoresServicios")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listOperadoresServicios() throws DetiPOSFault;

    @WebMethod(operationName = "ListMontosTA", action = "detipos:ListMontosTA")
    @RequestWrapper(localName = "ListMontosTARequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ListMontosTARequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listMontosTA(
            @WebParam(name = "codigoOper", targetNamespace = DPOS_WS_NAMESPACE) String codigoOper) throws DetiPOSFault;

    @WebMethod(operationName = "ListServicios", action = "detipos:ListServicios")
    @RequestWrapper(localName = "ListServiciosRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ListServiciosRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listServicios(
            @WebParam(name = "codigoOper", targetNamespace = DPOS_WS_NAMESPACE) String codigoOper) throws DetiPOSFault;

    @WebMethod(operationName = "CobroServicio", action = "detipos:CobroServicio")
    @RequestWrapper(localName = "CobroServicioRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CobroServicioRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String cobroServicio(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "codigoServicio", targetNamespace = DPOS_WS_NAMESPACE) String codigoServicio,
            @WebParam(name = "codigoProducto", targetNamespace = DPOS_WS_NAMESPACE) String codigoProducto,
            @WebParam(name = "lineaDeCaptura", targetNamespace = DPOS_WS_NAMESPACE) String lineaDeCaptura,
            @WebParam(name = "importe", targetNamespace = DPOS_WS_NAMESPACE) String importe,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password,
            @WebParam(name = "formaPago", targetNamespace = DPOS_WS_NAMESPACE) String formaPago,
            @WebParam(name = "authBancaria", targetNamespace = DPOS_WS_NAMESPACE) String authBancaria
    ) throws DetiPOSFault;

    @WebMethod(operationName = "CobroServicioA", action = "detipos:CobroServicioA")
    @RequestWrapper(localName = "CobroServicioARequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CobroServicioARequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String cobroServicioA(
            @WebParam(name = "codigoServicio", targetNamespace = DPOS_WS_NAMESPACE) String codigoServicio,
            @WebParam(name = "codigoProducto", targetNamespace = DPOS_WS_NAMESPACE) String codigoProducto,
            @WebParam(name = "lineaDeCaptura", targetNamespace = DPOS_WS_NAMESPACE) String lineaDeCaptura,
            @WebParam(name = "importe", targetNamespace = DPOS_WS_NAMESPACE) String importe,
            @WebParam(name = "formaPago", targetNamespace = DPOS_WS_NAMESPACE) String formaPago,
            @WebParam(name = "authBancaria", targetNamespace = DPOS_WS_NAMESPACE) String authBancaria
    ) throws DetiPOSFault;

    @WebMethod(operationName = "TiempoAire", action = "detipos:TiempoAire")
    @RequestWrapper(localName = "TiempoAireRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.TiempoAireRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String tiempoAire(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "codigoOperador", targetNamespace = DPOS_WS_NAMESPACE) String codigoOperador,
            @WebParam(name = "numero", targetNamespace = DPOS_WS_NAMESPACE) String numero,
            @WebParam(name = "importe", targetNamespace = DPOS_WS_NAMESPACE) String importe,
            @WebParam(name = "password", targetNamespace = DPOS_WS_NAMESPACE) String password,
            @WebParam(name = "formaPago", targetNamespace = DPOS_WS_NAMESPACE) String formaPago,
            @WebParam(name = "authBancaria", targetNamespace = DPOS_WS_NAMESPACE) String authBancaria
    ) throws DetiPOSFault;

    @WebMethod(operationName = "TiempoAireA", action = "detipos:TiempoAireA")
    @RequestWrapper(localName = "TiempoAireARequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.TiempoAireARequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String tiempoAireA(
            @WebParam(name = "codigoOperador", targetNamespace = DPOS_WS_NAMESPACE) String codigoOperador,
            @WebParam(name = "numero", targetNamespace = DPOS_WS_NAMESPACE) String numero,
            @WebParam(name = "importe", targetNamespace = DPOS_WS_NAMESPACE) String importe,
            @WebParam(name = "formaPago", targetNamespace = DPOS_WS_NAMESPACE) String formaPago,
            @WebParam(name = "authBancaria", targetNamespace = DPOS_WS_NAMESPACE) String authBancaria
    ) throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaTiempoAire", action = "detipos:ConsultaTiempoAire")
    @RequestWrapper(localName = "ConsultaTiempoAireRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaTiempoAireRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaTiempoAire(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "idTransaccion", targetNamespace = DPOS_WS_NAMESPACE) String idTransaccion
    ) throws DetiPOSFault;

    @WebMethod(operationName = "ListMenus", action = "detipos:ListMenus")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String listMenus() throws DetiPOSFault;

    @WebMethod(operationName = "CheckDispensarios", action = "detipos:CheckDispensarios")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String checkDispensarios() throws DetiPOSFault;

    @WebMethod(operationName = "CatalogoMangueras", action = "detipos:CatalogoMangueras")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String catalogoMangueras() throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaSaldoCliente", action = "detipos:ConsultaSaldoCliente")
    @RequestWrapper(localName = "ConsultaSaldoClienteRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaSaldoClienteRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaSaldoCliente(
            @WebParam(name = "cliente", targetNamespace = DPOS_WS_NAMESPACE) String cliente) throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaSaldoTarjeta", action = "detipos:ConsultaSaldoTarjeta")
    @RequestWrapper(localName = "ConsultaSaldoTarjetaRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaSaldoTarjetaRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaSaldoTarjeta(
            @WebParam(name = "tarjeta", targetNamespace = DPOS_WS_NAMESPACE) String tarjeta) throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaTarjeta", action = "detipos:ConsultaTarjeta")
    @RequestWrapper(localName = "ConsultaTarjetaRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaTarjetaRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaTarjeta(
            @WebParam(name = "tarjeta", targetNamespace = DPOS_WS_NAMESPACE) String tarjeta) throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaCliente", action = "detipos:ConsultaCliente")
    @RequestWrapper(localName = "ConsultaClienteRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaClienteRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaCliente(
            @WebParam(name = "cliente", targetNamespace = DPOS_WS_NAMESPACE) String cliente) throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaClienteByRFC", action = "detipos:ConsultaClienteByRFC")
    @RequestWrapper(localName = "ConsultaClienteByRFCRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaClienteByRFCRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaClienteByRFC(
            @WebParam(name = "rfc", targetNamespace = DPOS_WS_NAMESPACE) String rfc) throws DetiPOSFault;

    @WebMethod(operationName = "ConsultaBoleto", action = "detipos:ConsultaBoleto")
    @RequestWrapper(localName = "ConsultaBoletoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ConsultaBoletoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String consultaBoleto(
            @WebParam(name = "boleto", targetNamespace = DPOS_WS_NAMESPACE) String boleto) throws DetiPOSFault;

    @WebMethod(operationName = "UpdateBoleto", action = "detipos:UpdateBoleto")
    @RequestWrapper(localName = "UpdateBoletoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.UpdateBoletoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String updateBoleto(
            @WebParam(name = "boleto", targetNamespace = DPOS_WS_NAMESPACE) String boleto,
            @WebParam(name = "cargo", targetNamespace = DPOS_WS_NAMESPACE) String cargo,
            @WebParam(name = "ticket1", targetNamespace = DPOS_WS_NAMESPACE) String ticket1,
            @WebParam(name = "ticket2", targetNamespace = DPOS_WS_NAMESPACE) String ticket2,
            @WebParam(name = "activo", targetNamespace = DPOS_WS_NAMESPACE) String activo) throws DetiPOSFault;

    // Configuration Services
    @WebMethod(operationName = "GetEstacion", action = "detipos:GetEstacion")
    @RequestWrapper(localName = "GetEstacionRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.GetEstacionRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String getEstacion(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal,
            @WebParam(name = "lanMacAdd", targetNamespace = DPOS_WS_NAMESPACE) String lanMacAdd,
            @WebParam(name = "wlanMacAdd", targetNamespace = DPOS_WS_NAMESPACE) String wlanMacAdd,
            @WebParam(name = "kernelVersion", targetNamespace = DPOS_WS_NAMESPACE) String kernelVersion) throws DetiPOSFault;

    @WebMethod(operationName = "GetLogo", action = "detipos:GetLogo")
    @RequestWrapper(localName = "GetLogoRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.GetLogoRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String getLogo(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal) throws DetiPOSFault;

    @WebMethod(operationName = "GetLogoFacturacion", action = "detipos:GetLogoFacturacion")
    @RequestWrapper(localName = "GetLogoFacturacionRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.GetLogoFacturacionRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String getLogoFacturacion(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal) throws DetiPOSFault;

    @WebMethod(operationName = "GetConfigFiles", action = "detipos:GetConfigFiles")
    @RequestWrapper(localName = "GetConfigFilesRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.GetConfigFilesRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String getConfigFiles(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal) throws DetiPOSFault;

    @WebMethod(operationName = "GetFonts", action = "detipos:GetFonts")
    @RequestWrapper(localName = "GetFontsRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.GetFontsRequest")
    @ResponseWrapper(localName = "ComprobanteResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.ComprobanteResponse")
    public @WebResult(name = "comprobante", targetNamespace = DPOS_WS_NAMESPACE)
    String getFonts(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal) throws DetiPOSFault;

    @WebMethod(operationName = "CheckUpdates", action = "detipos:CheckUpdates")
    @RequestWrapper(localName = "CheckUpdatesRequest", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CheckUpdatesRequest")
    @ResponseWrapper(localName = "CheckUpdatesResponse", targetNamespace = DPOS_WS_NAMESPACE, className = "com.detisa.omicrom.services.CheckUpdatesResponse")
    public @WebResult(name = "update", targetNamespace = DPOS_WS_NAMESPACE)
    UpdateDTO checkUpdates(
            @WebParam(name = "idTerminal", targetNamespace = DPOS_WS_NAMESPACE) String idTerminal) throws DetiPOSFault;
}//RemedyPort
