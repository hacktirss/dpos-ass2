package com.detisa.fae;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaSaldoCliente", propOrder = {
    "idfae",
    "idcliente"
})
@Data
public class ConsultaSaldoCliente {

    protected String idfae;
    protected String idcliente;
}
