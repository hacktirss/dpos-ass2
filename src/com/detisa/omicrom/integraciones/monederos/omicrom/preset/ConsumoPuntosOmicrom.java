package com.detisa.omicrom.integraciones.monederos.omicrom.preset;

import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.EstacionDAO;
import com.detisa.omicrom.integraciones.ConsumoIntegracion;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import java.math.BigDecimal;

public class ConsumoPuntosOmicrom extends ConsumoIntegracion {

    private ClientesVO monedero;
    private DinamicVO<String, String> estacion;
    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    public ConsumoPuntosOmicrom() {
        super();
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        monedero = ClientesDAO.getClienteAutorizador(parameters.NVL(Consumo.PRMTR_APP_NAME));
        estacion = EstacionDAO.getDatosEstacion();
    }//init

    @Override
    public boolean validate() throws DetiPOSFault {
        return false;
    }
}