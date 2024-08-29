package com.ass2.volumetrico.puntoventa.services;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idTerminal",
    "tarjetas",
    "password"
})
@XmlRootElement(name = "RegistroTarjetaRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class RegistroTarjetaRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "tarjetas", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected List <RegistroTarjetasDTO> tarjetas;

    @XmlElement(name = "password", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String password;
}//RegistroTarjetaRequest
