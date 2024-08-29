package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleDAO;
import com.ass2.volumetrico.puntoventa.data.CorteVO;
import com.ass2.volumetrico.puntoventa.data.CorteVO.CT_FIELDS;
import com.ass2.volumetrico.puntoventa.data.DepositoDAO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CorteParcialAction extends BaseAction {

    public static final String WS_CT_ID = "corteID";

    public CorteParcialAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (!VariablesDAO.isEpsilon()) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error en la Interface", "La facilidad de Corte Parcial sólo aplica a la interface Epsilon"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {

        BigDecimal pMonto = BigDecimal.ZERO;
        BigDecimal pVolumen = BigDecimal.ZERO;
        BigDecimal pDescuento = BigDecimal.ZERO;
        BigDecimal tMonto = BigDecimal.ZERO;
        BigDecimal tVolumen = BigDecimal.ZERO;
        BigDecimal tAditivos = BigDecimal.ZERO;
        BigDecimal cAditivos = BigDecimal.ZERO;
        BigDecimal tDepositos = BigDecimal.ZERO;
        BigDecimal tDescuento = BigDecimal.ZERO;

        String currentPos = "";
        CorteVO corte;
        Comprobante comprobante = super.getComprobante();
        int idx = 1;
        int idx2 = 1;

        corte = CorteDAO.getCorte(CorteDAO.getCorteAbierto("1"));
        // Cambia la fecha final por la fecha de impresión
        corte.setField("fechaf", DateUtils.fncsFormat("dd/MM/YYYY HH:mm:ss"));

        for (DinamicVO<String, String> detalle : CorteDetalleDAO.getDetallesCorteAbierto(parameters.NVL(WS_PRMT_ISLA))) {
            LogManager.debug(detalle);
            tMonto = tMonto.add(new BigDecimal(detalle.NVL("dmonto")).setScale(3, RoundingMode.HALF_EVEN));
            tVolumen = tVolumen.add(new BigDecimal(detalle.NVL("dvolumen")).setScale(3, RoundingMode.HALF_EVEN));
            tDescuento = tDescuento.add(new BigDecimal(detalle.NVL("descuento", "0.00"))).setScale(3, RoundingMode.HALF_EVEN);
            if (currentPos.isEmpty() || !currentPos.equals(detalle.NVL("posicion"))) {
                if (!currentPos.isEmpty()) {
                    comprobante.append("TTL" + idx2, "" + idx2)
                            .append("posicion@TTL" + idx2, currentPos)
                            .append("monto@TTL" + idx2, pMonto.toPlainString())
                            .append("descuento@TTL" + idx2, pDescuento.toPlainString())
                            .append("volumen@TTL" + idx2++, pVolumen.toPlainString());
                }
                currentPos = detalle.NVL("posicion");
                pMonto = BigDecimal.ZERO;
                pVolumen = BigDecimal.ZERO;
                pDescuento = BigDecimal.ZERO;
            }

            pMonto = pMonto.add(new BigDecimal(detalle.NVL("dmonto")).setScale(3, RoundingMode.HALF_EVEN));
            pVolumen = pVolumen.add(new BigDecimal(detalle.NVL("dvolumen")).setScale(3, RoundingMode.HALF_EVEN));
            pDescuento = pDescuento.add(new BigDecimal(detalle.NVL("descuento", "0.00")).setScale(3, RoundingMode.HALF_EVEN));
            comprobante.append("DTL" + idx, "" + idx)
                    .append("posicion@DTL" + idx, detalle.NVL("posicion"))
                    .append("descripcion@DTL" + idx, detalle.NVL("descripcion"))
                    .append("dmonto@DTL" + idx, detalle.NVL("dmonto"))
                    .append("descuento@DTL" + idx, detalle.NVL("descuento"))
                    .append("dvolumen@DTL" + idx++, detalle.NVL("dvolumen"));
        }
        if (!currentPos.isEmpty()) {
            comprobante.append("TTL" + idx2,  "" + idx2)
                    .append("posicion@TTL" + idx2, currentPos)
                    .append("monto@TTL" + idx2, pMonto.toPlainString())
                    .append("descuento@TTL" + idx2, pDescuento.toPlainString())
                    .append("volumen@TTL" + idx2++, pVolumen.toPlainString());
        }
        idx = 1;
        for (DinamicVO<String, String> detalle : CorteDetalleDAO.getAditivosCorteActives(corte.NVL(CT_FIELDS.id.name()), parameters.NVL("isla"))) {
            tAditivos = tAditivos.add(new BigDecimal(detalle.NVL("IMPORTE")));
            cAditivos = cAditivos.add(new BigDecimal(detalle.NVL("ARTICULOS")));
            comprobante.append("ADT" + idx, detalle.NVL("ISLA"))
                    .append("ART@ADT" + idx, detalle.NVL("ARTICULOS"))
                    .append("IMP@ADT" + idx, detalle.NVL("IMPORTE"));
            idx++;
        }
        idx = 1;
        for (DinamicVO<String, String> detalle : ConsumosDAO.getVentasPorTipoCliente(corte.NVL(CT_FIELDS.id.name()), parameters.NVL("isla"))) {
            comprobante.append("DTLTP" + idx, detalle.NVL("tipodepago"))
                    .append("IMP@DTLTP" + idx, detalle.NVL("pesos"))
                    .append("DSC@DTLTP" + idx, detalle.NVL("descuento"))
                    .append("VOL@DTLTP" + idx, detalle.NVL("volumen"))
                    .append("CNT@DTLTP" + idx, detalle.NVL("ventas"));
            idx++;
        }
        idx = 1;
        for (DinamicVO<String, String> detalle : DepositoDAO.getDepositosCorte(corte.NVL(CT_FIELDS.id.name()), parameters.NVL("isla"))) {
            tDepositos = tDepositos.add(new BigDecimal(detalle.NVL("total"))).setScale(2, RoundingMode.HALF_EVEN);
            comprobante.append("DEP" + idx, detalle.NVL("id"))
                    .append("FECHA@DEP" + idx, detalle.NVL("fecha"))
                    .append("VEN@DEP" + idx, detalle.NVL("alias"))
                    .append("ISLA@DEP" + idx, detalle.NVL("isla_pos"))
                    .append("IMP@DEP" + idx, detalle.NVL("total"));
            idx++;
        }
        idx = 1;
        for (DinamicVO<String, String> efectivo : ConsumosDAO.getEfectivo(corte.NVL(CorteVO.CT_FIELDS.id.name()), parameters.NVL("isla"))) {
            comprobante.append("EFE" + idx, efectivo.NVL("isla_pos"))
                    .append("CORTE@EFE" + idx, efectivo.NVL("corte"))
                    .append("CONTADO@EFE" + idx, efectivo.NVL("contado"))
                    .append("DESCUENTO@EFE" + idx, efectivo.NVL("descuento"))
                    .append("DEPOSITO@EFE" + idx, efectivo.NVL("deposito"))
                    .append("EFECTIVO@EFE" + idx, efectivo.NVL("efectivo"));
            idx++;
        }

        comprobante.append("DEPTTL", tDepositos.toPlainString());
        comprobante.append("DEPCNT", String.valueOf(idx-1));

        if (!parameters.isNVL("isla")) {
          comprobante.append("ISLA", parameters.NVL("isla")); 
        }
        comprobante.append("ADITTL", tAditivos.toPlainString()).append("ADCTTL", cAditivos.toPlainString());
        comprobante.append("MTTL", tMonto.toPlainString()).append("VTTL", tVolumen.toPlainString());
        comprobante.append("DTTL", tDescuento.toPlainString());
        comprobante.append("GTTL", tMonto.add(tAditivos).subtract(tDescuento).toPlainString());
        comprobante.append(new Comprobante(corte, "ct_"));

        return comprobante;
    }//getComprobante
}//CorteParcialAction
