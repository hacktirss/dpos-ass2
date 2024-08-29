/**
 * UpdateDT0
 * DetiPOS® WEB Service
 * DetiPOS® Omicrom Services
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2017
 */
package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateDTO", propOrder = {
    "hasUpdate",
    "version",
    "md5"
}, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class UpdateDTO {
    @XmlElement(name = "hasUpdate", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected boolean hasUpdate;

    @XmlElement(name = "version", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String version;

    @XmlElement(name = "md5", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String md5;
}//UpdateDTO
