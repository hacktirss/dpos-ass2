
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenUnidadResponse", propOrder = {
    "_return"
})
public class ObtenUnidadResponse {

    @XmlElement(name = "return")
    protected DatosClientePrepagoTO _return;

    public DatosClientePrepagoTO getReturn() {
        return _return;
    }

    public void setReturn(DatosClientePrepagoTO value) {
        this._return = value;
    }

}
