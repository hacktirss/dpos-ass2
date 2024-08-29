
package mx.com.detisa.server;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkBalance", namespace = "http://detisa.com.mx/cfd/2", propOrder = {
    "balance",
    "idAutoriza",
    "importeAutorizado",
    "intereses",
    "valid"
})
public class CheckBalance {

    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    protected BigDecimal balance;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    protected String idAutoriza;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    protected BigDecimal importeAutorizado;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    protected String intereses;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    protected boolean valid;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal value) {
        this.balance = value;
    }

    public String getIdAutoriza() {
        return idAutoriza;
    }

    public void setIdAutoriza(String value) {
        this.idAutoriza = value;
    }

    public BigDecimal getImporteAutorizado() {
        return importeAutorizado;
    }

    public void setImporteAutorizado(BigDecimal value) {
        this.importeAutorizado = value;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String value) {
        this.intereses = value;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean value) {
        this.valid = value;
    }

    @Override
    public String toString() {
        return "Balance del cliente : " + balance.toPlainString() +
            "ID de la autorizaciÃ³n : " + idAutoriza +
            "Importe autorizado : " + importeAutorizado +
            "Intereses : " + intereses +
            "Valid ? " + valid;
    }
}
