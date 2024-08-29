package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.pattern.BaseUpdater;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.pattern.ComandoUpdater;
import java.util.List;

public class ValesUpdater extends BaseUpdater implements ComandoUpdater {

    private ComandosVO comando;
    private List <String> boletos;

    public ValesUpdater setBoletos(List <String> boletos) {
        this.boletos = boletos;
        return this;
    }

    private void updateBoletos() {
        EstadoPosicionesVO posicion;
        ConsumoVO consumo;
        double importeCargado;
        double saldoBoleto;
        double cargoBoleto;
        double remanente;

        try {
            posicion = EstadoPosicionesDAO.getByID(Integer.parseInt(comando.NVL(ComandosVO.CMD_FIELDS.posicion.name())));
            consumo = ConsumosDAO.getByID(posicion.getFolio());
            LogManager.debug(posicion);
            LogManager.info(consumo);
            importeCargado = consumo.getCampoAsDouble(ConsumoVO.RM_FIELDS.pesos.name());
            if  (importeCargado > 0) {
                remanente = importeCargado;
                for (BoletosVO boleto : BoletosDAO.getBoletos(boletos.toArray(new String[] {}))) {
                    LogManager.info(boleto);
                    LogManager.info("Remanente " + remanente);
                    saldoBoleto = boleto.getCampoAsDouble("IMPORTEDISPONIBLE");
                    cargoBoleto=remanente<=saldoBoleto? remanente : saldoBoleto;
                    remanente-=cargoBoleto;
                    BoletosDAO.updateBoleto(
                            boleto.NVL("IDNVO"),
                            String.valueOf(cargoBoleto),
                            ( "0".equals(boleto.NVL("TICKET")) ) ? consumo.NVL(ConsumoVO.RM_FIELDS.id.name()) : "",
                            ( "0".equals(boleto.NVL("TICKET2")) && !"0".equals(boleto.NVL("TICKET")) ) ? consumo.NVL(ConsumoVO.RM_FIELDS.id.name()) : "",
                            saldoBoleto-cargoBoleto<=0.05D ? "No" : "");
                    if (remanente<=0) break;
                }
            }
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }

    @Override
    public void onSuccess(ComandosVO comando) {
        this.comando = comando;
        updateBoletos();
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
