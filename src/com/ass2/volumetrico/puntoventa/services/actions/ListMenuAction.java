/*
 *  ListMenuAction
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

public class ListMenuAction extends BaseAction {

    public ListMenuAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int[] i = { 1 };
        MenusDAO.getMenuList().stream()
                .forEach((DinamicVO<String, String> menu)-> 
                    comprobante.append("MENU"+i[0], menu.NVL("id")).append(new Comprobante(menu, "MENU"+String.valueOf(i[0]++), "id", "")));
        return comprobante;
    }
}//ListMenuAction
