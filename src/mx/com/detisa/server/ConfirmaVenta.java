
package mx.com.detisa.server;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmaVenta", propOrder = {
    "arg0",
    "arg1",
    "arg2",
    "arg3",
    "arg4",
    "arg5"
})
public class ConfirmaVenta {

    protected String arg0;
    protected String arg1;
    protected String arg2;
    protected BigDecimal arg3;
    protected BigDecimal arg4;
    protected String arg5;

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String value) {
        this.arg0 = value;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String value) {
        this.arg1 = value;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String value) {
        this.arg2 = value;
    }

    public BigDecimal getArg3() {
        return arg3;
    }

    public void setArg3(BigDecimal value) {
        this.arg3 = value;
    }

    public BigDecimal getArg4() {
        return arg4;
    }

    public void setArg4(BigDecimal value) {
        this.arg4 = value;
    }

    public String getArg5() {
        return arg5;
    }

    public void setArg5(String value) {
        this.arg5 = value;
    }
}
