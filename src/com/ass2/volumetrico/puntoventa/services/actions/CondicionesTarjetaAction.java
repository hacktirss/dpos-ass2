package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.UnidadVO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import static com.ass2.volumetrico.puntoventa.services.actions.BaseAction.WS_PRMT_TARJETA;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.CollectionsUtils;
import java.text.ParseException;

public class CondicionesTarjetaAction extends SaldoAction {

    public static final String CVE_ACTIVADA     = "a";
    public static final String CVE_DESACTIVADA  = "d";

    public CondicionesTarjetaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }// Constructor

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TARJETA)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TARJETA"));
        }
        return this;
    }//validateRequest

    private DinamicVO<String, String> getUnidad(String tarjeta) throws DetiPOSFault {
        DinamicVO<String, String> unidad = new DinamicVO<>();
        UnidadesDAO.getUnidadV01(tarjeta);
        if (unidad.isVoid()) {
            throw new DetiPOSFault("Error consultando la tarjeta", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando la tarjeta", "La tarjeta no existe: " + tarjeta));
        }
        return unidad;
    }//getUnidad

    private DinamicVO<String, String> getCliente(String cliente) throws DetiPOSFault {
        DinamicVO<String, String> balance = new DinamicVO<>();
        try {
            balance = new DposActionFactory().getAnonymousAction(
                        DposActionFactory.ACTIONS.SALDO_CLIENTE,
                        new DinamicVO<>(CollectionsUtils.strToMap(BaseAction.WS_PRMT_CLIENTE + "=" + cliente))).getComprobante().getCampos();
        } catch (ParseException MURLE) {
            throw new DetiPOSFault("Error consultando el cliente", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, MURLE, "Consultando el saldo del cliente " + cliente));
        }
        if (balance.isVoid()) {
            throw new DetiPOSFault("Error consultando el cliente", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando el cliente", "El cliente no existe: " + cliente));
        }
        return balance;
    }//getCliente

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DinamicVO<String, String> unidad;
        DinamicVO<String, String> cliente;
        
        String tarjeta = parameters.NVL(WS_PRMT_TARJETA);

        unidad = getUnidad(tarjeta);
        cliente = getCliente(unidad.NVL(UnidadVO.UND_FIELDS.cliente.name()));

        return new Comprobante(cliente).append(new Comprobante(unidad));
    }//getComprobante
}//CondicionesTarjetaAction
