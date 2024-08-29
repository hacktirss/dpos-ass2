/*
 * ConsumoBoleto
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.ass2.volumetrico.puntoventa.data.BoletosDAO;
import com.ass2.volumetrico.puntoventa.data.BoletosVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.actions.BaseAction;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public class ConsumoBoleto extends Consumo {

    public static class BoletosComparator implements Comparator <BoletosVO> {

        @Override
        public int compare(BoletosVO object1, BoletosVO object2) {
            String id = BoletosVO.B_FIELDS.idnvo.name();
            return Integer.valueOf(object1.NVL(id)).compareTo(Integer.valueOf(object2.NVL(id)));
        }
    }

    @Getter protected final List <BoletosVO> boletos = new ArrayList <> ();
    @Getter protected List <String> requestedBoletos;

    public ConsumoBoleto() {
        super();
        setImporte(new BigDecimal(0));
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        requestedBoletos = filterRequestedBoletos(parameters.NVL(PRMT_ACCOUNT));
        super.init(parameters);
    }

    
    private void queryBoleto(String codigo) {
        BoletosVO boleto;
        BigDecimal importe;
        BigDecimal importeCargado;
        BigDecimal saldo;
            
        try {
            LogManager.info("Consultando información del boleto "+ BaseAction.WS_PRMT_BOLETO + "=" + codigo);
            boleto = new BoletosVO(BoletosDAO.getBoletoByID(codigo));
            importe = new BigDecimal(boleto.NVL(BoletosVO.B_FIELDS.importe, "0"));
            importeCargado = new BigDecimal(boleto.NVL(BoletosVO.B_FIELDS.importecargado, "0"));
            saldo = importe.subtract(importeCargado);
            LogManager.info(String.format("Información del boleto %s, importe total %s, importe cargado %s, saldo %s", codigo, importe.toPlainString(), importeCargado.toPlainString(), saldo.toPlainString()));
            if ("Si".equals(boleto.NVL(BoletosVO.B_FIELDS.vigente, "Si")) && saldo.doubleValue()>0) {
                setImporte(saldo.add(getImporte()));
                boletos.add(boleto);
            }
        } catch (DetiPOSFault ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }

    private List<String> filterRequestedBoletos(String sBoletos) {
        return Arrays.asList(sBoletos.split("[|]")).stream()
                            .distinct()
                            .map(item -> item.replaceAll("[\\t\\n\\r]+", ""))
                            .filter(item -> item.matches("\\d{10,13}"))
                            .collect(Collectors.toList());
    }

    @Override
    protected void determineImporte() throws DetiPOSFault {
        requestedBoletos.forEach(this::queryBoleto);
        Collections.sort(boletos, new BoletosComparator());
        parameters.setField(PRMT_CNS_TYP, ComandosVO.TIPO_CONSUMO.IMPORTE.name());
        parameters.setField(PRMT_ACCOUNT, boletos.stream().map(item -> item.NVL("CODIGO")).collect(Collectors.joining("|")));
        parameters.setField(PRMT_CNS_CNT, getImporte().toPlainString());
        LogManager.info("Valid codes " + parameters.NVL(PRMT_ACCOUNT));
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        super.validate();

        if (getImporte().doubleValue()<=0) {
            throw new DetiPOSFault("Error estableciendo el consumo", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error en el codigo", "No se encontraron boletos validos."));
        }
        return true;
    }
}
