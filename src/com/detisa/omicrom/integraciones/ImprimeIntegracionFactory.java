package com.detisa.omicrom.integraciones;

import com.detisa.omicrom.integraciones.monederos.gasngo.ImprimeGasngo;
import com.detisa.omicrom.integraciones.monederos.gasovales.ImprimeGasoVales;
import com.detisa.omicrom.integraciones.monederos.gasomatic.ImprimeGasoMatic;
import com.detisa.omicrom.integraciones.monederos.puntogas.ImprimePuntoGas;
import com.detisa.omicrom.integraciones.monederos.puntovales.ImprimePuntoVales;
import com.detisa.omicrom.integraciones.monederos.yena.ImprimeYena;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ImprimeIntegracionFactory {

    public static ImprimeIntegracion get(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject json = element.getAsJsonObject();
            ImprimeIntegracion imp;
            imp = new ImprimeBankTransaction(json);
            if (imp.valid()) {
                return imp;
            }
            imp = new ImprimeGasngo(json);
            if (imp.valid()) {
                return imp;
            }
            imp = new ImprimeGasoVales(json);
            if (imp.valid()) {
                return imp;
            }
            imp = new ImprimeGasoMatic(json);
            if (imp.valid()) {
                return imp;
            }
            imp = new ImprimePuntoVales(json);
            if (imp.valid()) {
                return imp;
            }
            imp = new ImprimePuntoGas(json);
            if (imp.valid()) {
                return imp;
            }
            imp = new ImprimeYena(json);
            if (imp.valid()) {
                return imp;
            }
        }
        return null;
    }
}
