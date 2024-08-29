
package mx.com.detisa.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datosClientePrepagoTO", namespace = "http://detisa.com.mx/cfd/2", propOrder = {
    "allowed",
    "cliente",
    "codigo",
    "combustible",
    "concepto",
    "descripcion",
    "dia",
    "estado",
    "horaf",
    "horai",
    "id",
    "idEstacion",
    "importe",
    "impreso",
    "intereses",
    "litros",
    "nip",
    "pidenip",
    "placas",
    "producto",
    "referencia",
    "referencia2"
})
public class DatosClientePrepagoTO {

    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String allowed;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String cliente;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String codigo;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String combustible;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String concepto;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String descripcion;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String dia;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String estado;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String horaf;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String horai;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String id;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String idEstacion;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String importe;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String impreso;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String intereses;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String litros;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String nip;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String pidenip;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String placas;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String producto;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String referencia;
    @XmlElement(namespace = "http://detisa.com.mx/cfd/2")
    private String referencia2;

    public String getAllowed() {
        return allowed;
    }

    public void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHoraf() {
        return horaf;
    }

    public void setHoraf(String horaf) {
        this.horaf = horaf;
    }

    public String getHorai() {
        return horai;
    }

    public void setHorai(String horai) {
        this.horai = horai;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(String idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getImpreso() {
        return impreso;
    }

    public void setImpreso(String impreso) {
        this.impreso = impreso;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public String getLitros() {
        return litros;
    }

    public void setLitros(String litros) {
        this.litros = litros;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getPidenip() {
        return pidenip;
    }

    public void setPidenip(String pidenip) {
        this.pidenip = pidenip;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getReferencia2() {
        return referencia2;
    }

    public void setReferencia2(String referencia2) {
        this.referencia2 = referencia2;
    }

    @Override
    public String toString() {
        return "allowed" + allowed
                + "cliente" + cliente
                + "codigo" + codigo
                + "combustible" + combustible
                + "concepto" + concepto
                + "descripcion" + descripcion
                + "dia" + dia
                + "estado" + estado
                + "horaf" + horaf
                + "horai" + horai
                + "id" + id
                + "idEstacion" + idEstacion
                + "importe" + importe
                + "impreso" + impreso
                + "intereses" + intereses
                + "litros" + litros
                + "nip" + nip
                + "pidenip" + pidenip
                + "placas" + placas
                + "producto" + producto
                + "referencia" + referencia
                + "referencia2" + referencia2;

    }
}
