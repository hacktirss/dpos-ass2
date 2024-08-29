package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idTerminal",
    "transaccion",
    "tipo",
    "password"
})
@XmlRootElement(name = "ImprimeJarreoRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class ImprimeJarreoRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "transaccion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String transaccion;

    @XmlElement(name = "tipo", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    private String tipo;

    @XmlElement(name = "password", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String password;
}//ImprimeJarreoRequest
