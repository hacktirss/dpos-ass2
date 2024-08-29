
package com.detisa.omicrom.integraciones.monederos.gasomatic;

import com.ass2.volumetrico.puntoventa.data.EndpointDAO;
import com.ass2.volumetrico.puntoventa.data.EndpointVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.mx.detisa.integrations.gasomatic.Autentificacion;
import com.mx.detisa.integrations.gasomatic.Autorizar;
import com.mx.detisa.integrations.gasomatic.Cifrado;
import com.mx.detisa.integrations.gasomatic.Combustibles;
import com.mx.detisa.integrations.gasomatic.Confirmar;
import com.mx.detisa.integrations.gasomatic.ObjectFactory;
import com.mx.detisa.integrations.gasomatic.RespuestaAutorizar;
import com.mx.detisa.integrations.gasomatic.RespuestaCifrado;
import com.mx.detisa.integrations.gasomatic.RespuestaConfirmar;
import com.mx.detisa.integrations.gasomatic.RespuestaSaldoTarjeta;
import com.mx.detisa.integrations.gasomatic.SaldoTarjeta;
import com.mx.detisa.integrations.gasomatic.TipoCarga;
import com.mx.detisa.integrations.gasomatic.WsSoap;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import lombok.Getter;

public class GasoMaticApi {

    private EndpointVO endpoint;
    private WsSoap service;
    private final ObjectFactory of = new ObjectFactory();
    @Getter private final String wallet;

    public GasoMaticApi(String wallet) throws DetiPOSFault {
        this.wallet = wallet;
        configure();
    }
    
    private void configure() throws DetiPOSFault {
        try {
            LogManager.info("Consultando monedero " + wallet);
            endpoint = EndpointDAO.get(wallet, "WALLET");
            service = GasoMaticFactory.getCardService(endpoint.getUrl_webservice());
        } catch (MalformedURLException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    private Autentificacion auth() throws UnknownHostException {

        Autentificacion auth = of.createAutentificacion();
        auth.setEmail(endpoint.getUsuario());
        auth.setToken(endpoint.getPassword());
        return auth;
    }

    public RespuestaCifrado cifra(String cardNumber, String pin) throws DetiPOSFault {

        try {
            Cifrado cifrado = of.createCifrado();
            cifrado.setTarjeta(cardNumber);
            cifrado.setNip(pin);
            RespuestaCifrado response = service.cifrarNip(auth(), cifrado);
            if ("00001".equals(response.getCodigo())) {
                return response;
            }
            throw new DetiPOSFault(response.getMsg());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public RespuestaSaldoTarjeta request(String cardNumber) throws DetiPOSFault {

        try {
            SaldoTarjeta saldo = of.createSaldoTarjeta();
            saldo.setTarjeta(cardNumber);
            RespuestaSaldoTarjeta response = service.consultarSaldo(auth(), saldo);
            if ("00001".equals(response.getCodigo())) {
                return response;
            }
            throw new DetiPOSFault(response.getMsg());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }
    
    public RespuestaAutorizar authorize(
            String cardNumber,
            String nip, 
            TipoCarga tipo,
            Combustibles combustible,
            int posicion,
            BigDecimal importe,
            BigDecimal precio,
            BigDecimal ieps,
            String placa,
            int odometro) throws DetiPOSFault {

        try {
            Autorizar auth = of.createAutorizar();
            auth.setBomba(posicion);
            auth.setCombustible(combustible);
            auth.setDispositivo(cardNumber);
            auth.setFechahora(DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
            auth.setTipo(tipo);
            auth.setImporte(importe);
            auth.setIeps(ieps);
            auth.setKilometraje(odometro);
            auth.setNip(nip);
            auth.setPlaca(placa);
            auth.setPrecio(precio);

            RespuestaAutorizar response = service.autorizarTransaccion(auth(), auth);
            if ("00001".equals(response.getCodigo())) {
                return response;
            }
            throw new DetiPOSFault(response.getMsg());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public RespuestaConfirmar confirm(
            String cardNumber,
            String nip, 
            TipoCarga tipo,
            Combustibles combustible,
            int posicion,
            BigDecimal importe,
            String oper,
            String placa,
            int odometro) throws DetiPOSFault {

        try {
            Confirmar conf = of.createConfirmar();
            conf.setBomba(posicion);
            conf.setCombustible(combustible);
            conf.setDispositivo(cardNumber);
            conf.setFechahora(DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
            conf.setTipo(tipo);
            conf.setImporte(importe);
            conf.setKilometraje(odometro);
            conf.setOpeRef(Integer.parseInt(oper));
            conf.setNip(nip);
            conf.setPlaca(placa);

            RespuestaConfirmar response = service.confirmarTransaccion(auth(), conf);
            if ("00001".equals(response.getCodigo())) {
                return response;
            }
            throw new DetiPOSFault(response.getMsg());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public RespuestaConfirmar cancel(
            String cardNumber,
            String nip, 
            TipoCarga tipo,
            Combustibles combustible,
            int posicion,
            String oper,
            String placa,
            int odometro) throws DetiPOSFault {

        try {
            Confirmar conf = of.createConfirmar();
            conf.setBomba(posicion);
            conf.setCombustible(combustible);
            conf.setDispositivo(cardNumber);
            conf.setFechahora(DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
            conf.setTipo(tipo);
            conf.setImporte(BigDecimal.ZERO);
            conf.setKilometraje(odometro);
            conf.setOpeRef(Integer.parseInt(oper));
            conf.setNip(nip);
            conf.setPlaca(placa);

            RespuestaConfirmar response = service.confirmarTransaccion(auth(), conf);
            if ("10139".equals(response.getCodigo())) {
                return response;
            }
            throw new DetiPOSFault(response.getMsg());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public static void main(String[] args) throws MalformedURLException {
            System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("java.net.useSystemProxies", "false");
            System.setProperty("com.sun.xml.internal.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");
            System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");
        WsSoap s = GasoMaticFactory.getCardService("https://www.gasomatic.com.mx/wsCatLodemoV2/?wsdl");
        ObjectFactory of = new ObjectFactory();

        Autentificacion autentificacion = of.createAutentificacion();
        autentificacion.setEmail("monederos@gruges.com.mx");
        autentificacion.setToken("BE84463A-A54C-4C84-AA27-DC594457F179");

        SaldoTarjeta saldo = of.createSaldoTarjeta();
        saldo.setTarjeta("6920000005818114");
        RespuestaSaldoTarjeta saldotarjeta = s.consultarSaldo(autentificacion, saldo);
        System.out.println(saldotarjeta);

        /*
        Cifrado cifrado = of.createCifrado();
        cifrado.setTarjeta("6920000005818114");
        cifrado.setNip("7491");
        RespuestaCifrado cifraNip = s.cifrarNip(autentificacion, cifrado);
        System.out.println(cifraNip);

        Autorizar auth = of.createAutorizar();
        auth.setBomba(1);
        auth.setCombustible(Combustibles.PREMIUM);
        auth.setDispositivo("6920000005818114");
        auth.setFechahora(DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
        auth.setTipo(TipoCarga.PRECIO);
        auth.setImporte(BigDecimal.ONE);
        auth.setIeps(new BigDecimal("0.591449"));
        auth.setKilometraje(1234);
        auth.setNip("7491");
        auth.setPlaca(saldotarjeta.getPlaca());
        auth.setPrecio(new BigDecimal("25.20"));

        RespuestaAutorizar autorizar = s.autorizarTransaccion(autentificacion, auth);
        System.out.println(autorizar);

*/
        Confirmar conf = of.createConfirmar();
        conf.setBomba(5);
        conf.setCombustible(Combustibles.PREMIUM);
        conf.setDispositivo("6920000005818114");
        conf.setFechahora(DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
        conf.setImporte(BigDecimal.ZERO);
        conf.setOpeRef(Integer.valueOf("94178397"));
        //conf.setOpeRef(Integer.valueOf(autorizar.getIdOperacion()));
        conf.setTipo(TipoCarga.PRECIO);
        conf.setImporte(BigDecimal.ZERO);
        conf.setKilometraje(1234);
        conf.setNip("7491");

        RespuestaConfirmar confirmacion = s.confirmarTransaccion(autentificacion, conf);
        System.out.println(confirmacion);
    }
}
