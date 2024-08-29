
package com.detisa.omicrom.integraciones.monederos.gasovales;

import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.EndpointDAO;
import com.ass2.volumetrico.puntoventa.data.EndpointVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.mx.detisa.integrations.gasovales.GESSession;
import com.mx.detisa.integrations.gasovales.GasovalesTipoValesDescarga;
import com.mx.detisa.integrations.gasovales.GasovalesValeAplicado;
import com.mx.detisa.integrations.gasovales.GesMensaje;
import com.mx.detisa.integrations.gasovales.V1Soap;
import com.softcoatl.utils.logging.LogManager;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public class GasoValesApi {

    private EndpointVO endpoint;
    private V1Soap service;
    @Getter private final String wallet;

    public GasoValesApi(String wallet) throws DetiPOSFault {
        this.wallet = wallet;
        configure();
    }

    private void configure() throws DetiPOSFault {
        try {
            LogManager.info("Consultando monedero " + wallet);
            endpoint = EndpointDAO.get(wallet, "WALLET");
            service = GasoValesFactory.getCardService(endpoint.getUrl_webservice());
        } catch (MalformedURLException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    public GESSession createSession(int posicion, String employee) {
        return service.gasovalesCrearSesion(Integer.parseInt(endpoint.getUsuario()), employee, posicion, GasovalesTipoValesDescarga.GASOVALES);
    }

    public List<GasovalesValeAplicado> authorize(GESSession session, List<String> vales) {
        List<GasovalesValeAplicado> obtenidos = vales.stream().map(id -> { return service.gasovalesObtenerVale(session.getSesionID(), id, 0, ""); }).collect(Collectors.toList());
        obtenidos.stream().forEach(item -> BitacoraIntegracionesDAO.evento(wallet, item.getFolio(), "OBTENER", item));
        obtenidos.stream().filter(item -> !"00".equals(item.getRcode())).forEach(item -> LogManager.error(item.getFolio() + "::" + item.getMensaje()));
        return obtenidos.stream().filter(item -> "00".equals(item.getRcode())).collect(Collectors.toList());
    }

    public GesMensaje apply(GESSession session) {
        return service.gasovalesAplicarVales(session.getSesion());
    }
}
