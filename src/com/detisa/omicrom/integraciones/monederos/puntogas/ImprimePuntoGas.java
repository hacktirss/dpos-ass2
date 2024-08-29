package com.detisa.omicrom.integraciones.monederos.puntogas;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.ImprimeIntegracionBase;
import com.google.gson.JsonObject;
import com.softcoatl.utils.logging.LogManager;

public class ImprimePuntoGas extends ImprimeIntegracionBase {

    public ImprimePuntoGas(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("formaPago") && "Monedero GES".equals(nvl("formaPago"));
    }

    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (valid()) {
            comprobante
                    .append("TRMonedero", "PuntoGAS")
                    .append("TRAutorizacion", nvl("noAutorizacion"))
                    .append("TRCliente", nvl("nombreCliente"))
                    .append("TROperador", nvl("chofer"))
                    .append("TRPlacas", nvl("placas"))
                    .append("TROdometro", nvl("odometro"))
                    .append("TRFormaPago", nvl("formaPago"))
                    .append("TRSaldo", nvl("saldo"))
                    .append("TRPuntos", nvl("puntosBonificados"))
                    .append("TRMonto", nvl("monto"))
                    .append("TRPago", nvl("montoApagar", "0")); 
        }
        LogManager.info(comprobante);
        return comprobante;
    }
}
