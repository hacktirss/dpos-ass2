package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;

public class AutorizacionesUpdater extends BaseUpdater implements ComandoUpdater {

    private UnidadVO unidad;

    public ComandoUpdater setUnidad(UnidadVO unidad) {
        this.unidad = unidad;
        return this;
    }

    private void updateSaldo(int rmid, BigDecimal despachado) {
        if ("B".equals(unidad.NVL("PERIODO"))) {
            AutorizacionesDAO.saldos(
                    rmid,
                    unidad.getCampoAsDecimal("IMPORTE"),
                    despachado,
                    unidad.getCampoAsInt("ID"));
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        EstadoPosicionesVO posicion;
        ConsumoVO consumo;
        LogManager.info("Actualizando comando exitoso id " + comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
        LogManager.debug(comando);
        posicion = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
        consumo = ConsumosDAO.getByID(posicion.getFolio());
        AutorizacionesDAO.ejecutado(
                consumo.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()),
                consumo.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()),
                comando.getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
        updateSaldo(consumo.getCampoAsInt(ConsumoVO.RM_FIELDS.id.name()), consumo.getCampoAsDecimal(ConsumoVO.RM_FIELDS.pesos.name()));
    }

    @Override
    public void onError(ComandosVO comando) {
        super.onError(comando);
        AutorizacionesDAO.vencido(comando.getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
    }

    @Override
    public void onTimeout(ComandosVO comando) {
        super.onTimeout(comando);
        AutorizacionesDAO.vencido(comando.getCampoAsInt(ComandosVO.CMD_FIELDS.id.name()));
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
