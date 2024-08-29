package com.ass2.volumetrico.puntoventa.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idIntegracion",
    "idTerminal",
    "tipo",
    "cantidad",
    "posicion",
    "manguera",
    "idTarjeta",
    "claveBanco",
    "formaPago",
    "despachador",
    "passwordDespachador",
    "password",
    "odometro"
})
@XmlRootElement(name = "ConsumoIntegracionRequest", namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
@Data
public class ConsumoIntegracionRequest {

    @XmlElement(name = "idIntegracion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idIntegracion;

    @XmlElement(name = "idTerminal", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTerminal;

    @XmlElement(name = "tipo", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String tipo;

    @XmlElement(name = "cantidad", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String cantidad;

    @XmlElement(name = "posicion", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String posicion;

    @XmlElement(name = "manguera", required = true, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String manguera;

    @XmlElement(name = "idTarjeta", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String idTarjeta;

    @XmlElement(name = "claveBanco", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String claveBanco;

    @XmlElement(name = "formaPago", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String formaPago;

    @XmlElement(name = "despachador", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String despachador;

    @XmlElement(name = "passwordDespachador", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String passwordDespachador;

    @XmlElement(name = "password", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String password;

    @XmlElement(name = "odometro", required = false, namespace = DetiPOSPort.DPOS_WS_NAMESPACE)
    protected String odometro;
}//ConsumoRequest
