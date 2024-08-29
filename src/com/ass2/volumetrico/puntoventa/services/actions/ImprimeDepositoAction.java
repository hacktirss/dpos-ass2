package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.DepositoDAO;
import com.ass2.volumetrico.puntoventa.data.DepositoVO;
import com.ass2.volumetrico.puntoventa.data.IslaDAO;
import com.ass2.volumetrico.puntoventa.data.IslaVO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;

public class ImprimeDepositoAction extends BaseAction {

    public static final String WS_DEP_CTID = "corteID";

    private final VendedoresVO despachador = new VendedoresVO();

    private final IslaVO isla;

    public ImprimeDepositoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        this.isla = new IslaVO(IslaDAO.getIslaByID("1"));
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_DESP)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro VENDEDOR"));
        }
        if (parameters.isNVL(WS_PRMT_AUTH)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro PASSWORD"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DepositoVO deposito;
        Comprobante comprobante = super.getComprobante();
        try {
            despachador.setEntries(VendedoresDAO.get(parameters.NVL("despachador")));
            if ("NO EXISTE".equals(despachador.NVL("NOMBRE"))) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Despachador incorrecto"));
            }
            if (!"NO PIDE".equals(despachador.NVL("NIP")) && !despachador.NVL("nip").equals(parameters.NVL("authenticate"))) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Password incorrecto"));
            }
            deposito = DepositoDAO.getDeposito(
                    parameters.NVL("despachador"), 
                    isla.NVL(IslaVO.ISL_FIELDS.corte.name()));
            if (deposito.isVoid()) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error de Parametros", "No se encontro del Deposito"));
            }
        } catch (DBException ex) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error de Parametros", "Error recuperando el Deposito"));
        }
        comprobante.append(new Comprobante(despachador, "ven_"));
        comprobante.append(new Comprobante(deposito, "dep_"));
        comprobante.append("dep_ID", deposito.NVL(DepositoVO.DEP_FIELDS.id.name()));
        return comprobante;
    }
}