/*
 * ConsultaTiempoAireRequest
 * ASS2PuntoVenta®
 * © 2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2016
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
    "idTransaccion"
})
@XmlRootElement(name = "ConsultaTiempoAireRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class ConsultaTiempoAireRequest {
    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;
    @XmlElement(name = "idTransaccion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTransaccion;
}//ConsultaTiempoAireRequest
