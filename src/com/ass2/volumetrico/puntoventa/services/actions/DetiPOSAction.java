/**
 * DetiPOSAction Action interface
 *
 * @author Rolando Esquivel VillafaÃ±a
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;

public interface DetiPOSAction {

    public DetiPOSAction validateRequest() throws DetiPOSFault;
    public DetiPOSAction validatePOS() throws DetiPOSFault;
    public Comprobante getComprobante() throws DetiPOSFault;
    public Object getResponse() throws DetiPOSFault;
}//DetiPOSAction
