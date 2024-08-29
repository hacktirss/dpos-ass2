package com.detisa.omicrom.integraciones;

import com.ass2.volumetrico.puntoventa.common.Comprobante;

public interface ImprimeIntegracion {
    public Comprobante extract();
    public boolean valid();
}
