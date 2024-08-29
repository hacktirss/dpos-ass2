package com.detisa.omicrom.integraciones.monederos.puntovales;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.ImprimeIntegracionBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softcoatl.utils.logging.LogManager;

public class ImprimePuntoVales extends ImprimeIntegracionBase {

    public ImprimePuntoVales(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("Vales");
    }
    
    
    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (valid()) {
            comprobante.append("TIPODEPAGO", "Boletos")
                    .append("TRMonedero", "PuntoGAS Vales")
                    .append("TRAutorizacion", nvl("NoAutorizacion"))
                    .append("TRFormaPago", nvl("FormaPago"))
                    .append("TRCliente", nvl("NombreCliente"))
                    .append("TROperador", nvl("Chofer"))
                    .append("TRPlacas", nvl("Placas"));

            JsonArray vales = json.get("Vales").getAsJsonArray();
            for (int i = 0; i < vales.size(); i++) {
                JsonObject item = vales.get(i).getAsJsonObject();
                LogManager.info(item);
                comprobante.append("BOL" + (i+1), nvl(item, "Vale"))
                            .append("BOL_CODIGO@BOL" + (i+1), nvl(item, "Vale"))
                            .append("BOL_SALDO@BOL" + (i+1), "Acitvo".equals(nvl(item, "Status")) ? nvl(item, "Importe") : "0.00")
                            .append("BOL_IMPORTE@BOL" + (i+1), nvl(item, "Monto"));  
            }
        }
        LogManager.info(comprobante);
        return comprobante;
    }
}
