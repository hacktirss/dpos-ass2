
package com.detisa.omicrom.integraciones.monederos.gasomatic;

import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_KM;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_MNG;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_POS;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_PIN_ACCOUNT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.detisa.integrations.gasomatic.Combustibles;
import com.mx.detisa.integrations.gasomatic.RespuestaAutorizar;
import com.mx.detisa.integrations.gasomatic.RespuestaConfirmar;
import com.mx.detisa.integrations.gasomatic.TipoCarga;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.util.Calendar;

public class GasoMaticUpdater extends BaseUpdater implements ComandoUpdater {

    private ComandosVO comando;
    private DinamicVO<String, String> parameters;
    private RespuestaAutorizar auth;

    public GasoMaticUpdater setRequest(DinamicVO<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public GasoMaticUpdater setAuth(RespuestaAutorizar auth) {
        this.auth = auth;
        return this;
    }

    private void saveAutorizacion(int folio, BigDecimal importe, Calendar fecha, RespuestaConfirmar confirmacion) {
        ObjectMapper mapper = new ObjectMapper();
        String authSequence;

        try {
            authSequence = mapper.writeValueAsString(confirmacion);
            AutorizacionesrmDAO.evento(
                    folio,
                    fecha,
                    importe,
                    authSequence);
        } catch (JsonProcessingException ex) {
            LogManager.error(ex);
        }
    }

    private void confirmGasoMatic() throws DetiPOSFault {

        try {
            EstadoPosicionesVO posicion;
            CombustibleVO combustible;
            ConsumoVO rm;

            posicion    = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
            rm          = ConsumosDAO.getByID(posicion.getFolio());
            combustible = CombustibleDAO.getCombustibleByClavei(rm.NVL(ConsumoVO.RM_FIELDS.producto.name()));

            RespuestaConfirmar confirm = new GasoMaticApi(parameters.NVL(Consumo.PRMTR_APP_NAME)).confirm(
                    parameters.NVL(PRMT_ACCOUNT), 
                    parameters.NVL(PRMT_PIN_ACCOUNT), 
                    comando.NVL(ComandosVO.CMD_FIELDS.comando.name()).startsWith("V") ? TipoCarga.LITROS : TipoCarga.PRECIO, 
                    Combustibles.valueOf(combustible.NVL(CombustibleVO.COM_FIELDS.descripcion.name())),
                    comando.getCampoAsInt(ComandosVO.CMD_FIELDS.posicion.name()), 
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()),
                    auth.getIdOperacion(),
                    auth.getPlaca(),
                    Integer.parseInt(parameters.NVL(PRMT_CNS_KM, "0")));
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "CONFIRM", confirm);

            saveAutorizacion(
                    rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()), 
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()), 
                    rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()), 
                    confirm);
        } catch (DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "GES EXCEPTION", ex.getMessage());
            throw ex;
        }
    }

    private void cancelGasoMatic() {

        try {
            BaseVO manguera = ManguerasDAO.getDispensarioPosicionManguera(parameters.NVL(PRMT_CNS_POS), parameters.NVL(PRMT_CNS_MNG));
            CombustibleVO combustible = CombustibleDAO.getCombustibleByClavei(manguera.NVL(ManguerasVO.DSP_FIELDS.producto.name()));

            RespuestaConfirmar confirm = new GasoMaticApi(parameters.NVL(Consumo.PRMTR_APP_NAME)).cancel(
                    parameters.NVL(PRMT_ACCOUNT), 
                    parameters.NVL(PRMT_PIN_ACCOUNT), 
                    comando.NVL(ComandosVO.CMD_FIELDS.comando.name()).startsWith("V") ? TipoCarga.LITROS : TipoCarga.PRECIO, 
                    Combustibles.valueOf(combustible.NVL(CombustibleVO.COM_FIELDS.descripcion.name())),
                    comando.getCampoAsInt(ComandosVO.CMD_FIELDS.posicion.name()), 
                    auth.getIdOperacion(),
                    auth.getPlaca(),
                    Integer.parseInt(parameters.NVL(PRMT_CNS_KM, "0")));
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "CANCEL", confirm);
        } catch (DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "GES EXCEPTION", ex.getMessage());
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
        try { 
            confirmGasoMatic();
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
    }

    @Override
    public void onError(ComandosVO comando) {
        super.onError(comando);
        this.comando = comando;
        BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "ERROR", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
        cancelGasoMatic();
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        super.onTimeout(comando);
        this.comando = comando;
        BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "TIMEOUT", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
        cancelGasoMatic();
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
