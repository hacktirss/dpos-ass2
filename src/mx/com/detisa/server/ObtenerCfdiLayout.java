
package mx.com.detisa.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerCfdiLayout", propOrder = {
    "user",
    "password",
    "text",
    "llave",
    "arg4",
    "llaveCer",
    "arg6",
    "arg7"
})
public class ObtenerCfdiLayout {

    protected String user;
    protected String password;
    @XmlElementRef(name = "text", type = JAXBElement.class, required = false)
    protected JAXBElement<byte[]> text;
    @XmlElementRef(name = "llave", type = JAXBElement.class, required = false)
    protected JAXBElement<byte[]> llave;
    @XmlElementRef(name = "arg4", type = JAXBElement.class, required = false)
    protected JAXBElement<byte[]> arg4;
    protected String llaveCer;
    @XmlElementRef(name = "arg6", type = JAXBElement.class, required = false)
    protected JAXBElement<byte[]> arg6;
    protected String arg7;

    /**
     * Obtiene el valor de la propiedad user.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Define el valor de la propiedad user.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Obtiene el valor de la propiedad password.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define el valor de la propiedad password.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Obtiene el valor de la propiedad text.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getText() {
        return text;
    }

    /**
     * Define el valor de la propiedad text.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setText(JAXBElement<byte[]> value) {
        this.text = value;
    }

    /**
     * Obtiene el valor de la propiedad llave.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getLlave() {
        return llave;
    }

    /**
     * Define el valor de la propiedad llave.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setLlave(JAXBElement<byte[]> value) {
        this.llave = value;
    }

    /**
     * Obtiene el valor de la propiedad arg4.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getArg4() {
        return arg4;
    }

    /**
     * Define el valor de la propiedad arg4.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setArg4(JAXBElement<byte[]> value) {
        this.arg4 = value;
    }

    /**
     * Obtiene el valor de la propiedad llaveCer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLlaveCer() {
        return llaveCer;
    }

    /**
     * Define el valor de la propiedad llaveCer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLlaveCer(String value) {
        this.llaveCer = value;
    }

    /**
     * Obtiene el valor de la propiedad arg6.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getArg6() {
        return arg6;
    }

    /**
     * Define el valor de la propiedad arg6.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setArg6(JAXBElement<byte[]> value) {
        this.arg6 = value;
    }

    /**
     * Obtiene el valor de la propiedad arg7.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg7() {
        return arg7;
    }

    /**
     * Define el valor de la propiedad arg7.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg7(String value) {
        this.arg7 = value;
    }

}
