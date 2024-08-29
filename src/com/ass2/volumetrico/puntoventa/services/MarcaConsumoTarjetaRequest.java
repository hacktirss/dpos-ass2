package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"idTerminal", "transaccion", "tarjeta", "password"})
@XmlRootElement(name = "MarcaCobroConsumoRequest", namespace = "http://detisa.com/omicrom/services/")
@Data
public class MarcaConsumoTarjetaRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String idTerminal;

    @XmlElement(name = "transaccion", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String transaccion;

    @XmlElement(name = "tarjeta", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String tarjeta;

    @XmlElement(name = "password", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String password;
}
