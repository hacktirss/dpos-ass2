package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroTarjetasDTO", propOrder = {
    "idTarjeta",
    "impreso"
}, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class RegistroTarjetasDTO {
    @XmlElement(name = "idTarjeta", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTarjeta;

    @XmlElement(name = "impreso", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String impreso;
}//RegistroTarjetasDTO
