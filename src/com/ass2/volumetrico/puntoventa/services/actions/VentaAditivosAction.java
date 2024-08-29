/**
 * ConsumoAction DetiPOS WEB Service
 * Card based transaction, validates card id, costumer credit, vehicle conditions and sets dispenser command
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since May 2014
 */
package com.ass2.volumetrico.puntoventa.services.actions;

import com.detisa.commons.TransactionEncoder;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.VentaAditivosVO;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.preset.ConsumoAditivos;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;

public class VentaAditivosAction extends BaseAction {

    private final Consumo consumo;

    public VentaAditivosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        consumo = new ConsumoAditivos();
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(ConsumoAditivos.PRMT_CNS_POS)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro POSICION"));
        }
        if (parameters.isNVL(ConsumoAditivos.PRMT_CLAVE_ADITIVO) && parameters.isNVL(ConsumoAditivos.PRMT_CODIGO_ADITIVO)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro CLAVE ADITIVO"));
        }
        if (parameters.isNVL(ConsumoAditivos.PRMT_CNS_CNT)) {
            throw new DetiPOSFault(new DetiPOSParametersFaultInfo("Se esperaba el parametro UNIDADES"));
        }
        return this;
    }

    private boolean executeConsumo() throws DetiPOSFault {
        consumo.init(parameters);
        return consumo.validate() && consumo.exec();
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        if (executeConsumo()) {
            VentaAditivosVO vtaditivo = ((ConsumoAditivos) consumo).getVentaAditivo();
            vtaditivo.setField("FOLIO_FAE", TransactionEncoder.base36Encode(vtaditivo.NVL("FOLIO_FAE")).replaceAll("(.{6})", "$1-"));
            vtaditivo.setField("QRC_FAE", vtaditivo.NVL("QRC_FAE") + vtaditivo.NVL("FOLIO_FAE"));
            LogManager.debug(vtaditivo);
            Comprobante comprobante = super.getComprobante()
                                        .append(new Comprobante(vtaditivo))
                                        .append(new Comprobante(((ConsumoAditivos) consumo).getCliente(), "CLI_"))
                                        .append(new Comprobante(((ConsumoAditivos) consumo).getUnidad(), "CC_"))
                                        .append("TR", vtaditivo.NVL("id"));
            if (!"NO PIDE".equals(((ConsumoAditivos) consumo).getVen().NVL("nip"))) {
                comprobante.append("DESPACHADOR", ((ConsumoAditivos) consumo).getVen().NVL("NOMBRE"));
            }
            return comprobante;
        }
        return super.getComprobante();
    }
}
