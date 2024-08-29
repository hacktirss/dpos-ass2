package com.detisa.omicrom.integraciones.monederos.gasngo;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.ImprimeIntegracionBase;
import com.google.gson.JsonObject;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;

public class ImprimeGasngo extends ImprimeIntegracionBase {

    public ImprimeGasngo(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("GngID");
    }

    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (exists("GngID")) {
            JsonObject preset = json.get("Preset").getAsJsonObject();
            comprobante.append("GNG", "1")
                    .append("TRMonedero", "GOSMO")
                    .append("TRFormaPago", "GOSMO")
                    .append("TRAutorizacion", nvl("GngID"))
                    .append("TROdometro", nvl("Odometer"))
                    .append("TRRendimiento", nvl("Rendimiento", "0"))
                    .append("TRCliente", nvl(preset, "ClientName"))
                    .append("TRRfc", nvl(preset, "ClientVatNumber"))
                    .append("TRNumCliente", nvl(preset, "ClientNumber"))
                    .append("TRPlacas", nvl(preset, "VehiclePlate"))
                    .append("TREco", nvl(preset, "VehicleEco"))
                    .append("TRAddWrap1", "VEHICULO CEDI: " + nvl(preset, "VehicleCEDI"));
            if (exists(preset, "ContingencyCode")) {
                comprobante.append("TRAddWrap2", "NUMERO DE INCIDENCIA: " + nvl(preset, "ContingencyCode"));
            }
            if (BigDecimal.ZERO.compareTo(new BigDecimal(nvl("Rendimiento", "0"))) >= 0) {
                comprobante.append("TRAddWrap3", "El odómetro capturado es menor que el odómetro anterior.");
            }
        }
        LogManager.info(comprobante);
        return comprobante;
    }
}
