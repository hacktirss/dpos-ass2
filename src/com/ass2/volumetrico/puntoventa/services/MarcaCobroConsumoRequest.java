package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"idTerminal", "transaccion", "cadena"})
@XmlRootElement(name = "MarcaCobroConsumoRequest", namespace = "http://detisa.com/omicrom/services/")
@Data
public class MarcaCobroConsumoRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String idTerminal;

    @XmlElement(name = "transaccion", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String transaccion;

    @XmlElement(name = "cadena", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String cadena;
}
