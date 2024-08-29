
package com.detisa.omicrom.integraciones.monederos.puntogas;

import com.detisa.integrations.puntogas.AprobacionReturn;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleDAO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.CuentasPorCobrarDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_KM;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_PIN_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.util.Calendar;

public class PuntoGASUpdater extends BaseUpdater implements ComandoUpdater {

    private ComandosVO comando;
    private DinamicVO<String, String> parameters;

    public PuntoGASUpdater setRequest(DinamicVO<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    private void saveAutorizacion(int folio, BigDecimal importe, Calendar fecha, AprobacionReturn confirmacion) {
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

    private void validateCXC(String id) {
        try {
            CuentasPorCobrarDAO.cancelConsumo(id);
        } catch (DBException ex) {
            LogManager.error(ex);
        }
    }

    private void confirmPuntoGAS() throws DetiPOSFault {

        PuntoGASApi api = new PuntoGASApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
        try {
            AprobacionReturn confirmacion;

            EstadoPosicionesVO posicion;
            CombustibleVO combustible;
            ConsumoVO rm;

            posicion    = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
            rm          = ConsumosDAO.getByID(posicion.getFolio());
            combustible = CombustibleDAO.getCombustibleByClavei(rm.NVL(ConsumoVO.RM_FIELDS.producto.name()));

            confirmacion = api.confirm(
                    parameters.NVL(PRMT_ACCOUNT),
                    parameters.NVL(PRMT_PIN_ACCOUNT),
                    rm.NVL(ConsumoVO.RM_FIELDS.id.name()),
                    rm.NVL(ConsumoVO.RM_FIELDS.posicion.name()),
                    CombustiblesPuntoGAS.mapping(combustible.NVL(CombustibleVO.COM_FIELDS.clave.name())),
                    rm.NVL(ConsumoVO.RM_FIELDS.pesos.name()),
                    rm.NVL(ConsumoVO.RM_FIELDS.volumen.name()),
                    parameters.NVL(PRMT_CNS_KM, "0"),
                    "1".equals(parameters.NVL("isImporte", "0")));
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "CONFIRM", confirmacion);

            saveAutorizacion(
                    rm.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()), 
                    rm.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()), 
                    rm.getCampoAsCalendar(ConsumoVO.RM_FIELDS.fin_venta.name()), 
                    confirmacion);

            if (confirmacion.getExitoso()>0 && BigDecimal.ZERO.compareTo(confirmacion.getMonto())==0) {
                validateCXC(rm.NVL(ConsumoVO.RM_FIELDS.id.name()));
            } else if (confirmacion.getExitoso()>0 && BigDecimal.ZERO.compareTo(confirmacion.getMonto())>0) {
                ConsumosDAO.setPagoReal(rm.NVL(ConsumoVO.RM_FIELDS.id.name()), confirmacion.getMonto());
            }
        } catch (DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento(api.getWallet(), parameters.NVL(PRMT_ACCOUNT), "GES EXCEPTION", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
        try { 
            confirmPuntoGAS();
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
        }
    }

    @Override
    public void onError(ComandosVO comando) {
        super.onError(comando);
        BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "ERROR", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        super.onTimeout(comando);
        BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "TIMEOUT", comando.NVL(ComandosVO.CMD_FIELDS.descripcion.name()));
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
