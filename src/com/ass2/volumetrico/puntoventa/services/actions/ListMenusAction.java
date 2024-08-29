/*
 *  ListMenusAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.MenusDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public class ListMenusAction extends BaseAction {

    public ListMenusAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int[] i = { 1 };
        MenusDAO.getMenus()
                .forEach((DinamicVO<String, String> menu) -> 
                        comprobante.append("ID" + i[0], menu.NVL("ID")).append(new Comprobante(menu, "ID"+String.valueOf(i[0]++), "ID", "")));
        return comprobante;
    }//getComprobante
}//ListMenusAction
