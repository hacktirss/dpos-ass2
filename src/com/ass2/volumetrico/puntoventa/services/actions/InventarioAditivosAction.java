package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InventarioAditivosAction extends BaseAction {

    public static final String SQL_INVENTARIO_ADITIVOS = "com/detisa/omicrom/sql/SelectInventarioAditivos.sql";
    public static final String SQL_INVENTARIO_ISLA = "com/detisa/omicrom/sql/SelectInventarioAditivosIsla.sql";

    public static final String SQL_PRMTR_ISLA = "[$]ISLA";

    public static final String CVE_ADITIVOS = "Aceites";
    public static final String CVE_ACTIVO = "Si";

    public static enum ADIVITOS_FIELDS {

        ADT_ID,
        DESCRIPCION,
        UMEDIDA,
        RUBRO,
        ACTIVO,
        EXISTENCIA,
        COSTO,
        PRECIO
    }
    private final List<DinamicVO<String, String>> islas = new ArrayList<>();

    public InventarioAditivosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

   @Override
   protected <E extends Enum<E>> List <String> getActionFields(Class <E> clazz) {
      List <String> fields = new ArrayList <> ();

      for (ADIVITOS_FIELDS field : ADIVITOS_FIELDS.values()) {
         fields.add(field.toString());
      }
      islas.forEach((DinamicVO<String, String> isla) -> {
          fields.add(isla.NVL("POSICION"));
        });

      return fields;
   }//fields

    public String getQueryIslas() {

        StringBuilder sqlIslas = new StringBuilder();

        islas.forEach((DinamicVO<String, String> isla) -> {
            sqlIslas.append(0==sqlIslas.length()?"" : ",");
            sqlIslas.append("exi").append(isla.NVL("POSICION")).append(" AS EXI").append(isla.NVL("POSICION"));
        }); //foreach ISLA

        return sqlIslas.toString();
    }//getQueryIslas

    private List<DinamicVO<String, String>> retriveData() throws DetiPOSFault {
        List<DinamicVO<String, String>> dataList = new ArrayList<>();
        String query = "";
        try {
            query = parameters.isNVL("isla") ?
                    BaseDAO.loadSQLSentenceAsResource(SQL_INVENTARIO_ADITIVOS) : 
                    BaseDAO.loadSQLSentenceAsResource(SQL_INVENTARIO_ISLA).replaceAll(SQL_PRMTR_ISLA, parameters.NVL(WS_PRMT_ISLA));
            LogManager.info("Consultando inventario de aditivos");
            LogManager.debug(query);
            dataList.addAll(OmicromSLQHelper.executeQuery(query));
            if (dataList.isEmpty()) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error consultando inventario", "No hay informacion de Inventario")); 
            }
        } catch (DetiPOSFault | DBException | IOException ex) {
            LogManager.info("Ocurrio un error recuperando el inventario de aditivos");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error recuperando el inventario"));
        }
        return dataList;
    }//retrieveData

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        Comprobante comprobante = super.getComprobante();
        int index = 1;

        comprobante.append("FECHA_IMPRESION", DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss", Calendar.getInstance(), DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO));

        for (DinamicVO<String, String> voVO : retriveData()) {
            comprobante.append("ADT" + index, "" + index)
                    .append(new Comprobante(voVO, "ADT" + index++, ADIVITOS_FIELDS.ADT_ID.toString(), ""));
        }
        if (parameters.isNVL("isla")) {
            comprobante.append("ORIGEN", "ALMACEN");
        } else {
            comprobante.append("ORIGEN", "ISLA " + parameters.NVL("isla"));
        }

        return comprobante;
    }//getComprobante
}//InventarioAditivosAction
