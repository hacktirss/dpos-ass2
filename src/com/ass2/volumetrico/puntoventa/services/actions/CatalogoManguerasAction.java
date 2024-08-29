/*
 *  CatalogoManguerasAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.logging.LogManager;

public class CatalogoManguerasAction extends BaseAction {

    public CatalogoManguerasAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int[] i = { 1 };
        ManguerasDAO.getCatalogoMangueras().forEach((DinamicVO<String, String> man) -> {
            LogManager.info(man);
            comprobante.append("PRD"+i[0], man.NVL("POSICION")).append(new Comprobante(man, "PRD"+String.valueOf(i[0]++), "posicion", ""));
        });
        return comprobante;
    }//getComprobante
}//CatalogoManguerasAction
