/*
 * ConsumoMarcado
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.IslaDAO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_ECO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public class ConsumoMarcado extends Consumo {

    public ConsumoMarcado() {
        super();
    }//Constructor

    @Override
    public boolean exec() throws DetiPOSFault {
        EstadoPosicionesDAO.updateStatus(posicion.NVL(EstadoPosicionesVO.EP_FIELDS.posicion.name()), 
                                         posicion.NVL(EstadoPosicionesVO.EP_FIELDS.codigo.name(), "."), 
                                         posicion.NVL(EstadoPosicionesVO.EP_FIELDS.kilometraje.name()),
                                         parameters.NVL(PRMT_CNS_ECO, ""));
        return true;
    }//exec

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        populate(parameters);
        posicion = EstadoPosicionesDAO.getByPosicion(parameters.NVL(PRMT_CNS_POS));
        isla = IslaDAO.getIslaByID("1");
    }
}//ConsumoMarcado
