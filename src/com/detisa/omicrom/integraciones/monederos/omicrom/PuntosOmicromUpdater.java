
package com.detisa.omicrom.integraciones.monederos.omicrom;

import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import com.softcoatl.data.DinamicVO;

public class PuntosOmicromUpdater extends BaseUpdater implements ComandoUpdater {

    private ComandosVO comando;
    private DinamicVO<String, String> parameters;

    public PuntosOmicromUpdater setRequest(DinamicVO<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
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
