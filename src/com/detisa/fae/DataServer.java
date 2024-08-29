package com.detisa.fae;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "DataServer", targetNamespace = "http://detisa.com/fae/")
@XmlSeeAlso({
    com.detisa.fae.ObjectFactory.class,
    com.ass2.volumetrico.puntoventa.services.ObjectFactory.class
})
public interface DataServer {

    @WebMethod(action = "detipos:validaConsumo")
    @WebResult(name = "CheckBalance", targetNamespace = "")
    @RequestWrapper(localName = "validaConsumo", targetNamespace = "http://detisa.com/fae/", className = "com.detisa.fae.ValidaConsumo")
    @ResponseWrapper(localName = "validaConsumoResponse", targetNamespace = "http://detisa.com/fae/", className = "com.detisa.fae.ValidaConsumoResponse")
    @Action(input = "detipos:validaConsumo", output = "http://detisa.com/fae/DataServer/validaConsumoResponse", fault = {
        @FaultAction(className = DetiPOSFault.class, value = "http://detisa.com/fae/DataServer/validaConsumo/Fault/DetiPOSFault"),
        @FaultAction(className = Exception_Exception.class, value = "http://detisa.com/fae/DataServer/validaConsumo/Fault/Exception")
    })
    public CheckBalance validaConsumo(
        @WebParam(name = "idfae", targetNamespace = "")
        int idfae,
        @WebParam(name = "codigo", targetNamespace = "")
        String codigo,
        @WebParam(name = "nip", targetNamespace = "")
        String nip,
        @WebParam(name = "montoSolicitado", targetNamespace = "")
        double montoSolicitado,
        @WebParam(name = "litrosSolicitado", targetNamespace = "")
        double litrosSolicitado,
        @WebParam(name = "claveProducto", targetNamespace = "")
        String claveProducto)
        throws DetiPOSFault, Exception_Exception
    ;

    @WebMethod(action = "detipos:validaConsumoVales")
    @WebResult(name = "CheckBalance", targetNamespace = "")
    @RequestWrapper(localName = "validaConsumoVales", targetNamespace = "http://detisa.com/fae/", className = "com.detisa.fae.ValidaConsumoVales")
    @ResponseWrapper(localName = "validaConsumoValesResponse", targetNamespace = "http://detisa.com/fae/", className = "com.detisa.fae.ValidaConsumoValesResponse")
    @Action(input = "detipos:validaConsumoVales", output = "http://detisa.com/fae/DataServer/validaConsumoValesResponse", fault = {
        @FaultAction(className = DetiPOSFault.class, value = "http://detisa.com/fae/DataServer/validaConsumoVales/Fault/DetiPOSFault"),
        @FaultAction(className = Exception_Exception.class, value = "http://detisa.com/fae/DataServer/validaConsumoVales/Fault/Exception")
    })
    public CheckBalance validaConsumoVales(
        @WebParam(name = "idfae", targetNamespace = "")
        int idfae,
        @WebParam(name = "codigo", targetNamespace = "")
        String codigo,
        @WebParam(name = "nip", targetNamespace = "")
        String nip)
        throws DetiPOSFault, Exception_Exception
    ;

    @WebMethod(action = "detipos:consultaSaldoCliente")
    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "consultaSaldoCliente", targetNamespace = "http://detisa.com/fae/", className = "com.detisa.fae.ConsultaSaldoCliente")
    @ResponseWrapper(localName = "consultaSaldoClienteResponse", targetNamespace = "http://detisa.com/fae/", className = "com.detisa.fae.ConsultaSaldoClienteResponse")
    @Action(input = "detipos:consultaSaldoCliente", output = "http://detisa.com/fae/DataServer/validaConsumoResponse", fault = {
        @FaultAction(className = DetiPOSFault.class, value = "http://detisa.com/fae/DataServer/validaConsumo/Fault/DetiPOSFault"),
        @FaultAction(className = Exception_Exception.class, value = "http://detisa.com/fae/DataServer/validaConsumo/Fault/Exception")})
    String consultaSaldoCliente(
            @WebParam(name = "idfae", targetNamespace = "") 
            String idfae, 
            @WebParam(name = "idcliente", targetNamespace = "") 
            String idcliente) throws DetiPOSFault, Exception_Exception;
}
