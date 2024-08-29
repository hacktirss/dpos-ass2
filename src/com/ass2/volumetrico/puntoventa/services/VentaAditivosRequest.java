/**
 * VentaAditivosRequest
 * DetiPOS® WEB Service
 * DetiPOS® Omicrom Services
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
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
    "posicion",
    "claveAditivo",
    "codigoAditivo",
    "cantidad",
    "claveBanco",
    "idTarjeta",
    "password"
})
@XmlRootElement(name = "VentaAditivosRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class VentaAditivosRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "posicion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String posicion;

    @XmlElement(name = "claveAditivo", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String claveAditivo;

    @XmlElement(name = "codigoAditivo", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String codigoAditivo;

    @XmlElement(name = "cantidad", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String cantidad;

    @XmlElement(name = "claveBanco", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String claveBanco;

    @XmlElement(name = "idTarjeta", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTarjeta;

    @XmlElement(name = "password", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String password;
}//VentaAditivosRequest
