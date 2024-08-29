
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizaVentaResponse", propOrder = {
    "_return"
})
@Data
public class AutorizaVentaResponse {

    @XmlElement(name = "return")
    protected CheckBalance _return;
}
