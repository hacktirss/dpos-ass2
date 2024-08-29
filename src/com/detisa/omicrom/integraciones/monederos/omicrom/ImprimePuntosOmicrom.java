package com.detisa.omicrom.integraciones.monederos.omicrom;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.ImprimeIntegracionBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softcoatl.utils.logging.LogManager;

public class ImprimePuntosOmicrom extends ImprimeIntegracionBase {

    public ImprimePuntosOmicrom(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("rcode") && "00".equals(nvl("rcode"));
    }
    
    
    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (valid()) {
            comprobante.append("TIPODEPAGO", "Boletos");
            JsonArray vales = json.get("vales").getAsJsonArray();
            for (int i = 0; i < vales.size(); i++) {
                JsonObject item = vales.get(i).getAsJsonObject();
                comprobante.append("BOL" + (i+1), nvl(item, "Folio"))
                            .append("BOL_CODIGO@BOL" + (i+1), nvl(item, "Folio"))
                            .append("BOL_SALDO@BOL" + (i+1), "0.00")
                            .append("BOL_IMPORTE@BOL" + (i+1), nvl(item, "Importe"));  
            }
        }
        LogManager.info(comprobante);
        return comprobante;
    }    
}
