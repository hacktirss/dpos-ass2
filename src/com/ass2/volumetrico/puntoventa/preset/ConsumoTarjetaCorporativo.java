/*
 * ConsumoPublico
 * ASS2PuntoVenta®
 * © 2018, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2018
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.detisa.fae.CheckBalance;
import com.detisa.fae.DataServer;
import com.detisa.fae.Exception_Exception;
import com.detisa.fae.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.UnidadVO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSDataBaseFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSInternalFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import javax.xml.ws.WebServiceException;

public class ConsumoTarjetaCorporativo extends Consumo {

    private final BaseVO unidad;
    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    public ConsumoTarjetaCorporativo() {
        super();
        unidad = new UnidadVO();
    }//Constructor

    private void initUnidad() throws DetiPOSFault {
        unidad.setEntries(UnidadesDAO.getUnidadV01(parameters.NVL(PRMT_ACCOUNT)));
    }//initUnidad

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        initUnidad();
    }//init

    @Override
    public boolean exec() throws DetiPOSFault {
        return super.exec();
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        DataServer port;
        int despachoActual;
        try {

            if (unidad.isVoid() || unidad.getCampoAsInt("SIMULTANEO")==0) {
                despachoActual = EstadoPosicionesDAO.checkCardCode(parameters.NVL(PRMT_ACCOUNT));
                if (despachoActual!=0) {
                    throw new DetiPOSFault(ERR_PRESET, new DetiPOSDataBaseFaultInfo("Codigo en uso", "El codigo se encuentra consumiendo en la posicion " + despachoActual));
                }
            }

            port = RMIClientFactory.getDataServerPort(VariablesDAO.getCorporativo("url_sync_data"), "DataServer", "DataServer", 10000);
            CheckBalance balance = port.validaConsumo(
                            VariablesDAO.getIdFAE(), 
                            parameters.NVL(PRMT_ACCOUNT), 
                            parameters.NVL(PRMT_PIN_ACCOUNT), 
                            getImporte().doubleValue(), 
                            getVolumen().doubleValue(), 
                            getCombustible().NVL("ID"));

            BigDecimal montoAutorizado;
            BigDecimal volumenAutorizado;

            if ("L".equals(balance.getTipoMonto())) {

                volumenAutorizado = BigDecimal.valueOf(balance.getMontoAutorizado());
                montoAutorizado = volumenAutorizado.multiply(combustible.getCampoAsDecimal("PRECIO"), new MathContext(2, RoundingMode.HALF_EVEN));
            } else {

                montoAutorizado = BigDecimal.valueOf(balance.getMontoAutorizado());
                volumenAutorizado = montoAutorizado.divide(combustible.getCampoAsDecimal("PRECIO"), new MathContext(2, RoundingMode.HALF_EVEN));
            }

            if (montoAutorizado.compareTo(CARGA_MINIMA)<=0) {
                throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("El importe autorizado es menor a la carga minima definida en " + CARGA_MINIMA + " pesos"));
            }

            if (isLleno()) {
                transform("IMPORTE", montoAutorizado.toPlainString());
            } else {
                if (isImporte() && montoAutorizado.compareTo(getImporte())<0) {
                    transform("IMPORTE", montoAutorizado.toPlainString());
                } else if (isVolumen() && montoAutorizado.compareTo(getImporte())<0) {
                    transform("VOLUMEN", volumenAutorizado.toPlainString());
                }
            }//true
        } catch (WebServiceException | MalformedURLException | DetiPOSFault | Exception_Exception ex) {
            LogManager.info("Error solicitando autorización a Corporativo");
            LogManager.error(ex);
            LogManager.info("Trace", ex);
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSInternalFaultInfo(ex, "Error de conectividad solicitando autorizacion a Corporativo"));
        }

        comando.setField(ComandosVO.CMD_FIELDS.idtarea.name(), "0");
    
        return super.validate();
    }
}
