/**
 * QRGenerator
 * ASS2PuntoVenta®
 * © 2023, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since feb 2023
 */
package com.ass2.volumetrico.puntoventa.utils.qr;

import com.softcoatl.data.DinamicVO;

public interface QRGenerator {
    public DinamicVO<String, String> getQR(int id);
}
