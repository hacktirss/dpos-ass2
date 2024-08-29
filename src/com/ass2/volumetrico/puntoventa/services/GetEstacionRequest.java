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
    "lanMacAdd",
    "wlanMacAdd",
    "kernelVersion"
})
@XmlRootElement(name = "GetEstacionRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class GetEstacionRequest {

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "lanMacAdd", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String lanMacAdd;

    @XmlElement(name = "wlanMacAdd", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String wlanMacAdd;

    @XmlElement(name = "kernelVersion", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String kernelVersion;
}//GetLogoRequest
