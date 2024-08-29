
package mx.com.detisa.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerCfdiLayoutResponse", propOrder = {
    "pdf"
})
public class ObtenerCfdiLayoutResponse {

    @XmlElementRef(name = "pdf", type = JAXBElement.class, required = false)
    protected JAXBElement<byte[]> pdf;

    public JAXBElement<byte[]> getPdf() {
        return pdf;
    }

    public void setPdf(JAXBElement<byte[]> value) {
        this.pdf = value;
    }

}
