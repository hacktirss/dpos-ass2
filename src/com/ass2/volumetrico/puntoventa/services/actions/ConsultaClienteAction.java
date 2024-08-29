package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;

public class ConsultaClienteAction extends BaseAction {

    public static enum CLIENTE_FIELDS {

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
        CIA
    }

    public ConsultaClienteAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private DinamicVO<String, String> getCliente(String cliente) throws DetiPOSFault {
        return ClientesDAO.getClienteByID(cliente);
    }//getCliente

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
        DinamicVO<String, String> cliente;
        String clienteID = parameters.NVL(WS_PRMT_CLIENTE);

        cliente = getCliente(clienteID);
        LogManager.debug(cliente);
        if (cliente.isVoid() || cliente.isNVL("ID")) {
            throw new DetiPOSFault("No existe el Cliente", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "El Cliente no existe", clienteID));
        }
        return new Comprobante(cliente, "CLI_");
    }//getComprobante
}//ConsultaTarjetaAction
