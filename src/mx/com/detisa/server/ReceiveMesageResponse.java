
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "receiveMesageResponse", propOrder = {
    "response"
})
public class ReceiveMesageResponse {

    protected Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response value) {
        this.response = value;
    }

}
