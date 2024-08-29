package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import static com.ass2.volumetrico.puntoventa.services.actions.BaseAction.WS_PRMT_POSICION;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecuperaConsumoCobroAction extends BaseAction {
    
    public static final String SQL_ULTIMO_CONSUMOS_POSICION = "com/detisa/omicrom/sql/SelectUltimoConsumosPosicion.sql";
    public static final String SQL_POSICION = "[$]POSICION";

    public RecuperaConsumoCobroAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }
    
    private DinamicVO<String, String> retrieveData(String posicion) throws DetiPOSFault {
        List <DinamicVO<String, String>> consumos = new ArrayList <> ();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_ULTIMO_CONSUMOS_POSICION)
                    .replaceAll(SQL_POSICION, posicion);
            LogManager.info("Consultando último consumos de la posición " + posicion);
            LogManager.debug(sql);
            consumos = OmicromSLQHelper.executeQuery(sql);
            if (consumos.isEmpty()) {
                throw new DetiPOSFault("No hay consumos disponibles en la posicion " + posicion, new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "No hay consumos disponibles en la posicion " + posicion, "No hay consumos disponibles en la posicion " + posicion));
            }
            if (!consumos.get(0).NVL("ttr").isEmpty()) {
                throw new DetiPOSFault("El consumo en la posicion " + posicion + " ya fue cobrado.", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "El consumo en la posicion " + posicion + " ya fue cobrado.", "El consumo en la posicion " + posicion + " ya fue cobrado."));
            }
        } catch (DetiPOSFault | DBException | IOException ex) {
            LogManager.info("Error consultando últimos consumos de la posición " + posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando los consumos en la posicion "+posicion));
        }

        return consumos.get(0);
    }//retrieveData

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_POSICION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro POSICION"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = new Comprobante();
        DinamicVO<String, String> consumo = retrieveData(parameters.NVL(WS_PRMT_POSICION));
        comprobante.append(new Comprobante(consumo));
        return comprobante;
    }//getComprobante
}
