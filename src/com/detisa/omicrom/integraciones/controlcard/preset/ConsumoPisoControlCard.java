package com.detisa.omicrom.integraciones.controlcard.preset;

import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.detisa.omicrom.integraciones.controlcard.ws.RMIControlCardClientFactory;
import com.detisa.omicrom.integraciones.controlcard.ws.Autorizacion;
import com.detisa.omicrom.integraciones.controlcard.ws.ServicioSoap;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.MAX_WAITING_TIME;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_TYP;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import javax.xml.ws.WebServiceException;

public class ConsumoPisoControlCard extends Consumo {

    private Autorizacion balance;
    private final BaseVO interes = new ComandosVO();
    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    public ConsumoPisoControlCard() {
        super();
    }//Constructor

    private boolean execInteres() throws DetiPOSFault {
        LogManager.error(manguera);
        if (!interes.isVoid() && manguera.getCampoAsInt(ManguerasVO.DSP_FIELDS.enable.name())>0) {
            ComandosDAO.create((ComandosVO) interes);
            if (ComandosDAO.isExecuted(interes.NVL(ComandosVO.CMD_FIELDS.id.name()), "1", "1", MAX_WAITING_TIME)) {
                ManguerasDAO.updateFactor(getManguera().NVL("POSICION"), 
                        getManguera().NVL("MANGUERA"),
                        balance.getPrecioUnitario().multiply(new BigDecimal(100)).toPlainString());
            }
        }//is interes
        return true;
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
    }//init

    @Override
    public boolean exec() throws DetiPOSFault {
        boolean ejecutado = execInteres() && super.exec();

        if (!ejecutado) {
            cancelarCC();
        }
        return ejecutado;
    }//exec

    @Override
    public boolean validate() throws DetiPOSFault {
        ServicioSoap port;
        boolean validated = super.validate();
        try {
            port = RMIControlCardClientFactory.getDataServerPort(VariablesDAO.getCorporativo("cc_endpoint"), "Servicio", "ServicioSoap", 10000);
            balance = port.solicitaAutorizacion(parameters.NVL(PRMT_ACCOUNT),
                    parameters.isNVL(PRMT_EMPLOYEE) ? parameters.NVL(PRMT_PIN_ACCOUNT) : parameters.NVL(PRMT_PIN_EMPLOYEE),
                    parameters.isNVL(PRMT_EMPLOYEE) ? parameters.NVL(PRMT_ACCOUNT) : "",
                    Integer.parseInt(parameters.NVL(PRMT_CNS_POS)),
                    Integer.parseInt(parameters.NVL(PRMT_CNS_MNG)),
                    isImporte() || isLleno() ? this.getImporte() : BigDecimal.ZERO,
                    isVolumen() ? this.getCant() : BigDecimal.ZERO,
                    Integer.parseInt(parameters.NVL(PRMT_CNS_FP)));

            BigDecimal montoAutorizado;
            BigDecimal volumenAutorizado;

            volumenAutorizado = balance.getVolumenAutorizado();
            montoAutorizado = balance.getImporteAutorizado();

            if (BigDecimal.ZERO.compareTo(volumenAutorizado)==0
                    && BigDecimal.ZERO.compareTo(montoAutorizado)==0) {
                throw new DetiPOSFault(balance.getMensaje());
            }
            
            if (montoAutorizado.compareTo(CARGA_MINIMA)<=0) {
                throw new DetiPOSFault("El importe autorizado es menor a la carga minima definida en " + CARGA_MINIMA + " pesos");
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

            LogManager.info(balance);
            if (balance.getPrecioUnitario()!=null && BigDecimal.ZERO.compareTo(balance.getPrecioUnitario())<=0) {
                LogManager.info(balance);
                interes.setEntries(ComandosVO.interes(getManguera(), balance.getPrecioUnitario().multiply(new BigDecimal(100)).toPlainString()));
            }
            LogManager.error(interes);

            comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), balance.getIdTransito() + "|" + parameters.NVL(PRMT_ACCOUNT), false);
            comando.setField(ComandosVO.CMD_FIELDS.idtarea, String.valueOf(balance.getIdTransito()));
            LogManager.error(comando);
            posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, balance.getIdTransito() + "|" + parameters.NVL(PRMT_ACCOUNT));
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        } catch (MalformedURLException ex) {
            throw new DetiPOSFault("ERROR DE CONEXION " + ex.getMessage());
        }
    
        return validated;
    }

    public boolean cancelarCC() throws DetiPOSFault {
        ServicioSoap port;
        try {
            port = RMIControlCardClientFactory.getDataServerPort(VariablesDAO.getCorporativo("url_sync_data"), "Servicio", "ServicioSoap", 10000);
            port.ventaCancelada(balance.getIdTransito(), false);
            return true;
        } catch (WebServiceException wsdle) {
            throw new DetiPOSFault("NO HAY ACCCESO AL SERVICIO " + wsdle.getMessage());
        } catch (MalformedURLException ex) {
            throw new DetiPOSFault("ERROR DE CONEXION " + ex.getMessage());
        }
    }
}//ConsumoPisoControlCard
