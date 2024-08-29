
package com.detisa.omicrom.integraciones.monederos.puntovales;

import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.detisa.omicrom.integraciones.monederos.puntovales.preset.ConsumoPuntoVales;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.detisa.omicrom.puntogas.vales.api.PuntoGasValesException;
import com.detisa.omicrom.puntogas.vales.aprobacion.AprobacionValesResponse;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class PuntoValesUpdater extends BaseUpdater implements ComandoUpdater {

    private ComandosVO comando;
    private ConsumoPuntoVales consumo;

    public PuntoValesUpdater setConsumo(ConsumoPuntoVales consumo) {
        this.consumo = consumo;
        return this;
    }
    
    private void saveAutorizacion(int folio, BigDecimal importe, Calendar fecha, AprobacionValesResponse aprobacion) {
        ObjectMapper mapper = new ObjectMapper();
        String authSequence;

        try {
            authSequence = mapper.writeValueAsString(aprobacion);
            LogManager.info(folio);
            LogManager.info(importe);
            LogManager.info(fecha);
            LogManager.info(authSequence);
            AutorizacionesrmDAO.evento(
                    folio,
                    fecha,
                    importe,
                    authSequence);
        } catch (JsonProcessingException ex) {
            LogManager.error(ex);
        }
    }

    private void apply() throws DetiPOSFault {
        try {
            EstadoPosicionesVO posicion = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
            ConsumoVO rm = ConsumosDAO.getByID(posicion.getFolio());
            CombustibleVO combustible = CombustibleDAO.getCombustibleByClavei(rm.NVL(ConsumoVO.RM_FIELDS.producto.name()));
            PuntoGasValesApi puntoValesApi = new PuntoGasValesApi(consumo.getParameters().NVL(Consumo.PRMTR_APP_NAME));
            AprobacionValesResponse aprobacion = puntoValesApi.approve(
                    consumo.getAuth().getToken(), consumo.getVales(),
                    rm.getCampoAsInt(ConsumoVO.RM_FIELDS.posicion.name()),
                    rm.NVL(ConsumoVO.RM_FIELDS.id.name()),
                    rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.volumen.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.precio.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.importe.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.iva.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.ieps.name()),
                    combustible.NVL(CombustibleVO.COM_FIELDS.clave.name()));
            consumo.getVales().forEach(vale -> 
                    BitacoraIntegracionesDAO.evento(puntoValesApi.getWallet(), vale, "APPROVE", aprobacion));
            saveAutorizacion(
                    rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()),
                    rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()),
                    aprobacion);
        } catch (NoSuchAlgorithmException | PuntoGasValesException ex) {
            LogManager.error("Error aplicando consumo PuntoVales " + ex.getMessage());
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
        try { 
            apply();
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
        super.onError(comando);
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
