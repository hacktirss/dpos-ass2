/**
 * CorteV2
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
import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CorteDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleVO;
import com.ass2.volumetrico.puntoventa.data.TotalizadoresDAO;
import com.ass2.volumetrico.puntoventa.data.TotalizadoresVO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;

public class CorteV2 extends Corte {

    public CorteV2(String isla, CLIENT client, PROCESS process) throws DBException, DetiPOSFault {

        super(isla, client, process);
    }

    @Override
    protected boolean totalize() {

        TotalizadoresDAO.drop(this.corteOmicrom.id());
        actives.forEach((DinamicVO<String, String> active) -> {
                    try {
                        LogManager.info("Solicitando Totalizador en la posición " + active.NVL(TotalizadoresVO.T_FIELDS.posicion.name()));
                        ComandosVO comando = ComandosVO.newTotalizador(active.NVL(ComandosVO.CMD_FIELDS.posicion.name()), this.corteOmicrom.id());
                        ComandosDAO.create(comando);
                        LogManager.info("Comando creado con id " + comando.NVL(ComandosVO.CMD_FIELDS.id.name()));
                        register(comando, active);
                    } catch (DetiPOSFault ex) {
                        OmicromLogManager.error(ex);
                    }
                });
        LogManager.info("Totalizing done!");
        return true;
    }//totalize

    @Override
    protected boolean matchTotalizers() {
        try {
            waitTotalizadores();
            totalizadoresCorte = new TotalizadoresCorte(corteOmicrom.id());
            if (!openOnly()) {
                LogManager.info("Determinando consumos");
                CorteDetalleDAO.updateCortesDetallesV2(corteOmicrom.id());
                corteOmicrom.reload();
                executeAutoconsumo();

                corteOmicrom.getDetalles().stream()
                        .forEach((CorteDetalleVO detalle) -> {
                            try {
                                if(totalizadoresCorte.hasTotalizador(detalle.NVL(CorteDetalleVO.CDT_FIELDS.posicion.name()))) {
                                    updateDetalle(detalle);
                                } else {
                                    insertConciliationWindow(detalle);
                                }
                            } catch (DBException | DetiPOSFault ex) {
                                OmicromLogManager.error(ex);
                            }
                        });
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
