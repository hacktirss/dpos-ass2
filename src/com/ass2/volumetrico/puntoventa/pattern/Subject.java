package com.ass2.volumetrico.puntoventa.pattern;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.ConsumoVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.preset.Consumo;

/**
 *
 * @author ROLANDO
 */
public interface Subject {
    public void register(Observer observer);
    public void unregister(Observer observer);
    public void notifyObservers();
    public void error();
    public void timeout();
    public Object getState();
    public Consumo getConsumo();
    public ComandosVO getComando();
    public EstadoPosicionesVO getPosicion();
    public ConsumoVO getComprobante();
}
