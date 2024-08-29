/**
 * VentaDivisasRequest
 * DetiPOS® WEB Service
 * DetiPOS® Omicrom Services
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2018
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
    "transaccion",
    "divisa",
    "password"
})
@XmlRootElement(name = "VentaDivisasRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class VentaDivisasRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "transaccion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String transaccion;

    @XmlElement(name = "divisa", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    private String divisa;

    @XmlElement(name = "password", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String password;
}//ImprimeJarreoRequest
