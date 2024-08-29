/**
 * PumpFilter 
 * DetiPOS WEB Service
 * Filro de bombas conectado/desconectado
 * ® 2021, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since Jan 2021
 */
package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.softcoatl.data.DinamicVO;

public class PumpFilter {

    public static boolean locked(DinamicVO<String, String> pump) {
        EstadoPosicionesVO pos = EstadoPosicionesDAO.getByPosicion(pump.NVL(ComandosVO.CMD_FIELDS.posicion.name()));
        return pos.isBlocked();
    }
    public static boolean unlocked(DinamicVO<String, String> pump) {
        EstadoPosicionesVO pos = EstadoPosicionesDAO.getByPosicion(pump.NVL(ComandosVO.CMD_FIELDS.posicion.name()));
        return !pos.isBlocked();
    }
}
