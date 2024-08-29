package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecuperaConsumosAction extends BaseAction {

    public static final String SQL_ULTIMOS_CONSUMOS_POSICION = "com/detisa/omicrom/sql/SelectUltimosConsumosPosicion.sql";
    public static final String SQL_POSICION = "[$]POSICION";
    public static final String SQL_LIMITE = "[$]LIMITE";

    public RecuperaConsumosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private List <DinamicVO<String, String>> retrieveData(String posicion) throws DetiPOSFault {
        List <DinamicVO<String, String>> consumos = new ArrayList <> ();
        String limite = VariablesDAO.getCorporativo("limite_impresion", "5");

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_ULTIMOS_CONSUMOS_POSICION)
                    .replaceAll(SQL_POSICION, posicion)
                    .replaceAll(SQL_LIMITE, limite);
            LogManager.info("Consultando últimos consumos de la posición " + posicion);
            LogManager.debug(sql);
            consumos.addAll(OmicromSLQHelper.executeQuery(sql));
            if (consumos.isEmpty()) {
                throw new DetiPOSFault("No hay consumos disponibles en la posicion " + posicion, new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "No hay consumos disponibles en la posicion " + posicion, "No hay consumos disponibles en la posicion " + posicion));
            }
        } catch (DetiPOSFault | DBException | IOException ex) {
            LogManager.info("Error consultando últimos consumos de la posición " + posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando los consumos en la posicion "+posicion));
        }

        return consumos;
    }//retrieveData

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_POSICION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro POSICION"));
        }
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        int idx = 1;

        for (DinamicVO<String, String> tr : retrieveData(parameters.NVL(WS_PRMT_POSICION))) {
            LogManager.debug(tr);
            comprobante.append(new Comprobante(tr, "TR" + idx++, "TR", ""));
            LogManager.debug(comprobante.serialize());
        }

        return comprobante;
    }//getComprobante
}//SaldoAction
