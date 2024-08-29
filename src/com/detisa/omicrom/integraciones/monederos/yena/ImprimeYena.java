package com.detisa.omicrom.integraciones.monederos.yena;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.detisa.omicrom.integraciones.ImprimeIntegracionBase;
import com.google.gson.JsonObject;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ImprimeYena extends ImprimeIntegracionBase {

    public ImprimeYena(JsonObject json) {
        super(json);
    }

    @Override
    public boolean valid() {
        return exists("CampaignID");
    }
    
    @Override
    public Comprobante extract() {
        Comprobante comprobante = new Comprobante();
        if (valid()) {
            comprobante
                    .append("TRMonedero", "YENA!")
                    .append("TRAutorizacion", nvl("AuthCode"))
                    .append("TRSession", nvl("SessionID"))
                    .append("TRSaldo", nvl("CurrentMoney"))
                    .append("TRAccount", nvl("CardNumber"));
            if (exists("Amount")) {
                comprobante.append("TRMonto", nvl("Amount"));
            }
            if ("1".equals(nvl("CampaignID")) && exists("PreviousMoney")) {
                comprobante.append("TRSaldoA", nvl("PreviousMoney"));
            } else if (nvl("CampaignID").contains("Descuento") && exists("PreviousMoney")) {
                comprobante.append("TRDescuento", nvl("PreviousMoney"));
            } else if (exists("CurrentMoney") && exists("PreviousMoney")) {
                BigDecimal abono = new BigDecimal(nvl("CurrentMoney"))
                                    .subtract(new BigDecimal(nvl("PreviousMoney"))).setScale(2, RoundingMode.HALF_EVEN);
                comprobante.append("TRSaldoA", nvl("PreviousMoney"));
                comprobante.append("TRAbono", abono.toPlainString());
            }
            try {
                if (exists("Coupons")) {
                    JsonObject cupones = json.getAsJsonArray("Coupons").get(0).getAsJsonObject();
                    comprobante.append("TRAddText1", "CUPONES");
                    comprobante.append("TRAddText2", "EXPIRA" + nvl(cupones, "ExpirationDate"));
                    comprobante.append("TRAddWrap1", "CODIGO " + nvl(cupones, "Code").replaceAll("[-]", " - "));
                    comprobante.append("TRAddWrap2", nvl(cupones, "Description"));
                }
            } catch(Throwable ex) {
                LogManager.error(ex);
            }
        }
        LogManager.info(comprobante);
        return comprobante;
    }
}
