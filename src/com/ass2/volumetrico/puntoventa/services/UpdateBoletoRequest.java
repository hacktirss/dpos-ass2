/**
 * UpdateBoletoRequest
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
    "boleto",
    "cargo",
    "ticket1",
    "ticket2",
    "activo"
})
@XmlRootElement(name = "UpdateBoletoRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class UpdateBoletoRequest {

    @XmlElement(name = "boleto", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String boleto;

    @XmlElement(name = "cargo", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String cargo;

    @XmlElement(name = "ticket1", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String ticket1;

    @XmlElement(name = "ticket2", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String ticket2;
    
    @XmlElement(name = "activo", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String activo;
}//UpdateBoletoRequest
