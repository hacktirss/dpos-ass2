
package com.detisa.omicrom.integraciones.monederos.gasngo;

import com.detisa.integrations.gasngo.service.GasngoServer;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.integration.ServiceLocator;
import lombok.Getter;

public class GasngoApi {

    @Getter private final String wallet;
    @Getter GasngoServer server;

    public GasngoApi(String wallet) throws DetiPOSFault {
        this.wallet = wallet;
        configure();
    }
    
    private void configure() throws DetiPOSFault {
        server = (GasngoServer) ServiceLocator.getInstance().getService(GasngoServer.NAME);
    }
}
