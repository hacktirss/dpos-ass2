package com.detisa.omicrom.integraciones;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.google.gson.JsonObject;

public class ImprimeBankTransaction extends ImprimeIntegracionBase {

    public ImprimeBankTransaction(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("ARQC");
    }
    
    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (valid()) {
            comprobante.append("BANK", "1")
                    .append("TRMonedero", nvl("APN"))
                    .append("TRAutorizacion", nvl("CodigoAprobacion"))
                    .append("TRAccount", nvl("DigitosTarjeta"))
                    .append("TRMonto", nvl("Monto"))
                    .append("TRAddText1", "Afiliacion " + nvl("Afiliacion"))
                    .append("TRAddText2", "AID " + nvl("AID"))
                    .append("TRAddText3", "ARQC " + nvl("ARQC"));
        }
        return comprobante;
    }    
}
