package com.detisa.omicrom.integraciones.monederos.gasngo.preset;

import com.detisa.integrations.gasngo.Event;
import com.detisa.integrations.gasngo.ObjectFactory;
import com.detisa.integrations.gasngo.service.GasngoServer;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.commons.pattern.Observer;
import com.softcoatl.integration.ServiceLocator;
import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;

public class ConsumoTAGGasngo extends ConsumoGasngo implements Observer {

    public ConsumoTAGGasngo() throws DetiPOSFault {
        super();
    }

    @Override
    protected void requestAuthorization() throws DetiPOSFault {
        GasngoServer api = (GasngoServer) ServiceLocator.getInstance().getService(GasngoServer.NAME);
        api.register(this);

        try {
            Event auth = ObjectFactory.createAuthEvent(parameters.NVL(PRMT_ACCOUNT), Integer.parseInt(parameters.NVL(PRMT_PIN_ACCOUNT)));
            auth.setNozzles(nozzles);
            api.send(auth);
            waitResponse();
        } catch (DatatypeConfigurationException | IOException ex) {
            throw new DetiPOSFault("Error invocando Servidor GNG. " + ex.getMessage());
        } finally {
            api.unregister(this);
        }
    }
}