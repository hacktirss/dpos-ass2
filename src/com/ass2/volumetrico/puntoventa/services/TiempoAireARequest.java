/*
 * TiempoAireARequest
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2016
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
    "codigoOperador",
    "numero",
    "importe",
    "formaPago",
    "authBancaria"
})
@XmlRootElement(name = "TiempoAireARequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class TiempoAireARequest {
    @XmlElement(name = "codigoOperador", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String codigoOperador;
    @XmlElement(name = "numero", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String numero;
    @XmlElement(name = "importe", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String importe;
    @XmlElement(name = "formaPago", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String formaPago;
    @XmlElement(name = "authBancaria", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String authBancaria;
}//TiempoAireARequest
