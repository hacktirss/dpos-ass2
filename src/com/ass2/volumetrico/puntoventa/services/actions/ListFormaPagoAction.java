/*
 *  ListFormaPagoAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.CFDICatalogosDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public class ListFormaPagoAction extends BaseAction {

    public ListFormaPagoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        CFDICatalogosDAO.getFormaPago()
                .forEach((DinamicVO<String, String> ban) -> comprobante.append(ban.NVL("clave"), ban.NVL("descripcion")));
        return comprobante;
    }//getComprobante
}//ListFormaPagoAction
