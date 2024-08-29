
package com.detisa.omicrom.integraciones.monederos.puntovales;

import com.ass2.volumetrico.puntoventa.data.EndpointDAO;
import com.ass2.volumetrico.puntoventa.data.EndpointVO;
import com.detisa.omicrom.puntogas.vales.api.PuntoGasService;
import com.detisa.omicrom.puntogas.vales.api.PuntoGasValesException;
import com.detisa.omicrom.puntogas.vales.aprobacion.AprobacionVales;
import com.detisa.omicrom.puntogas.vales.aprobacion.AprobacionValesResponse;
import com.detisa.omicrom.puntogas.vales.autenticacion.Autenticacion;
import com.detisa.omicrom.puntogas.vales.autenticacion.AutenticacionResponse;
import com.detisa.omicrom.puntogas.vales.consulta.ConsultaVales;
import com.detisa.omicrom.puntogas.vales.consulta.ConsultaValesResponse;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Getter;

public class PuntoGasValesApi {

    private static final String NIP = "0000";

    private EndpointVO endpoint;
    @Getter private final String wallet;

    public PuntoGasValesApi(String wallet) throws DetiPOSFault {
        this.wallet = wallet;
        configure();
    }
    
    private void configure() throws DetiPOSFault {
        endpoint = EndpointDAO.get(wallet, "WALLET");
    }

    public AutenticacionResponse authenticate() throws NoSuchAlgorithmException, PuntoGasValesException {

        PuntoGasService service = new PuntoGasService(endpoint.getUrl_webservice());
        Autenticacion auth = new Autenticacion();
        auth.setUser(endpoint.getUsuario());
        auth.setPassword(endpoint.getPassword());
        auth.setTerminal(endpoint.getClave_aux());

        LogManager.info("PuntoGas Vales Autenticacion Request " + auth);
        AutenticacionResponse authResponse = service.autenticacion(auth);
        LogManager.info("PuntoGas Vales Autenticacion " + authResponse);

        return authResponse;
    }

    public ConsultaValesResponse request(
            String token,
            List<String> vales,
            String folioSeguimiento, 
//            String nip,
            int marcarEnUso) throws NoSuchAlgorithmException, PuntoGasValesException {

        PuntoGasService service = new PuntoGasService(endpoint.getUrl_webservice());

        ConsultaVales consulta = new ConsultaVales();
        consulta.setEstacion(endpoint.getUsuario().replaceAll("[^0-9]", ""));
        consulta.setFolioSegumiento(folioSeguimiento);
        consulta.setVales(vales);
        consulta.setNip(NIP);
        consulta.setTerminal(endpoint.getClave_aux());
        consulta.setMarcarEnUso(marcarEnUso);

        LogManager.info("PuntoGas Vales Request " + consulta);
        ConsultaValesResponse response = service.consulta(consulta, token);
        LogManager.info("PuntoGas Vales " + response);

        return response;
    }

    public AprobacionValesResponse approve(
            String token,
            List<String> vales,
//            String nip, 
            int posicion,
            String ticket,
            Calendar fecha,
            BigDecimal cantidad,
            BigDecimal precio,
            BigDecimal total,
            BigDecimal iva,
            BigDecimal ieps,
            String combustible) throws NoSuchAlgorithmException, PuntoGasValesException {

        String ticketNumber = DateUtils.fncsFormat("yyDDD", fecha) + StringUtils.fncsLeftPadding(ticket, '0', 12);

        PuntoGasService service = new PuntoGasService(endpoint.getUrl_webservice());
        AprobacionVales aprobacion = new AprobacionVales();
        aprobacion.setVales(vales);
        aprobacion.setEstacion(endpoint.getUsuario().replaceAll("[^0-9]", ""));
        aprobacion.setTerminal(endpoint.getClave_aux());
        aprobacion.setNip(NIP);
        aprobacion.setNoBomba(posicion);
        aprobacion.setMonto(total);
        aprobacion.setClaveCombustible(combustible);
        aprobacion.setProductoPrecioUnitario(precio);
        aprobacion.setTicketVolumetrico("#" + ticketNumber);
        aprobacion.setLitros(cantidad);
        aprobacion.setTipoTerminal(4);
        aprobacion.setFechaVenta(fecha);
        aprobacion.setHoraVenta(fecha);
        aprobacion.setTipoOperacion(4);
        aprobacion.setIva(iva);
        aprobacion.setIeps(cantidad.multiply(ieps).setScale(2, RoundingMode.HALF_EVEN));
        aprobacion.setIepsPorcentaje(ieps);
        aprobacion.setSubtotal(cantidad.multiply(precio).setScale(2, RoundingMode.HALF_EVEN));
        aprobacion.setIvaCalcular(1);

        LogManager.info("PuntoGas Vales Approve Request " + aprobacion);
        AprobacionValesResponse order = service.aprobacion(aprobacion, token);
        LogManager.info("PuntoGas Vales Approve Card " + order);

        return order;
    }
    
    public static void main(String[] args) throws PuntoGasValesException {
        PuntoGasService service = new PuntoGasService("http://52.116.149.99:6015");

        Autenticacion auth = new Autenticacion();
        auth.setUser("ES06832");
        auth.setPassword("!83^RM{EI=V|L}ebremW");
        auth.setTerminal("1111111016");
        LogManager.info("PuntoGas Vales Autenticacion Request " + auth);
        AutenticacionResponse authResponse = service.autenticacion(auth);
        LogManager.info("PuntoGas Vales Autenticacion " + authResponse);
        
        List<String> vales = new ArrayList<>();
        vales.add("CAM PUNTOVALE 8535197828");
        vales.add("CAM PUNTOVALE 3936197826");

        ConsultaVales consulta = new ConsultaVales();
        consulta.setEstacion("06832");
        consulta.setVales(vales);
        consulta.setTerminal("1111111016");
        consulta.setNip("0000");

        LogManager.info("PuntoGas Vales Request " + consulta);
        ConsultaValesResponse response = service.consulta(consulta, authResponse.getToken());
        LogManager.info("PuntoGas Vales " + response);
    }
}
