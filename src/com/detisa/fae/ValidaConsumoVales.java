package com.detisa.fae;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validaConsumoVales", propOrder = {
    "idfae",
    "codigo",
    "nip"
})
@Data
public class ValidaConsumoVales {

    protected int idfae;
    protected String codigo;
    protected String nip;
}
