/*
 * SaldoAction
 * ASS2PuntoVenta®
 * ® 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones.monederos.puntovales;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.*;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.detisa.omicrom.puntogas.vales.api.PuntoGasValesException;
import com.detisa.omicrom.puntogas.vales.autenticacion.AutenticacionResponse;
import com.detisa.omicrom.puntogas.vales.consulta.ConsultaValesResponse;
import com.ass2.volumetrico.puntoventa.services.actions.BaseAction;
import com.ass2.volumetrico.puntoventa.services.actions.DetiPOSAction;
import com.softcoatl.utils.logging.LogManager;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class SaldoAction extends BaseAction {

    public static final String CVE_ACTIVO = "Si";
    public static final int MAX_WAITING_TIME = 6000; //Seconds

    private PuntoGasValesApi puntoValesApi;

    public SaldoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
        puntoValesApi = new PuntoGasValesApi(parameters.NVL(Consumo.PRMTR_APP_NAME));
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(PRMT_ACCOUNT)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro NUMERO DE CUENTA"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = super.getComprobante();
        try {
            AutenticacionResponse auth = puntoValesApi.authenticate();
            List<String> vales = Arrays.asList(parameters.NVL(PRMT_ACCOUNT).split("[|]"));
            ConsultaValesResponse response = puntoValesApi.request(
                    auth.getToken(),
                    vales,
                    "",
//                    parameters.NVL(PRMT_PIN_ACCOUNT),
                    0);
            BitacoraIntegracionesDAO.evento(parameters.NVL(Consumo.PRMTR_APP_NAME), parameters.NVL(PRMT_ACCOUNT), "REQUEST", response);
            comprobante
                    .append("API", "YENA")
                    .append("TIPO", "IMPORTE")
                    .append("TKEY", "I")
                    .append("SALDO", response.getSaldoAutorizado().toPlainString());
        } catch (NoSuchAlgorithmException | PuntoGasValesException ex) {
            LogManager.error(ex);
        }
        return comprobante;
    }
}
