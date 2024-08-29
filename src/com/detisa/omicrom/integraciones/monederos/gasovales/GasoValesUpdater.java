
package com.detisa.omicrom.integraciones.monederos.gasovales;

import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.detisa.omicrom.integraciones.monederos.gasovales.preset.ConsumoTarjetaGasoVales;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.detisa.integrations.gasovales.GasovalesValeAplicado;
import com.mx.detisa.integrations.gasovales.GesMensaje;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

public class GasoValesUpdater extends BaseUpdater implements ComandoUpdater {

    private static class GasoVales extends GesMensaje {

        @Getter private List<GasovalesValeAplicado> vales;

        public GasoVales(GesMensaje mensaje) {
            setId(mensaje.getId());
            setMensaje(mensaje.getMensaje());
            setRcode(mensaje.getRcode());
            setDescripcion(mensaje.getDescripcion());
        }

        public GasoVales setVales(List<GasovalesValeAplicado> vales) {
            this.vales = vales;
            return this;
        }
    }

    private ComandosVO comando;
    private ConsumoTarjetaGasoVales consumo;

    public GasoValesUpdater setConsumo(ConsumoTarjetaGasoVales consumo) {
        this.consumo = consumo;
        return this;
    }
    
    private GasoVales apply() throws DetiPOSFault {
        GasoValesApi gasoValesApi = new GasoValesApi(consumo.getParameters().NVL(Consumo.PRMTR_APP_NAME));
        GesMensaje mensaje = gasoValesApi.apply(consumo.getSession());
        consumo.getGasovales().forEach(item -> 
                BitacoraIntegracionesDAO.evento(gasoValesApi.getWallet(), item.getFolio(), "APLICAR", mensaje));
        return new GasoVales(mensaje).setVales(consumo.getGasovales());
    }

    private void saveAutorizacion(int folio, BigDecimal importe, Calendar fecha) {
        ObjectMapper mapper = new ObjectMapper();
        String authSequence;

        try {
            authSequence = mapper.writeValueAsString(apply());
            AutorizacionesrmDAO.evento(
                    folio,
                    fecha,
                    importe,
                    authSequence);
        } catch (JsonProcessingException ex) {
            LogManager.error(ex);
        } catch (DetiPOSFault ex) {
            Logger.getLogger(GasoValesUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void confirmGasoVales() throws DetiPOSFault {

        EstadoPosicionesVO posicion = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
        ConsumoVO rm = ConsumosDAO.getByID(posicion.getFolio());
        LogManager.info(rm);
        saveAutorizacion(
                rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()),
                rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()),
                rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()));
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
        try { 
            confirmGasoVales();
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
    }

    @Override
    public void onError(ComandosVO comando) {
        super.onError(comando);
        BitacoraIntegracionesDAO.evento(consumo.getParameters().NVL(Consumo.PRMTR_APP_NAME), consumo.getParameters().NVL(PRMT_ACCOUNT), "ERROR", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        super.onTimeout(comando);
        BitacoraIntegracionesDAO.evento(consumo.getParameters().NVL(Consumo.PRMTR_APP_NAME), consumo.getParameters().NVL(PRMT_ACCOUNT), "TIMEOUT", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
    }
    

    @Override
    public void onPullback(ComandosVO comando) {
        // Do nothing
    }

    @Override
    public void onDispatching(ComandosVO comando) {
        // Do nothing
    }
}
