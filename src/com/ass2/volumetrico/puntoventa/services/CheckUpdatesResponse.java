package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"update"})
@XmlRootElement(name = "CheckUpdatesResponse", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class CheckUpdatesResponse {

    @XmlElement(name = "update", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected UpdateDTO update;
}//CheckUpdatesResponse
