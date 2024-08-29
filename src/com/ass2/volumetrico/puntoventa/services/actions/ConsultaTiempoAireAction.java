/*
 *  ConsultaTiempoAireAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.GycseDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public class ConsultaTiempoAireAction extends TiempoAireAction {

    public ConsultaTiempoAireAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return new Comprobante(estacion).append(new Comprobante(GycseDAO.getOperacion(parameters.NVL(TiempoAireAction.PRMTR_TRANSACCION))));
    }//getComprobante
}//ConsultaTiempoAireAction
