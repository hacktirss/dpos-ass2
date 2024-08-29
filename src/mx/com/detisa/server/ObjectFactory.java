
package mx.com.detisa.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private static final String NAMESPACE = "http://server.detisa.mx.com/";

    private static final QName _ObtenerCfdiLayout_QNAME = new QName(ObjectFactory.NAMESPACE, "obtenerCfdiLayout");
    private static final QName _Exception_QNAME = new QName(ObjectFactory.NAMESPACE, "Exception");
    private static final QName _ObtenerCfdiLayoutResponse_QNAME = new QName(ObjectFactory.NAMESPACE, "obtenerCfdiLayoutResponse");
    private static final QName _AutorizaVentaResponse_QNAME = new QName(ObjectFactory.NAMESPACE, "autorizaVentaResponse");
    private static final QName _ConfirmaVentaResponse_QNAME = new QName(ObjectFactory.NAMESPACE, "confirmaVentaResponse");
    private static final QName _ReceiveJSONMesageResponse_QNAME = new QName(ObjectFactory.NAMESPACE, "receiveJSONMesageResponse");
    private static final QName _ReceiveMesageResponse_QNAME = new QName(ObjectFactory.NAMESPACE, "receiveMesageResponse");
    private static final QName _AutorizaVenta_QNAME = new QName(ObjectFactory.NAMESPACE, "autorizaVenta");
    private static final QName _ReceiveMesage_QNAME = new QName(ObjectFactory.NAMESPACE, "receiveMesage");
    private static final QName _ConfirmaVenta_QNAME = new QName(ObjectFactory.NAMESPACE, "confirmaVenta");

    private static final QName _ReceiveJSONMesage_QNAME = new QName(ObjectFactory.NAMESPACE, "receiveJSONMesage");
    private static final QName _ObtenerCfdiLayoutResponsePdf_QNAME = new QName("", "pdf");
    private static final QName _ObtenerCfdiLayoutArg4_QNAME = new QName("", "arg4");
    private static final QName _ObtenerCfdiLayoutLlave_QNAME = new QName("", "llave");
    private static final QName _ObtenerCfdiLayoutText_QNAME = new QName("", "text");
    private static final QName _ObtenerCfdiLayoutArg6_QNAME = new QName("", "arg6");

    public ConfirmaVenta createConfirmaVenta() {
        return new ConfirmaVenta();
    }

    public ReceiveJSONMesage createReceiveJSONMesage() {
        return new ReceiveJSONMesage();
    }

    public AutorizaVenta createAutorizaVenta() {
        return new AutorizaVenta();
    }

    public ReceiveMesage createReceiveMesage() {
        return new ReceiveMesage();
    }

    public ReceiveJSONMesageResponse createReceiveJSONMesageResponse() {
        return new ReceiveJSONMesageResponse();
    }

    public ReceiveMesageResponse createReceiveMesageResponse() {
        return new ReceiveMesageResponse();
    }

    public ObtenUnidad createObtenUnidad() {
        return new ObtenUnidad();
    }
    
    public ObtenUnidadResponse createObtenUnidadResponse() {
        return new ObtenUnidadResponse();
    }

    public ConfirmaVentaResponse createConfirmaVentaResponse() {
        return new ConfirmaVentaResponse();
    }

    public AutorizaVentaResponse createAutorizaVentaResponse() {
        return new AutorizaVentaResponse();
    }

    public ObtenerCfdiLayoutResponse createObtenerCfdiLayoutResponse() {
        return new ObtenerCfdiLayoutResponse();
    }

    public Exception createException() {
        return new Exception();
    }

    public ObtenerCfdiLayout createObtenerCfdiLayout() {
        return new ObtenerCfdiLayout();
    }

    public Response createResponse() {
        return new Response();
    }

    public CheckBalance createCheckBalance() {
        return new CheckBalance();
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "obtenerCfdiLayout")
    public JAXBElement<ObtenerCfdiLayout> createObtenerCfdiLayout(ObtenerCfdiLayout value) {
        return new JAXBElement <>(_ObtenerCfdiLayout_QNAME, ObtenerCfdiLayout.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<>(_Exception_QNAME, Exception.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "obtenerCfdiLayoutResponse")
    public JAXBElement<ObtenerCfdiLayoutResponse> createObtenerCfdiLayoutResponse(ObtenerCfdiLayoutResponse value) {
        return new JAXBElement<>(_ObtenerCfdiLayoutResponse_QNAME, ObtenerCfdiLayoutResponse.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "autorizaVentaResponse")
    public JAXBElement<AutorizaVentaResponse> createAutorizaVentaResponse(AutorizaVentaResponse value) {
        return new JAXBElement<>(_AutorizaVentaResponse_QNAME, AutorizaVentaResponse.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "confirmaVentaResponse")
    public JAXBElement<ConfirmaVentaResponse> createConfirmaVentaResponse(ConfirmaVentaResponse value) {
        return new JAXBElement<>(_ConfirmaVentaResponse_QNAME, ConfirmaVentaResponse.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "receiveJSONMesageResponse")
    public JAXBElement<ReceiveJSONMesageResponse> createReceiveJSONMesageResponse(ReceiveJSONMesageResponse value) {
        return new JAXBElement<>(_ReceiveJSONMesageResponse_QNAME, ReceiveJSONMesageResponse.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "receiveMesageResponse")
    public JAXBElement<ReceiveMesageResponse> createReceiveMesageResponse(ReceiveMesageResponse value) {
        return new JAXBElement<>(_ReceiveMesageResponse_QNAME, ReceiveMesageResponse.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "autorizaVenta")
    public JAXBElement<AutorizaVenta> createAutorizaVenta(AutorizaVenta value) {
        return new JAXBElement<>(_AutorizaVenta_QNAME, AutorizaVenta.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "receiveMesage")
    public JAXBElement<ReceiveMesage> createReceiveMesage(ReceiveMesage value) {
        return new JAXBElement<>(_ReceiveMesage_QNAME, ReceiveMesage.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "confirmaVenta")
    public JAXBElement<ConfirmaVenta> createConfirmaVenta(ConfirmaVenta value) {
        return new JAXBElement<>(_ConfirmaVenta_QNAME, ConfirmaVenta.class, null, value);
    }

    @XmlElementDecl(namespace = ObjectFactory.NAMESPACE, name = "receiveJSONMesage")
    public JAXBElement<ReceiveJSONMesage> createReceiveJSONMesage(ReceiveJSONMesage value) {
        return new JAXBElement<>(_ReceiveJSONMesage_QNAME, ReceiveJSONMesage.class, null, value);
    }

    @XmlElementDecl(namespace = "", name = "pdf", scope = ObtenerCfdiLayoutResponse.class)
    public JAXBElement<byte[]> createObtenerCfdiLayoutResponsePdf(byte[] value) {
        return new JAXBElement<>(_ObtenerCfdiLayoutResponsePdf_QNAME, byte[].class, ObtenerCfdiLayoutResponse.class, ((byte[]) value));
    }

    @XmlElementDecl(namespace = "", name = "arg4", scope = ObtenerCfdiLayout.class)
    public JAXBElement<byte[]> createObtenerCfdiLayoutArg4(byte[] value) {
        return new JAXBElement<>(_ObtenerCfdiLayoutArg4_QNAME, byte[].class, ObtenerCfdiLayout.class, ((byte[]) value));
    }

    @XmlElementDecl(namespace = "", name = "llave", scope = ObtenerCfdiLayout.class)
    public JAXBElement<byte[]> createObtenerCfdiLayoutLlave(byte[] value) {
        return new JAXBElement<>(_ObtenerCfdiLayoutLlave_QNAME, byte[].class, ObtenerCfdiLayout.class, ((byte[]) value));
    }

    @XmlElementDecl(namespace = "", name = "text", scope = ObtenerCfdiLayout.class)
    public JAXBElement<byte[]> createObtenerCfdiLayoutText(byte[] value) {
        return new JAXBElement<>(_ObtenerCfdiLayoutText_QNAME, byte[].class, ObtenerCfdiLayout.class, ((byte[]) value));
    }

    @XmlElementDecl(namespace = "", name = "arg6", scope = ObtenerCfdiLayout.class)
    public JAXBElement<byte[]> createObtenerCfdiLayoutArg6(byte[] value) {
        return new JAXBElement<>(_ObtenerCfdiLayoutArg6_QNAME, byte[].class, ObtenerCfdiLayout.class, ((byte[]) value));
    }

}
