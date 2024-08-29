package com.detisa.omicrom.integraciones.monederos.gasngo.preset;

import com.detisa.integrations.gasngo.Event;
import com.detisa.integrations.gasngo.Notificacion;
import com.detisa.integrations.gasngo.ObjectFactory;
import com.detisa.integrations.gasngo.Preset;
import com.detisa.integrations.gasngo.Products;
import com.detisa.integrations.gasngo.service.GasngoEvent;
import com.detisa.integrations.gasngo.service.GasngoServer;
import com.ass2.volumetrico.puntoventa.data.BitacoraIntegracionesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.detisa.omicrom.integraciones.monederos.gasngo.CombustibleGasngo;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.commons.pattern.Observer;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.integration.ServiceLocator;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.GenericSleeper;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;

public abstract class ConsumoGasngo extends ConsumoIntegracion implements Observer {

    public static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    protected ClientesVO monedero;
    protected Calendar init;
    
    @Getter protected List<String> nozzles;
    @Getter protected Notificacion autorizacion;
    @Getter protected Notificacion notification;

    protected boolean waiting = true;

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        LogManager.info(parameters);
        monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
        nozzles = Arrays.asList(StringUtils.fncsLeftPadding(manguera.NVL(ManguerasVO.DSP_FIELDS.posicion.name()), '0', 2) 
                    + CombustibleGasngo.mapping(combustible.NVL(CombustibleVO.COM_FIELDS.clave.name())));
    }

    private boolean timeOut() {
        return (90*DateUtils.MILIS_POR_SEGUNDO)<(Calendar.getInstance().getTimeInMillis()-init.getTimeInMillis());
    }

    protected void waitResponse() throws DetiPOSFault {
        init = Calendar.getInstance();
        waiting = true;
        do {
            GenericSleeper.get().setTimeout(10).sleep();
            if (timeOut()) {
                throw new DetiPOSFault("Sin respuesta de la Integración");
            }
        } while(waiting);
    }

    @Override
    public String getAutorizadoTexto() {
        if (isLleno()) {
            return "TANQUE LLENO";
        } else if (isImporte()) {
            return getCant().setScale(2, RoundingMode.HALF_EVEN).toPlainString() + " PESOS";
        } else {
            return getCant().setScale(3, RoundingMode.HALF_EVEN).toPlainString() + " LITROS";
        }
    }
 
    protected abstract void requestAuthorization() throws DetiPOSFault;

    @Override
    public boolean validate() throws DetiPOSFault {

        try {
            super.validate();
            requestAuthorization();
            if (!notification.isAuthorizacion()) {
                throw new DetiPOSFault(notification.getType().detail());
            } else if (autorizacion.getPreset().getAuthorized()) {
                Preset preset = autorizacion.getPreset();
                if (new BigDecimal(preset.getAuthorizedMaxTransactionCost()).compareTo(getImporte())<0) {
                    transform("IMPORTE", String.valueOf(preset.getAuthorizedMaxTransactionCost()));
                }
                if (!preset.getAuthorizedProduct().contains(Products.Any) 
                        && !preset.getAuthorizedProduct().contains(Products.fromValue(combustible.getCampoAsInt(CombustibleVO.COM_FIELDS.clave.name())))) {
                    throw new DetiPOSFault("Combustible no permitido: " + combustible.NVL(CombustibleVO.COM_FIELDS.descripcion.name()));
                }

                if (getImporte().compareTo(CARGA_MINIMA)<=0) {
                    throw new DetiPOSFault("El importe autorizado es menor a la carga minima definida en " + CARGA_MINIMA + " pesos");
                }

                comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), "Consumo Gasngo", false);
                comando.setField(ComandosVO.CMD_FIELDS.idtarea, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
                LogManager.debug(comando);
                posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, monedero.NVL(ClientesVO.CLI_FIELDS.id.name()));
                return true;
            }
            throw new DetiPOSFault(autorizacion.getPreset().getAuthorizationFailureReason().detail());
        } catch (DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento("GASNGO", parameters.NVL(PRMT_ACCOUNT), "GASNGO EXCEPTION", ex.getMessage());
            if (autorizacion!=null) {
                cancel();
            }
            throw ex;
        }
    }

    @Override
    public boolean cancel() {
        GasngoServer api = (GasngoServer) ServiceLocator.getInstance().getService(GasngoServer.NAME);
        api.register(this);
        try {
            Event cancel = ObjectFactory.createCancelEvent(autorizacion.getGngID());
            cancel.setNozzles(nozzles);
            cancel.setProductPrice(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN));
            api.send(cancel);
            waitResponse();
        } catch (DatatypeConfigurationException | IOException | DetiPOSFault ex) {
            BitacoraIntegracionesDAO.evento("GASNGO", parameters.NVL(PRMT_ACCOUNT), "GASNGO EXCEPTION", ex.getMessage());
            LogManager.error(ex);
        } finally {
            api.unregister(this);
        }
        return super.cancel();
    }

    @Override
    public void notify(com.softcoatl.commons.events.Event event) {
        if (event instanceof GasngoEvent) {
            notification = ((GasngoEvent) event).getNotification();
            BitacoraIntegracionesDAO.evento(GasngoServer.NAME, parameters.NVL(PRMT_ACCOUNT), notification.getType().detail().toUpperCase(), notification);
            if (notification.isAuthorizacion()) {
                autorizacion = notification;
            }
            waiting = false;
        }
    }
}