
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "receiveMesage", propOrder = {
    "user",
    "password",
    "msj"
})
public class ReceiveMesage {

    protected String user;
    protected String password;
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

    public String getMsj() {
        return msj;
    }

    public void setMsj(String value) {
        this.msj = value;
    }

}
