/**
 * ConsumoAction DetiPOS WEB Service
 * Card based transaction, validates card id, costumer credit, vehicle conditions and sets dispenser command
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 * Modification log
 * Nov 11, 2015 REV@Softcoatl Authorization as a separate service, it could be local or remote
 */
package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesDAO;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesUpdater;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.pattern.ComandoObserver;
import com.ass2.volumetrico.puntoventa.pattern.ComandoSubject;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.preset.ConsumoCliente;
import com.ass2.volumetrico.puntoventa.preset.ConsumoMarcado;
import com.ass2.volumetrico.puntoventa.preset.ConsumoPublico;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import java.math.BigDecimal;

public class ConsumoAction extends ConsumoActionBase {

    private boolean marcado = false;

    public ConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        if (parameters.isNVL(PRMT_ACCOUNT) && parameters.isNVL(PRMT_PIN_ACCOUNT) && parameters.isNVL(PRMT_CNS_KM)) {
            parameters.setField(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID));
            marcado = VariablesDAO.getCorporativo("pos_presetbancos", "1").equals("0");
            consumo = marcado ? new ConsumoMarcado() : new ConsumoPublico();
        } else {
            consumo = new ConsumoCliente();
        }
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();

        if (parameters.isNVL(PRMT_CNS_TYP)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro TIPO DE CONSUMO"));
        }
        ComandosVO.TIPO_CONSUMO.validate(parameters.NVL(PRMT_CNS_TYP));
        if (!marcado && (!ComandosVO.TIPO_CONSUMO.LLENO.toString().equals(parameters.NVL(PRMT_CNS_TYP)) && parameters.isNVL(PRMT_CNS_CNT))) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro CANTIDAD"));
        }
        return this;
    }

    protected void initObserver() {
        ComandoSubject comando = new ComandoSubject(consumo);
        ComandoObserver observer = new ComandoObserver(new AutorizacionesUpdater().setUnidad(((ConsumoCliente) consumo).getUnidad()));
        comando.register(observer);
        comando.initConsumo();
    }

    private void logAutorizacion(ConsumoCliente consumo, String detail) {
        AutorizacionesDAO.autorizacion(
                consumo.getCliente().getCampoAsInt("ID"), 
                consumo.getUnidad().NVL("CODIGO"), 
                consumo.getClaveTipo(),
                ( ComandosVO ) consumo.getComando(), 
                consumo.isLleno() ? IMPORTE_MAXIMO : new BigDecimal(consumo.getParameters().NVL(PRMT_CNS_CNT)),
                consumo.getImporte(),
                detail);
    }

    private void logRechazo(ConsumoCliente consumo, String motivo) {
        AutorizacionesDAO.rechazo(
                consumo.getCliente().getCampoAsInt("ID"), 
                consumo.getUnidad().NVL("CODIGO"), 
                consumo.getClaveTipo(),
                consumo.isLleno() ? IMPORTE_MAXIMO : new BigDecimal(consumo.getParameters().NVL(PRMT_CNS_CNT)),
                motivo);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        try {
            Comprobante comprobante = super.getComprobante();
            if (executed) {
                if (marcado) {
                    comprobante.append("PRESET", "VENTA AUTORIZADA INGRESE EL IMPORTE EN EL DISPENSARIO"); 
                } else if (consumo instanceof ConsumoCliente) {
                    comprobante.append("PRESET", comprobante.getCampos().NVL("PRESET") + " PLACAS " + ((ConsumoCliente) consumo).getUnidad().NVL("PLACAS")); 
                    logAutorizacion((ConsumoCliente) consumo, comprobante.getCampos().NVL("PRESET"));
                }
            }
            return comprobante;
        } catch (DetiPOSFault dpf) {
            if (consumo instanceof ConsumoCliente) {
                logRechazo((ConsumoCliente) consumo, dpf.getFaultInfo().getErrorDetail());
            }
            throw dpf;
        }
    }
}
