/**
 * CorteV3
 * DetiPOS WEB Service
 * Ejecuta el corte en la isla indicada
 * ® 2014, ASS2
 * http://www.ass2.com.mx
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since Dec 2014
 */
package com.detisa.omicrom.bussiness;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.data.CorteDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;

public class CorteV3 extends Corte {

    public CorteV3(String isla, Corte.CLIENT client, Corte.PROCESS process) throws DBException, DetiPOSFault {
        super(isla, client, process);
    }

    private void logCurrentStatus() {
        try {
            LogManager.info("Mostrando estatus actual en man_pro");
            ManguerasDAO.getMangueras()
                    .forEach((ManguerasVO man) -> LogManager.info(
                            String.format("Posición %s, Manguera %s. Totalizadores($/V) %s/%s ",
                                    man.NVL(ManguerasVO.DSP_FIELDS.posicion.name()),
                                    man.NVL(ManguerasVO.DSP_FIELDS.manguera.name()),
                                    man.NVL(ManguerasVO.DSP_FIELDS.totalizadorV.name()),
                                    man.NVL(ManguerasVO.DSP_FIELDS.totalizador$.name()))));
        } catch (DetiPOSFault ex) {
            LogManager.info("Error mostrando estatus actual en man_pro");
        }
    }
    @Override
    protected boolean totalize() {
        return true;
    }

    @Override
    protected boolean matchTotalizers() {
        try {
            totalizadoresCorte = new TotalizadoresCorte(this.corteOmicrom.id());
            logCurrentStatus();
            if (!openOnly()) {
                LogManager.info("Determinando consumos");
                CorteDetalleDAO.updateCortesDetallesV3(this.corteOmicrom.id());
                LogManager.debug("Detalles cerrados");
                CorteDAO.closeCorte(this.corteOmicrom.id(), this.client.name());
                corteOmicrom.reload();
            }
            insertNewCorte();
            return true;
        } catch (DBException | DetiPOSFault ex) {
            OmicromLogManager.error(ex);
        } finally {
            initNotificationWindow();
            this.done = true;
        }
        return false;
    }
}
