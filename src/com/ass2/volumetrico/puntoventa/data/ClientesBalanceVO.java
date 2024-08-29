/*
 * ClientesBalanceVO
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since jun 2017
 */
package com.ass2.volumetrico.puntoventa.data;

import java.math.BigDecimal;

public class ClientesBalanceVO extends ClientesVO {

    public static enum BAL_FIELDS {

        ABONOS,
        CARGOS,
        SALDO
    }

    public boolean saldoSuficiente(BigDecimal cargo) {
        //TODO validate Saldo or Credito
        if (isCredito() || isPospago()) {
            return new BigDecimal(NVL(BAL_FIELDS.SALDO.name())).subtract(cargo).compareTo(BigDecimal.ZERO) >= 0;
        } else if (isPrepago()) {
            return new BigDecimal(NVL(BAL_FIELDS.SALDO.name())).subtract(cargo).compareTo(BigDecimal.ZERO) >= 0;
        }
        return false;
    }//saldoSuficiente
}//ClienteBalanceVO
