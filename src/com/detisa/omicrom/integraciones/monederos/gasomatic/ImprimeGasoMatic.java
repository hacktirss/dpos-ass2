package com.detisa.omicrom.integraciones.monederos.gasomatic;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.ImprimeIntegracionBase;
import com.google.gson.JsonObject;
import com.softcoatl.utils.logging.LogManager;

public class ImprimeGasoMatic extends ImprimeIntegracionBase {

    public ImprimeGasoMatic(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("codigo") && exists("idOperacion") && exists("autorizacion");
    }

    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (valid()) {
            comprobante
                    .append("TRMonedero", "GasoMatic")
                    .append("TRAutorizacion", nvl("autorizacion"))
                    .append("TRPlacas", nvl("placa"))
                    .append("TROdometro", nvl("km"))
                    .append("TRRendimiento", nvl("rendimiento", "0"))
                    .append("TRSaldo", nvl("saldo"))
                    .append("TRAddWrap1", "IdOperacion: " + nvl("idOperacion"));
        }
        LogManager.info(comprobante);
        return comprobante;
    }
}
