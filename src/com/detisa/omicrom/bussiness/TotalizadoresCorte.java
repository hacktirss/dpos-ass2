/*
 * TotalizadoresCorte
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2017
 */
package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.data.TotalizadoresDAO;
import com.ass2.volumetrico.puntoventa.data.TotalizadoresVO;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.logging.LogManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public class TotalizadoresCorte {
    
    @Getter 
    private final List <TotalizadoresVO> totalizadores = new ArrayList <> ();

    public TotalizadoresCorte(String corte) throws DBException {
        TotalizadoresDAO.getTotalizadores(corte)
                .forEach((TotalizadoresVO totalizador) -> {
                    LogManager.info("Recuperando totalizador " + totalizador);
                    totalizadores.add(totalizador);
                });
    }
    public boolean hasTotalizador(String posicion) {
        return totalizadores.stream()
                .anyMatch(totalizador -> totalizador.NVL("posicion").equals(posicion));
    }
    public TotalizadoresVO getTotalizador(String posicion) {
        return totalizadores.stream()
                .filter(totalizador -> totalizador.NVL("posicion").equals(posicion))
                .collect(Collectors.collectingAndThen(Collectors.toList(), items -> items.isEmpty() ? new TotalizadoresVO() : items.get(0)));
    }    
}//TotalizadoresCorte
