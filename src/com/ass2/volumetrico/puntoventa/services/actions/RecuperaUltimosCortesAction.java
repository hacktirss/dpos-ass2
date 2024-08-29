package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.util.ArrayList;
import java.util.List;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;

public class RecuperaUltimosCortesAction extends BaseAction {

    public static final String SQL_ULTIMOS_CORTES = "com/detisa/omicrom/sql/SelectUltimosCortes.sql";
    public static final String SQL_ISLA = "[$]ISLA";

    public RecuperaUltimosCortesAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private List <DinamicVO<String, String>> retrieveData(String isla) throws DetiPOSFault {
        List <DinamicVO<String, String>> cortes = new ArrayList <> ();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_ULTIMOS_CORTES).replaceAll(SQL_ISLA, isla);
            LogManager.info("Recuperando últimos cortes de la Isla " + isla);
            LogManager.debug(sql);
            cortes.addAll(OmicromSLQHelper.executeQuery(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Error recuperando últimos cortes de la Isla " + isla);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando los consumos en la posicion "+isla));
        }

        return cortes;
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
            comprobante.append(new Comprobante(tr, "CT" + idx++, "CT", ""));
        }

        return comprobante;
    }//getComprobante
}//SaldoAction
