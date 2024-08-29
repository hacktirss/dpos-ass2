
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "receiveJSONMesage", propOrder = {
    "user",
    "password",
    "tipo",
    "msj"
})
public class ReceiveJSONMesage {

    protected String user;
    protected String password;
    protected String tipo;
    protected String msj;

    public String getUser() {
        return user;
    }

    public void setUser(String value) {
        this.user = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String value) {
        this.tipo = value;
    }

    public String getMsj() {
        return msj;
    }

    public void setMsj(String value) {
        this.msj = value;
    }

}
