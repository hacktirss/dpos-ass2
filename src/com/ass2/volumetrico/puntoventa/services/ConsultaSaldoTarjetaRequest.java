package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tarjeta"
})
@XmlRootElement(name = "ConsultaSaldoTarjetaRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class ConsultaSaldoTarjetaRequest {

    @XmlElement(name = "tarjeta", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String tarjeta;
}//ConsultaSaldoTarjetaRequest
