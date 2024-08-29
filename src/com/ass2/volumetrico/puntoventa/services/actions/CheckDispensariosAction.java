/*
 *  CheckDispensariosAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.PosicionesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import java.util.stream.Collectors;

public class CheckDispensariosAction extends BaseAction {

    public CheckDispensariosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        String d = PosicionesDAO.listDisconnected("1").stream()
                    .map(disconnected -> disconnected.NVL("posicion"))
                    .collect(Collectors.joining(","));
        return new Comprobante().append("STATUS", d.isEmpty() ? "OK" : d);
    }//getComprobante
}//CheckDispensariosAction
