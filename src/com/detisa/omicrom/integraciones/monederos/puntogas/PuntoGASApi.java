
package com.detisa.omicrom.integraciones.monederos.puntogas;

import com.detisa.integrations.puntogas.AprobacionParameter;
import com.detisa.integrations.puntogas.AprobacionReturn;
import com.detisa.integrations.puntogas.AutenticateReturn;
import com.detisa.integrations.puntogas.Ecombutibles;
import com.detisa.integrations.puntogas.ModelAutenticate;
import com.detisa.integrations.puntogas.ObjectFactory;
import com.detisa.integrations.puntogas.PuntoGasWSSoap;
import com.detisa.integrations.puntogas.SaldoParameter;
import com.detisa.integrations.puntogas.SaldoRetun;
import com.ass2.volumetrico.puntoventa.data.EndpointDAO;
import com.ass2.volumetrico.puntoventa.data.EndpointVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import lombok.Getter;

public class PuntoGASApi {

    private EndpointVO endpoint;
    private PuntoGasWSSoap service;
    private final ObjectFactory of = new ObjectFactory();
    @Getter private final String wallet;

    public PuntoGASApi(String wallet) throws DetiPOSFault {
        this.wallet = wallet;
        configure();
    }
    
    private void configure() throws DetiPOSFault {
        try {
            LogManager.info("Consultando monedero " + wallet);
            endpoint = EndpointDAO.get(wallet, "WALLET");
            service = PuntoGASFactory.getCardService(endpoint.getUrl_webservice());
        } catch (MalformedURLException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    private AutenticateReturn login() throws UnknownHostException {

        ModelAutenticate auth = of.createModelAutenticate();

        auth.setUser(endpoint.getUsuario());
        auth.setContraseña(endpoint.getPassword());
        auth.setIp(InetAddress.getLocalHost().getHostAddress());
        auth.setTerminal(endpoint.getClave_aux());

        LogManager.info("PuntoGAS Login Request " + auth);
        AutenticateReturn authRes = service.sessionOn(auth);
        LogManager.info("PuntoGAS Login " + authRes);

        return authRes;
    }

    public AprobacionReturn confirm(
            String cardNumber, 
            String cardKey, 
            String folioVenta,
            String posicion,
            String combustible,
            String importe,
            String volumen,
            String odometro,
            boolean isImporte) throws DetiPOSFault {

        try {
            AutenticateReturn auth = login();
            if (auth.getExito()==0) {

                AprobacionParameter request = of.createAprobacionParameter();

                request.setToken(auth.getToken());
                request.setIdVolumetrico(folioVenta);
                request.setPosLitros(Double.parseDouble(volumen));
                request.setPosMonto(importe);
                request.setTIPOPRESET(isImporte ? "P".toCharArray()[0] : "L".toCharArray()[0]);
                request.setPosTarjeta(cardNumber);
                request.setNip(cardKey);
                request.setPosEstacion(endpoint.getUsuario().replaceAll("ES", ""));
                request.setFOLIO(endpoint.getClave_aux());
                request.setPosCombustible(Ecombutibles.fromValue(combustible));
                request.setPosTerminal(endpoint.getClave_aux());
                request.setTipoTerminal(1);
                request.setPosOdometro(new BigDecimal(odometro).doubleValue());
                request.setPosBomba(Integer.parseInt(posicion));

                LogManager.info("PuntoGAS Confirmacion Request " + request);
                AprobacionReturn confirmacion = service.aprobacion(request);
                LogManager.info("PuntoGAS Confirmacion " + confirmacion);
                
                if (confirmacion.getExitoso()==0) {
                    return confirmacion;
                }
                throw new DetiPOSFault("Code " + confirmacion.getExitoso() + "::" + confirmacion.getMensaje());
            }
            throw new DetiPOSFault(auth.getMensaje());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public SaldoRetun authorize(
            String cardNumber, 
            String cardKey, 
            String posicion,
            String importe,
            String estacion, 
            String odometro,
            boolean isImporte) throws DetiPOSFault {

        try {
            AutenticateReturn auth = login();
            if (auth.getExito()==0) {

                SaldoParameter request = of.createSaldoParameter();

                request.setToken(auth.getToken());
                request.setEstacionPos(estacion);
                request.setTerminalPos(endpoint.getClave_aux());
                request.setTarjetaPos(cardNumber);
                request.setNip(cardKey);
                request.setOdometro(odometro);
                request.setBomba(posicion);
                request.setMonto(importe);
                request.setTipoPreset(isImporte ? "S" : "L");

                LogManager.info("PuntoGAS Autorizacion Request " + request);
                SaldoRetun response = service.saldoKM(request);
                LogManager.info("PuntoGAS Autorizacion " + response);

                if (response.getExitoso()==0) {
                    return response;
                }
                throw new DetiPOSFault("Code " + response.getExitoso() + "::" + response.getMensaje());
            }
            throw new DetiPOSFault(auth.getMensaje());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public SaldoRetun requestKM(
            String cardNumber, 
            String cardKey, 
            String odometro,
            String estacion) throws DetiPOSFault {

        try {
            AutenticateReturn auth = login();
            if (auth.getExito()==0) {

                SaldoParameter request = of.createSaldoParameter();

                request.setToken(auth.getToken());
                request.setEstacionPos(estacion);
                request.setTerminalPos(endpoint.getClave_aux());
                request.setOdometro(odometro);
                request.setTarjetaPos(cardNumber);
                request.setNip(cardKey);

                LogManager.info("PuntoGAS Saldo Request " + request);
                SaldoRetun response = service.saldoKM(request);
                LogManager.info("PuntoGAS Saldo " + response);

                if (response.getExitoso()==0) {
                    return response;
                }
                throw new DetiPOSFault("Code " + response.getExitoso() + "::" + response.getMensaje());
            }
            throw new DetiPOSFault(auth.getMensaje());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }

    public SaldoRetun request(
            String cardNumber, 
            String cardKey, 
            String estacion) throws DetiPOSFault {

        try {
            AutenticateReturn auth = login();
            if (auth.getExito()==0) {

                SaldoParameter request = of.createSaldoParameter();

                request.setToken(auth.getToken());
                request.setEstacionPos(estacion);
                request.setTerminalPos(endpoint.getClave_aux());
                request.setTarjetaPos(cardNumber);
                request.setNip(cardKey);

                LogManager.info("PuntoGAS Saldo Request " + request);
                SaldoRetun response = service.saldo(request);
                LogManager.info("PuntoGAS Saldo " + response);

                if (response.getExitoso()==0) {
                    return response;
                }
                throw new DetiPOSFault("Code " + response.getExitoso() + "::" + response.getMensaje());
            }
            throw new DetiPOSFault(auth.getMensaje());
        } catch (UnknownHostException ex) {
           throw new DetiPOSFault(ex.getMessage());
        }
    }
}
