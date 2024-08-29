package com.detisa.fae;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validaConsumo", propOrder = {
    "idfae",
    "codigo",
    "nip",
    "montoSolicitado",
    "litrosSolicitado",
    "claveProducto"
})
@Data
public class ValidaConsumo {

    protected int idfae;
    protected String codigo;
    protected String nip;
    protected double montoSolicitado;
    protected double litrosSolicitado;
    protected String claveProducto;
}
