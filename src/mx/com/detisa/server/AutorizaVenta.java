
package mx.com.detisa.server;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizaVenta", propOrder = {
    "arg0",
    "arg1",
    "arg2",
    "arg3",
    "arg4",
    "arg5",
    "arg6",
    "arg7",
    "arg8"
})
public class AutorizaVenta {

    protected String arg0;
    protected String arg1;
    protected BigDecimal arg2;
    protected BigDecimal arg3;
    protected String arg4;
    protected String arg5;
    protected String arg6;
    protected String arg7;
    protected String arg8;

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

    public BigDecimal getArg2() {
        return arg2;
    }

    public void setArg2(BigDecimal value) {
        this.arg2 = value;
    }

    public BigDecimal getArg3() {
        return arg3;
    }

    public void setArg3(BigDecimal value) {
        this.arg3 = value;
    }

    public String getArg4() {
        return arg4;
    }

    public void setArg4(String value) {
        this.arg4 = value;
    }

    public String getArg5() {
        return arg5;
    }

    public void setArg5(String value) {
        this.arg5 = value;
    }

    public String getArg6() {
        return arg6;
    }

    public void setArg6(String value) {
        this.arg6 = value;
    }

    public String getArg7() {
        return arg7;
    }

    public void setArg7(String value) {
        this.arg7 = value;
    }

    public String getArg8() {
        return arg8;
    }

    public void setArg8(String value) {
        this.arg8 = value;
    }
}
