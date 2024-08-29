package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromVO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.UnidadVO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import java.math.BigDecimal;

/**
 *
 * @author ROLANDO
 */
public class ConsultaSaldoTarjetaAction extends BaseAction {

    public static final String PATTERN_CODIGO = "([0-9]{19,20})";
    public static final String PATTERN_TARJETA = "[0-9a-fA-F]{8}";

    public ConsultaSaldoTarjetaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();

        if (parameters.isNVL(WS_PRMT_TARJETA)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TARJETA"));
        }//if
        if (!parameters.NVL(WS_PRMT_TARJETA).matches(PATTERN_TARJETA) && !parameters.NVL(WS_PRMT_TARJETA).matches(PATTERN_CODIGO)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No es una TARJETA valida"));
        }

        return this;
    }//validateRequest

    private DinamicVO<String, String> getUnidad(String tarjeta) throws DetiPOSFault {
        DinamicVO<String, String> unidad = new OmicromVO();
        unidad.setEntries(UnidadesDAO.getUnidadV01(tarjeta));
        if (unidad.isVoid()) {
            throw new DetiPOSFault("Error consultando la tarjeta", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando la tarjeta", "La tarjeta no existe: " + tarjeta));
        }
        return unidad;
    }

    private DinamicVO<String, String> getSaldoUnidad(String tarjeta) throws DetiPOSFault {
        DinamicVO<String, String> saldo = new OmicromVO();
        saldo.setEntries(ConsumosDAO.getSaldoTarjeta(tarjeta));
        if (saldo.isVoid()) {
            throw new DetiPOSFault("Error consultando la tarjeta", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando la tarjeta", "La tarjeta no existe: " + tarjeta));
        }
        return saldo;
    }

    private DinamicVO<String, String> getCliente(String cliente) throws DetiPOSFault {
        DinamicVO<String, String> balance = ClientesDAO.getClienteBalanceByID(cliente);
        if (balance.isVoid()) {
            throw new DetiPOSFault("Error consultando el cliente", 
                    new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando el cliente", "El cliente no existe: " + cliente));
        }
        return balance;
    }//getCliente

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DinamicVO<String, String> unidad;
        DinamicVO<String, String> permitido;
        DinamicVO<String, String> balance;

        String tarjeta = parameters.NVL(WS_PRMT_TARJETA);

        unidad = getUnidad(tarjeta);
        permitido = getSaldoUnidad(unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()));
        balance = getCliente(unidad.NVL(UnidadVO.UND_FIELDS.cliente.name()));

        boolean insuficiente = new BigDecimal(balance.NVL("SALDO")).compareTo(new BigDecimal(permitido.NVL("permitido"))) < 0;
        return new Comprobante()
                .append("CLI", balance.NVL("ID"))
                .append("NOMBRE", balance.NVL("NOMBRE"))
                .append("CODIGO", unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()))
                .append("SALDO_TARJETA", insuficiente ? balance.NVL("SALDO") : permitido.NVL("permitido"))
                .append("SALDO_CLIENTE", balance.NVL("SALDO"));
    }
}
