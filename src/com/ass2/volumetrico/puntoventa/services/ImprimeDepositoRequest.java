package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"idTerminal", "corte", "vendedor", "password"})
@XmlRootElement(name = "ImprimeDepositoRequest", namespace = "http://detisa.com/omicrom/services/")
@Data
public class ImprimeDepositoRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String idTerminal;

    @XmlElement(name = "corte", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String corte;

    @XmlElement(name = "vendedor", required = true, namespace = "http://detisa.com/omicrom/services/")
    protected String vendedor;

    @XmlElement(name = "password", required = false, namespace = "http://detisa.com/omicrom/services/")
    protected String password;
}
