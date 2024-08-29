/*
 *  ListTarjetasFlotillasAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.BancosDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public class ListTarjetasFlotillasAction extends BaseAction {

    public ListTarjetasFlotillasAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int[] i = { 1 };
        BancosDAO.getBancos().stream()
                .forEach((DinamicVO<String, String> ban)-> {
                    comprobante.append(new Comprobante(ban, "BAN"+String.valueOf(i[0]++), "id", ""));
                });
        return comprobante;
    }
}
