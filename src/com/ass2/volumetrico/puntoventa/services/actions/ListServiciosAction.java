/*
 *  ListServiciosAction
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

public class ListServiciosAction extends BaseAction {

    public static final String PRMTR_CODIGO_OPER = "CODIGO_OPER";
    public ListServiciosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int[] i = { 1 };
        GycseDAO.getServicios(parameters.NVL(PRMTR_CODIGO_OPER))
                .forEach((DinamicVO<String, String> om) ->comprobante.append("S"+i[0], om.NVL("servicio"))
                    .append("P"+i[0], om.NVL("producto"))
                    .append("N"+i[0], om.NVL("nombre"))
                    .append("D"+i[0]++, om.NVL("descripcion")));
        return comprobante;
    }//getComprobante
}//ListServiciosAction
