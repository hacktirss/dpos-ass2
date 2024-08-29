package com.ass2.volumetrico.puntoventa.services.actions;

import com.detisa.fae.DataServer;
import com.detisa.fae.Exception_Exception;
import com.detisa.fae.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.net.MalformedURLException;

public class ConsultaSaldoClienteAction extends BaseAction {

    public static enum BALANCE_FIELDS {

        NOMBRE,
        DIRECCION,
        COLONIA,
        MUNICIPIO,
        ALIAS,
        TELEFONO,
        ACTIVO,
        CONTACTO,
        OBSERVACIONES,
        TIPODEPAGO,
        LIMITE,
        RFC,
        CODIGO,
        CORREO,
        NUMEROEXT,
        NUMEROINT,
        ENVIARCORREO,
        CUENTABAN,
        ESTADO,
        FORMADEPAGO,
        CORREO2,
        PUNTOS,
        CIA,
        ABONOS,
        CARGOS,
        SALDO
    }

    public ConsultaSaldoClienteAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private DinamicVO<String, String> getBalance(String cliente) throws DetiPOSFault {
        return ClientesDAO.getClienteBalanceByID(cliente);
    }//getBalance

    private BigDecimal getSaldoCorporativo(String cliente) {
        try {
            LogManager.info("Consultando saldo del cliente en Corporativo. Cliente ID = " + cliente);
            DataServer port = RMIClientFactory.getDataServerPort(VariablesDAO.getCorporativo("url_sync_data"), "DataServer", "DataServer", 10000);
            return new BigDecimal(port.consultaSaldoCliente(String.valueOf(VariablesDAO.getIdFAE()), cliente));
        } catch (MalformedURLException | DetiPOSFault | Exception_Exception ex) {
            LogManager.info("Error consultando saldo del cliente en Corporativo. Cliente ID = " + cliente);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_CLIENTE)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro CLIENTE"));
        }//if
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DinamicVO<String, String> balanceCliente;
        String cliente = parameters.NVL(WS_PRMT_CLIENTE);

        balanceCliente = getBalance(cliente);
        if (balanceCliente.isVoid()) {
            throw new DetiPOSFault("No se encontro el cliente "+cliente, new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando cliente", "No se encontro el cliente " + cliente));
        }
        
        if ("1".equals(balanceCliente.NVL("CORPORATIVO"))) {
            balanceCliente.setField("SALDO", getSaldoCorporativo(cliente).toPlainString());
        }
        return new Comprobante(balanceCliente);
    }//getComprobante
}//ConsultaSaldoClienteAction
