package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.vo.BaseVO;
import java.util.List;

public class ConsultaClienteByRFCAction extends BaseAction {

    public static enum CLIENTE_FIELDS {

        ID,
        NOMBRE,
        TIPODEPAGO,
        RFC,
        FORMADEPAGO
    }

    public ConsultaClienteByRFCAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private List<BaseVO> getClientes(String rfc) throws DetiPOSFault {
        return ClientesDAO.getClienteByRFC(rfc);
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
        String rfc = parameters.NVL(WS_PRMT_CLIENTE);
        Comprobante comprobante = new Comprobante();
        int[] idx = { 1 };

        getClientes(rfc).forEach(item -> {
            comprobante.append(new Comprobante(item, "CL"+(idx[0]++), "ID", ""));
        });

        return comprobante;
    }//getComprobante
}//ConsultaTarjetaAction
