package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.AutorizacionesrmDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.regex.Pattern;

public class MarcaCobroConsumoAction extends BaseAction {

    public static final String WS_PRMTR_CADENA_AUTH = "cadenaAuth";

    public MarcaCobroConsumoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    private static String clear(String input) {
        return input.replaceAll(Pattern.quote("{"), "").replaceAll(Pattern.quote("}"), "").trim();
    }

    private JsonObject parseObject(JsonObject jsonI) throws ParseException {
        JsonObject jsonO = new JsonObject();
        jsonI.entrySet().forEach(item -> jsonO.addProperty(clear(item.getKey()), clear(item.getValue().getAsString())));
        return jsonO;
    }

    private void insertaTransaccion(String idConsumo, String transaccion) throws DetiPOSFault {
        JsonObject json;
        try {
            json = parseObject((JsonObject) new JsonParser().parse(transaccion));
            LogManager.debug(json);
            LogManager.info("Insertando transacción OILPAY " + idConsumo);
            AutorizacionesrmDAO.evento(
                    Integer.parseInt(idConsumo), 
                    DateUtils.fncoCalendar("MdY", json.get("FechaTransaccion").getAsString()), 
                    new BigDecimal(json.get("Monto").getAsString()), 
                    json.toString());
            LogManager.info("Insertando transacción OILPAY " + idConsumo);
        } catch (ParseException ex) {
            LogManager.info("Error marcando transaccion OILPAY " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+idConsumo));
        }
    }//updateComprobante

    private boolean marcaCobro(String idConsumo) throws DetiPOSFault {
        String sql = "UPDATE rm JOIN cli ON TRUE " +
                        "SET rm.cliente = cli.id, rm.codigo = cli.id, rm.tipodepago = cli.tipodepago, enviado = 0 " +
                        "WHERE cli.alias = 'OILPAY' AND cli.tipodepago = 'Tarjeta' AND rm.id = " + idConsumo + " AND rm.cliente = 0 AND rm.uuid = '-----'";
        boolean updated = false;

        if (StringUtils.isNVL(idConsumo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No se encontro el consumo"));
        }


        try {
            LogManager.info("Marcando venta con pago OILPAY " + idConsumo);
            LogManager.debug(sql);
            updated = MySQLHelper.getInstance().execute(sql);
        } catch (DBException ex) {
            LogManager.info("Error marcando venta como jarreo " + idConsumo);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+idConsumo));
        }

        return updated;
    }//updateComprobante

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_TRANSACCION)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TRANSACCION"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        if (!marcaCobro(parameters.NVL(BaseAction.WS_PRMT_TRANSACCION))) {
            throw new DetiPOSFault("Error insertando transaccion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error insertando transaccion", "Error insertando transaccion"));
        }
        insertaTransaccion(parameters.NVL(BaseAction.WS_PRMT_TRANSACCION), parameters.NVL(WS_PRMTR_CADENA_AUTH));
        return new ImprimeTransaccionAction(parameters).getComprobante();
    }//getComprobante
}
