package com.detisa.fae;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkBalance", propOrder = {
    "balance",
    "idAutoriza",
    "intereses",
    "montoAutorizado",
    "saldoDisponible",
    "tipoMonto",
    "valid",
    "codigoVales"
})
@Data
public class CheckBalance {

    protected BigDecimal balance;
    protected String idAutoriza;
    protected String intereses;
    protected double montoAutorizado;
    protected double saldoDisponible;
    protected String tipoMonto;
    protected boolean valid;
    protected String codigoVales;
}
