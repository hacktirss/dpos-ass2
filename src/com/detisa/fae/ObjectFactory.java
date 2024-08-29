package com.detisa.fae;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private static final QName _ValidaConsumo_QNAME = new QName("http://detisa.com/fae/", "validaConsumo");
    private static final QName _ValidaConsumoVales_QNAME = new QName("http://detisa.com/fae/", "validaConsumoVales");
    private static final QName _ValidaConsumoResponse_QNAME = new QName("http://detisa.com/fae/", "validaConsumoResponse");
    private static final QName _ValidaConsumoValesResponse_QNAME = new QName("http://detisa.com/fae/", "validaConsumoValesResponse");
    private static final QName _ConsultaSaldoCliente_QNAME = new QName("http://detisa.com/fae/", "consultaSaldoCliente");
    private static final QName _ConsultaSaldoClienteResponse_QNAME = new QName("http://detisa.com/fae/", "consultaSaldoClienteResponse");
    private static final QName _Exception_QNAME = new QName("http://detisa.com/fae/", "Exception");

    public Exception createException() {
        return new Exception();
    }

    public ValidaConsumoResponse createValidaConsumoResponse() {
        return new ValidaConsumoResponse();
    }

    public ValidaConsumoValesResponse createValidaConsumoValesResponse() {
        return new ValidaConsumoValesResponse();
    }

    public ValidaConsumo createValidaConsumo() {
        return new ValidaConsumo();
    }

    public ValidaConsumoVales createValidaConsumoVales() {
        return new ValidaConsumoVales();
    }

    public ConsultaSaldoCliente createConsultaSaldoCliente() {
        return new ConsultaSaldoCliente();
    }

    public ConsultaSaldoClienteResponse createConsultaSaldoClienteResponse() {
        return new ConsultaSaldoClienteResponse();
    } 

    public CheckBalance createCheckBalance() {
        return new CheckBalance();
    }

    @XmlElementDecl(namespace = "http://detisa.com/fae/", name = "validaConsumo")
    public JAXBElement<ValidaConsumo> createValidaConsumo(ValidaConsumo value) {
        return new JAXBElement<ValidaConsumo>(_ValidaConsumo_QNAME, ValidaConsumo.class, null, value);
    }

    @XmlElementDecl(namespace = "http://detisa.com/fae/", name = "validaConsumoVales")
    public JAXBElement<ValidaConsumoVales> createValidaConsumoVales(ValidaConsumoVales value) {
        return new JAXBElement<ValidaConsumoVales>(_ValidaConsumoVales_QNAME, ValidaConsumoVales.class, null, value);
    }

    @XmlElementDecl(namespace = "http://detisa.com/fae/", name = "validaConsumoResponse")
    public JAXBElement<ValidaConsumoResponse> createValidaConsumoResponse(ValidaConsumoResponse value) {
        return new JAXBElement<ValidaConsumoResponse>(_ValidaConsumoResponse_QNAME, ValidaConsumoResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://detisa.com/fae/", name = "validaConsumoValesResponse")
    public JAXBElement<ValidaConsumoValesResponse> createValidaConsumoValesResponse(ValidaConsumoValesResponse value) {
        return new JAXBElement<ValidaConsumoValesResponse>(_ValidaConsumoValesResponse_QNAME, ValidaConsumoValesResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://detisa.com/fae/", name = "consultaSaldoCliente")
    public JAXBElement<ConsultaSaldoCliente> createConsultaSaldoCliente(ConsultaSaldoCliente value) {
        return new JAXBElement<ConsultaSaldoCliente>(_ConsultaSaldoCliente_QNAME, ConsultaSaldoCliente.class, null, value);
    }

    @XmlElementDecl(namespace = "http://detisa.com/fae/", name = "consultaSaldoClienteResponse")
    public JAXBElement<ConsultaSaldoClienteResponse> createConsultaSaldoClienteResponse(ConsultaSaldoClienteResponse value) {
        return new JAXBElement<ConsultaSaldoClienteResponse>(_ConsultaSaldoClienteResponse_QNAME, ConsultaSaldoClienteResponse.class, null, value);
    }
}
