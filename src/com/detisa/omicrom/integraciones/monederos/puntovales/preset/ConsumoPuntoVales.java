package com.detisa.omicrom.integraciones.monederos.puntovales.preset;

import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.detisa.omicrom.integraciones.monederos.puntovales.PuntoGasValesApi;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.detisa.omicrom.puntogas.vales.Vale;
import com.detisa.omicrom.puntogas.vales.api.PuntoGasValesException;
import com.detisa.omicrom.puntogas.vales.autenticacion.AutenticacionResponse;
import com.detisa.omicrom.puntogas.vales.consulta.ConsultaValesResponse;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.WebServiceException;
import lombok.Getter;

public class ConsumoPuntoVales extends ConsumoIntegracion {

    private ClientesVO monedero;
    private PuntoGasValesApi puntoValesApi;
    @Getter private ConsultaValesResponse response;
    @Getter private AutenticacionResponse auth;
    @Getter private List<String> vales;

    public ConsumoPuntoVales() throws DetiPOSFault {
        super();
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        try {
            super.init(parameters);
            monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
            puntoValesApi = new PuntoGasValesApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
            auth = puntoValesApi.authenticate();
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), "", "AUTHENTICATE", auth);
        } catch (NoSuchAlgorithmException | PuntoGasValesException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }

    @Override
    public boolean validate() throws DetiPOSFault {

        try {
            super.validate();
            vales = Arrays.stream(parameters.NVL(PRMT_ACCOUNT).split("[|]")).distinct().collect(Collectors.toList());
            response = puntoValesApi.request(auth.getToken(), vales, "", 1);
            vales.forEach(vale -> 
                    BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), vale, "REQUEST", response));

            BigDecimal saldo = response.getSaldoAutorizado();
            if (saldo.compareTo(BigDecimal.ZERO)>0) {
                vales.removeAll(response.getVales().stream().filter(item -> !item.getStatus().equals("Activo")).map(Vale::getVale).collect(Collectors.toList()));
                transform("IMPORTE", saldo.toPlainString());
                comando = ComandosVO.parse(manguera, "IMPORTE", saldo.toPlainString(), "Consumo GASOVALES", false);
                comando.setField(ComandosVO.CMD_FIELDS.idtarea, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
                LogManager.error(comando);
                posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
            } else {
                throw new DetiPOSFault(response.getVales().stream().map(item -> item.getVale() + " " + item.getMsgStatus()).collect(Collectors.joining(" ")));
            }
        } catch (NoSuchAlgorithmException | PuntoGasValesException ex)  {
            throw new DetiPOSFault("Error consultando el servicio PuntoVales. " + ex.getMessage());
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        }
    
        return true;
    }
}