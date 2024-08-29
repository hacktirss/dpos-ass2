package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventarioTanquesAction extends BaseAction {

    public static final String SQL_INVENTARIO_TANQUES = "com/detisa/omicrom/sql/SelectInventarioTanques.sql";

    public static final String CVE_ACTIVO = "Si";

    public InventarioTanquesAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    private List<DinamicVO<String, String>> retriveData() throws DetiPOSFault {

        List<DinamicVO<String, String>> dataList = new ArrayList <> ();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_INVENTARIO_TANQUES);
            LogManager.info("Consultando inventario de tanques");
            LogManager.debug(sql);
            dataList.addAll(OmicromSLQHelper.executeQuery(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Error consultando inventario de tanques");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando el inventario"));
        }
        return dataList;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = super.getComprobante();
        int i = 1;

        for (DinamicVO<String, String> voVO : retriveData()) {
            comprobante.append(new Comprobante(voVO, "TNQ" + i++, "TANQUE", ""));
        }

        return comprobante;
    }
}
