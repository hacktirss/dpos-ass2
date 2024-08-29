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
    "posicion",
    "transaccion",
    "efectivo",
    "password",
    "aditivos"
})
@XmlRootElement(name = "ImprimeTransaccionComplexRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class ImprimeTransaccionComplexRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "posicion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String posicion;

    @XmlElement(name = "transaccion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String transaccion;

    @XmlElement(name = "efectivo", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String efectivo;

    @XmlElement(name = "password", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String password;

    @XmlElement(name = "aditivos", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected List <VentaAditivo> aditivos;
}//ImprimeTransaccionRequest
