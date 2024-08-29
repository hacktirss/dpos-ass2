/**
 * VentaAditivo
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
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VentaAditivo", propOrder = {
    "claveAditivo",
    "codigoAditivo",
    "cantidad"
})
@Data
public class VentaAditivo {
    
    @XmlElement(name = "claveAditivo", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String claveAditivo;

    @XmlElement(name = "codigoAditivo", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String codigoAditivo;

    @XmlElement(name = "cantidad", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String cantidad;
}//VentaAditivo
