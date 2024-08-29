
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response", propOrder = {
    "error",
    "pdf",
    "uuid",
    "valid",
    "xml"
})
public class Response {

    protected String error;
    protected String pdf;
    protected String uuid;
    protected boolean valid;
    protected String xml;

    public String getError() {
        return error;
    }

    public void setError(String value) {
        this.error = value;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String value) {
        this.pdf = value;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String value) {
        this.uuid = value;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean value) {
        this.valid = value;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String value) {
        this.xml = value;
    }

}
