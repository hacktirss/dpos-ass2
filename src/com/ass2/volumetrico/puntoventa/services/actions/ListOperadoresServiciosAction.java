/*
 *  ListOperadoresServiciosAction
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

public class ListOperadoresServiciosAction extends BaseAction {

    public ListOperadoresServiciosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int[] i = { 1 };
        GycseDAO.getOperadoresServicios()
                .forEach((DinamicVO<String, String> om) -> 
                        comprobante.append("OM"+i[0], om.NVL("codigo")).append(new Comprobante(om, "OM"+i[0]++, "codigo", "")));
        return comprobante;
    }//getComprobante
}//ListOperadoresServiciosAction
