/*
 *  ListTarjetasAction
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

public class ListTarjetasAction extends BaseAction {

    public ListTarjetasAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        BancosDAO.getBancos().stream()
                .forEach((DinamicVO<String, String> banco)-> comprobante.append(banco.NVL("id"), banco.NVL("alias")));
        return comprobante;
    }
}//ListTarjetasAction
