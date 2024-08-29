
package com.detisa.omicrom.integraciones.monederos.gasngo;

import com.detisa.integrations.gasngo.Event;
import com.detisa.integrations.gasngo.ObjectFactory;
import com.detisa.integrations.gasngo.Products;
import com.detisa.integrations.gasngo.service.GasngoServer;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.TerminalAlmacenamientoVO;
import com.ass2.volumetrico.puntoventa.data.TerminalesAlmacenamientoDAO;
import com.detisa.omicrom.integraciones.monederos.gasngo.preset.ConsumoGasngo;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcoatl.integration.ServiceLocator;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Calendar;
import javax.xml.datatype.DatatypeConfigurationException;

public class GasngoUpdater extends BaseUpdater implements ComandoUpdater {

    private ConsumoGasngo consumo;
    private ComandosVO comando;

    public GasngoUpdater setConsumo(ConsumoGasngo consumo) {
        this.consumo = consumo;
        return this;
    }

    private BigDecimal calcRentimiento(BigDecimal volumen, BigDecimal kmini, BigDecimal kmend) {
        BigDecimal rendimiento = BigDecimal.ZERO;
        try {
            rendimiento = kmend.subtract(kmini).divide(volumen, RoundingMode.HALF_EVEN);
        } catch (ArithmeticException exc) {
            LogManager.error(exc);
        }
        return rendimiento;
    } 
    private void saveAutorizacion(int folio, BigDecimal importe, BigDecimal volumen, BigDecimal kilometraje, Calendar fecha) {
        ObjectMapper mapper = new ObjectMapper();
        String authSequence;

        try {
            consumo.getAutorizacion().setRendimiento(calcRentimiento(volumen, consumo.getAutorizacion().getOdometer(), kilometraje));
            consumo.getAutorizacion().setOdometer(kilometraje);
            authSequence = mapper.writeValueAsString(consumo.getAutorizacion());
            AutorizacionesrmDAO.evento(
                    folio,
                    fecha,
                    importe,
                    authSequence);
        } catch (JsonProcessingException ex) {
            LogManager.error(ex);
        }
    }

    private int getTAR(int posicion, int manguera) {
        try {
            LogManager.debug("Consultando TAR " + posicion + "/" + manguera);
            return TerminalesAlmacenamientoDAO.getTerminalAlmacenamiento(posicion, manguera).getCampoAsInt(TerminalAlmacenamientoVO.FIELDS.terminal.name());
        } catch (SQLException ex) {
            LogManager.error(ex);
        }
        return 0;
    }

    private void start() {
        try {
            GasngoServer api = (GasngoServer) ServiceLocator.getInstance().getService(GasngoServer.NAME);
            Event start = ObjectFactory.createStartEvent(consumo.getAutorizacion().getGngID(), comando.getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
            start.setNozzles(consumo.getNozzles());
            start.setOdometer(new BigDecimal(consumo.getParameters().NVL(PRMT_CNS_KM, "0")));
            api.send(start);
        } catch (IOException | DatatypeConfigurationException ex) {
            BitacoraIntegracionesDAO.evento("GASNGO", consumo.getParameters().NVL(PRMT_ACCOUNT), "GASNGO EXCEPTION", ex.getMessage());
            LogManager.error(ex);
        }
    }

    private void end() {
        try {
            EstadoPosicionesVO posicion = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
            ConsumoVO rm = ConsumosDAO.getByID(posicion.getFolio());
            CombustibleVO combustible = CombustibleDAO.getCombustibleByClavei(rm.NVL(ConsumoVO.RM_FIELDS.producto.name()));

            if (BigDecimal.ZERO.compareTo(rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name())) == 0) {
                consumo.cancel();
                throw new DetiPOSFault("Consumo en cero, cancelando autorización.");
            }

            GasngoServer api = (GasngoServer) ServiceLocator.getInstance().getService(GasngoServer.NAME);
            Event end = ObjectFactory.createEndEvent(consumo.getAutorizacion().getGngID(), comando.getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
            end.setNozzles(consumo.getNozzles());
            end.setProduct(Products.fromValue(combustible.getCampoAsInt(CombustibleVO.COM_FIELDS.clave.name())));
            end.setVolume(rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.volumen.name()).setScale(3, RoundingMode.HALF_EVEN));
            end.setCost(rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()).setScale(2, RoundingMode.HALF_EVEN));
            end.setTransactionNumber(rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()));
            end.setTransactionInternalReference(comando.getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
            end.setProductPrice(rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.precio.name()).setScale(2, RoundingMode.HALF_EVEN));
            end.setOdometer(new BigDecimal(consumo.getParameters().NVL(PRMT_CNS_KM, "0")));
            end.setTar(getTAR(rm.getCampoAsInt(ConsumoVO.RM_FIELDS.posicion.name()), rm.getCampoAsInt(ConsumoVO.RM_FIELDS.manguera.name())));
            api.send(end);
            saveAutorizacion(
                    rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()), 
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.volumen.name()),
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.kilometraje.name()),
                    rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()));
        } catch (IOException | DatatypeConfigurationException | DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento("GASNGO", consumo.getParameters().NVL(PRMT_ACCOUNT), "GASNGO EXCEPTION", ex.getMessage());
            LogManager.error(ex);
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        LogManager.info("Done");
        LogManager.debug(comando);
        this.comando = comando;
        end();
    }

    @Override
    public void onError(ComandosVO comando) {
        super.onError(comando);
        this.comando = comando;
        BitacoraIntegracionesDAO.evento(consumo.getParameters().NVL(Consumo.PRMTR_APP_NAME), consumo.getParameters().NVL(PRMT_ACCOUNT), "ERROR", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
        consumo.cancel();
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        super.onTimeout(comando);
        this.comando = comando;
        BitacoraIntegracionesDAO.evento(consumo.getParameters().NVL(Consumo.PRMTR_APP_NAME), consumo.getParameters().NVL(PRMT_ACCOUNT), "TIMEOUT", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
        consumo.cancel();
    }

    @Override
    public void onPullback(ComandosVO comando) {
        LogManager.info("Manguera descolgada");
        LogManager.debug(comando);
        this.comando = comando;
        start();
    }

    @Override
    public void onDispatching(ComandosVO comando) {
        LogManager.info("Despachando");
        LogManager.debug(comando);
        this.comando = comando;
    }
}
