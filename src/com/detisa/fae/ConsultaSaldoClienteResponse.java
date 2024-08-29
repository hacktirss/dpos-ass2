package com.detisa.fae;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaSaldoClienteResponse", propOrder = {
    "_return"
})
@Data
public class ConsultaSaldoClienteResponse {

    @XmlElement(name = "return")
    protected String _return;
}
