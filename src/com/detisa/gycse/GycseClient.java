/*
 * GycseClient
 * DPOS
 *  2016, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel VillafaÃ±a, Softcoatl
 * @version 1.0
 * @since may 2016
 */
package com.detisa.gycse;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.data.GycseEndpoints;
import com.ass2.volumetrico.puntoventa.data.GycseEndpoints.FIELDS;
import com.ass2.volumetrico.puntoventa.data.GycseEndpointsDAO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.PropertyLoader;
import com.softcoatl.utils.Sleeper;
import java.net.MalformedURLException;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsDatosConsultaRecarga;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsDatosPagoServicio;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsDatosRecarga;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsRespuesta;
import serviciosame.GYCSEIP2P;
import serviciosame.GYCSEInfoRouteadorClsDatosMaestros;
import serviciosame.RMIClientFactory;

public class GycseClient implements Sleeper {

    public static final String RMI_PROPERTIES = "samermi.properties";
    public static final String RMI_ENDPOINT = "same.endpoint";
    public static final String RMI_SERVICE = "same.service";
    public static final String RMI_PORT = "same.port";
    public static final String RMI_USR = "same.usr";
    public static final String RMI_LOG = "same.pdw";
    public static final String RMI_CLIENT = "same.client";

    public static final String GYCSE_SERVICIOS_SID_OPERACION = "162";
    public static final String GYCSE_SERVICIOS_SOCIO_COMERCIAL = "22";

    private static final serviciosame.ObjectFactory SAME_FACTORY = new serviciosame.ObjectFactory();
    private static final org.datacontract.schemas._2004._07.gycse_btservicios.ObjectFactory GYCSE_FACTORY = new org.datacontract.schemas._2004._07.gycse_btservicios.ObjectFactory();

    private final Properties rmiprop = PropertyLoader.load(RMI_PROPERTIES);

    private GYCSEIP2P gycsePort = null;
    @Getter private GycseEndpoints endpoint = null;

    public GycseClient() throws DetiPOSFault {
        endpoint = GycseEndpointsDAO.getEndpoint();
    }    
    private GYCSEIP2P getGycsePort() throws DetiPOSFault {
        try {
            LogManager.debug("Creando cliente gycse");
            if (gycsePort==null) {
                gycsePort = RMIClientFactory.getGycsePort(
                        endpoint.NVL(FIELDS.endpoint.name()),
                        endpoint.NVL(FIELDS.service.name()),
                        endpoint.NVL(FIELDS.portname.name()),
                        DateUtils.MILIS_POR_MINUTO * 1);
            }
        } catch (MalformedURLException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
        return gycsePort;
    }//getGycsePort

    public GYCSEInfoRouteadorClsRespuesta invokeConsulta(
            String codigoOperador,
            String idTransaccion,
            XMLGregorianCalendar fechaTransaccion) throws DetiPOSFault {
        GYCSEInfoRouteadorClsDatosConsultaRecarga datosRecarga = new GYCSEInfoRouteadorClsDatosConsultaRecarga();
        GYCSEInfoRouteadorClsDatosMaestros datos = SAME_FACTORY.createGYCSEInfoRouteadorClsDatosMaestros();
        int i = 0;

        // SAME Services Detisa Client ID
        datos.setINumCliente(Integer.valueOf(rmiprop.getProperty(RMI_CLIENT)));
        // Local transaction ID
        datos.setSIdPeticion(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSIdPeticion(idTransaccion));
        // SID 01 for "Recarga de tiempo aire" operations
        datos.setSidOperacion(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSidOperacion("84"));
        // Commercial partner
        datos.setSSocioComercial(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSSocioComercial(codigoOperador));

        datosRecarga.setSIdCliente(
                GYCSE_FACTORY.createGYCSEInfoRouteadorClsDatosConsultaRecargaSIdCliente(rmiprop.getProperty(RMI_CLIENT)));
        datosRecarga.setSTransNumberCliente(
                GYCSE_FACTORY.createGYCSEInfoRouteadorClsDatosConsultaRecargaSTransNumberCliente(idTransaccion));
        datosRecarga.setDtFechaTransaccion(fechaTransaccion);
        datos.setDatosConsultaRecarga(
                SAME_FACTORY.createGYCSEInfoRouteadorClsDatosMaestrosDatosConsultaRecarga(datosRecarga));

        LogManager.info("Starting TA Request");

        GYCSEInfoRouteadorClsRespuesta respuesta = null;
        do {
            try {
                respuesta = getGycsePort().consultaRecarga(datos);
                if (!respuesta.getClsResConsultaRecarga().getValue().getSCodigoRespuesta().getValue().equals("99")) break;
            } catch (DetiPOSFault exc) {
                if (!exc.getMessage().contains("SocketTimeoutException")
                        && !exc.getMessage().contains("ConnectException")) {
                    throw exc;
                }
            }
            sleep();
        } while (i++<12);

        return respuesta;
    }//invokeConsulta

    public GYCSEInfoRouteadorClsRespuesta invokeServicio(
            String codigoOperador,
            String servicio,
            String producto,
            String idTransaccion,
            String captura,
            int importe) throws DetiPOSFault {
        String token = getGycsePort().clsCreaToken(rmiprop.getProperty(RMI_USR), rmiprop.getProperty(RMI_LOG));
        GYCSEInfoRouteadorClsDatosPagoServicio datosServicio = newDatosServicio(servicio, producto, captura, importe);
        GYCSEInfoRouteadorClsDatosMaestros datos = SAME_FACTORY.createGYCSEInfoRouteadorClsDatosMaestros();

        try {
            XMLGregorianCalendar fechaSolicitudXML= javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("GMT-5")));
            datos.setDtFechaSolicitud(fechaSolicitudXML);
        } catch (DatatypeConfigurationException ex) {
            OmicromLogManager.error(ex);
        }

        // SAME Services Detisa Client ID
        datos.setINumCliente(Integer.valueOf(rmiprop.getProperty(RMI_CLIENT)));
        // Local transaction ID
        datos.setSIdPeticion(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSIdPeticion(idTransaccion));
        // SID 01 for "Recarga de tiempo aire" operations
        datos.setSidOperacion(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSidOperacion(GYCSE_SERVICIOS_SID_OPERACION));
        // Commercial partner
        datos.setSSocioComercial(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSSocioComercial(GYCSE_SERVICIOS_SOCIO_COMERCIAL));

        datos.setDatosPServicios(SAME_FACTORY.
                createGYCSEInfoRouteadorClsDatosMaestrosDatosPServicios(datosServicio));

        LogManager.info("Starting PS Request");

        GYCSEInfoRouteadorClsRespuesta respuesta = null;
        try {
            respuesta = getGycsePort().operacion(datos, token);
            if ("99".equals(respuesta.getClsResRecarga().getValue().getSCodigoRespuesta().getValue())) {
                respuesta = invokeConsulta(codigoOperador, idTransaccion, datos.getDtFechaSolicitud());
            }
        } catch (DetiPOSFault ste) {
            OmicromLogManager.error(ste);
            if (ste.getMessage().contains("SocketTimeoutException")
                    || ste.getMessage().contains("ConnectException")) {
                respuesta = invokeConsulta(codigoOperador, idTransaccion, datos.getDtFechaSolicitud());
            } else {
                throw ste;
            }
        }

        LogManager.info("Ending PS Request");
        return respuesta;
    }//invokeServicio

    public GYCSEInfoRouteadorClsRespuesta invokeRecarga(
            String codigoOperador, 
            String idTransaccion, 
            String numero, 
            int importe) throws DetiPOSFault {
        String token = getGycsePort().clsCreaToken(rmiprop.getProperty(RMI_USR), rmiprop.getProperty(RMI_LOG));
        GYCSEInfoRouteadorClsDatosRecarga datosRecarga = newDatosRecarga(numero, importe);
        GYCSEInfoRouteadorClsDatosMaestros datos = SAME_FACTORY.createGYCSEInfoRouteadorClsDatosMaestros();

        try {
            XMLGregorianCalendar fechaSolicitudXML= javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("GMT-5")));
            datos.setDtFechaSolicitud(fechaSolicitudXML);
        } catch (DatatypeConfigurationException ex) {
            OmicromLogManager.error(ex);
        }

        // SAME Services Detisa Client ID
        datos.setINumCliente(Integer.valueOf(rmiprop.getProperty(RMI_CLIENT)));
        // Local transaction ID
        datos.setSIdPeticion(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSIdPeticion(idTransaccion));
        // SID 01 for "Recarga de tiempo aire" operations
        datos.setSidOperacion(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSidOperacion("01"));
        // Commercial partner
        datos.setSSocioComercial(SAME_FACTORY
                .createGYCSEInfoRouteadorClsDatosMaestrosSSocioComercial(codigoOperador));
        datos.setDatosRecarga(SAME_FACTORY.
                createGYCSEInfoRouteadorClsDatosMaestrosDatosRecarga(datosRecarga));

        LogManager.info("Starting TA Request");

        GYCSEInfoRouteadorClsRespuesta respuesta = null;
        try {
            respuesta = getGycsePort().operacion(datos, token);
            if ("99".equals(respuesta.getClsResRecarga().getValue().getSCodigoRespuesta().getValue())) {
                respuesta = invokeConsulta(codigoOperador, idTransaccion, datos.getDtFechaSolicitud());
            }
        } catch (DetiPOSFault ste) {
            OmicromLogManager.error(ste);
            if (ste.getMessage().contains("SocketTimeoutException")
                    || ste.getMessage().contains("ConnectException")) {
                respuesta = invokeConsulta(codigoOperador, idTransaccion, datos.getDtFechaSolicitud());            
            } else {
                throw ste;
            }
        }

        LogManager.info("Ending TA Request");
        return respuesta;
    }//invokeRecarga

    private static GYCSEInfoRouteadorClsDatosRecarga newDatosRecarga(String numero, int importe) {
            GYCSEInfoRouteadorClsDatosRecarga datosRecarga = new GYCSEInfoRouteadorClsDatosRecarga();

            datosRecarga.setIMonto1(importe);
            datosRecarga.setSDNB(GYCSE_FACTORY.createGYCSEInfoRouteadorClsDatosRecargaSDNB(numero));

            return datosRecarga;
    }

    private static GYCSEInfoRouteadorClsDatosPagoServicio newDatosServicio(String servicio, String producto, String captura, int importe) {
            GYCSEInfoRouteadorClsDatosPagoServicio datosServicio = new GYCSEInfoRouteadorClsDatosPagoServicio();

            datosServicio.setDMonto((double) importe);
            datosServicio.setSIdProducto(GYCSE_FACTORY.createGYCSEInfoRouteadorClsDatosPagoServicioSIdProducto(producto));
            datosServicio.setSIdServicio(GYCSE_FACTORY.createGYCSEInfoRouteadorClsDatosPagoServicioSIdServicio(servicio));
            datosServicio.setSLineaCaptura(GYCSE_FACTORY.createGYCSEInfoRouteadorClsDatosPagoServicioSLineaCaptura(captura));

            return datosServicio;
    }

    @Override
    public void sleep() {
        try { Thread.sleep(DateUtils.MILIS_POR_SEGUNDO * 15); } catch (InterruptedException ex) {
            LogManager.error(ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Sleeper setTimeout(int timeout) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}//GycseClient
