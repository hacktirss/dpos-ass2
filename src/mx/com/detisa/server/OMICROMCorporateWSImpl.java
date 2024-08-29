
package mx.com.detisa.server;

import java.math.BigDecimal;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


@WebService(name = "OMICROMCorporateWSImpl", targetNamespace = "http://server.detisa.mx.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface OMICROMCorporateWSImpl {

    public static final String CORP_NAMESPACE = "http://server.detisa.mx.com/";

    @WebMethod
    @WebResult(name = "pdf", targetNamespace = "")
    @RequestWrapper(localName = "obtenerCfdiLayout", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ObtenerCfdiLayout")
    @ResponseWrapper(localName = "obtenerCfdiLayoutResponse", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ObtenerCfdiLayoutResponse")
    @Action(input = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/obtenerCfdiLayoutRequest", output = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/obtenerCfdiLayoutResponse")
    public byte[] obtenerCfdiLayout(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "text", targetNamespace = "")
        byte[] text,
        @WebParam(name = "llave", targetNamespace = "")
        byte[] llave,
        @WebParam(name = "arg4", targetNamespace = "")
        byte[] arg4,
        @WebParam(name = "llaveCer", targetNamespace = "")
        String llaveCer,
        @WebParam(name = "arg6", targetNamespace = "")
        byte[] arg6,
        @WebParam(name = "arg7", targetNamespace = "")
        String arg7);

    @WebMethod
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "receiveMesage", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ReceiveMesage")
    @ResponseWrapper(localName = "receiveMesageResponse", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ReceiveMesageResponse")
    @Action(input = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/receiveMesageRequest", output = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/receiveMesageResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/receiveMesage/Fault/Exception")
    })
    public Response receiveMesage(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "msj", targetNamespace = "")
        String msj)
        throws Exception_Exception
    ;

    @WebMethod
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "receiveJSONMesage", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ReceiveJSONMesage")
    @ResponseWrapper(localName = "receiveJSONMesageResponse", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ReceiveJSONMesageResponse")
    @Action(input = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/receiveJSONMesageRequest", output = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/receiveJSONMesageResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/receiveJSONMesage/Fault/Exception")
    })
    public Response receiveJSONMesage(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "tipo", targetNamespace = "")
        String tipo,
        @WebParam(name = "msj", targetNamespace = "")
        String msj)
        throws Exception_Exception
    ;

    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "autorizaVenta", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.AutorizaVenta")
    @ResponseWrapper(localName = "autorizaVentaResponse", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.AutorizaVentaResponse")
    @Action(input = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/autorizaVentaRequest", output = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/autorizaVentaResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/autorizaVenta/Fault/Exception")
    })
    public CheckBalance autorizaVenta(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        BigDecimal arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        BigDecimal arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        String arg4,
        @WebParam(name = "arg5", targetNamespace = "")
        String arg5,
        @WebParam(name = "arg6", targetNamespace = "")
        String arg6,
        @WebParam(name = "arg7", targetNamespace = "")
        String arg7,
        @WebParam(name = "arg8", targetNamespace = "")
        String arg8)
        throws Exception_Exception
    ;

    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "confirmaVenta", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ConfirmaVenta")
    @ResponseWrapper(localName = "confirmaVentaResponse", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ConfirmaVentaResponse")
    @Action(input = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/confirmaVentaRequest", output = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/confirmaVentaResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/confirmaVenta/Fault/Exception")
    })
    public String confirmaVenta(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        BigDecimal arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        BigDecimal arg4,
        @WebParam(name = "arg5", targetNamespace = "")
        String arg5)
        throws Exception_Exception
    ;

    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "obtenUnidad", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ObtenUnidad")
    @ResponseWrapper(localName = "obtenUnidadResponse", targetNamespace = CORP_NAMESPACE, className = "mx.com.detisa.server.ObtenUnidadResponse")
    @Action(input = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/obtenUnidadRequest", output = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/obtenUnidadResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://server.detisa.mx.com/OMICROMCorporateWSImpl/autorizaVenta/Fault/Exception")
    })
    public DatosClientePrepagoTO obtenUnidad(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1)
        throws Exception_Exception
    ;}
