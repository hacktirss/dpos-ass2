package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.CuentasPorCobrarDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import static com.ass2.volumetrico.puntoventa.services.actions.BaseAction.WS_PRMT_AUTH;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;

public class MarcaConsumoTarjetaAction extends ImprimeTransaccionAction {

    public MarcaConsumoTarjetaAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    private boolean marcaCobro(String idConsumo, String idTarjeta) throws DetiPOSFault {
        String sql = "UPDATE rm JOIN cli ON TRUE " +
                        "SET rm.cliente = cli.id, rm.codigo = cli.id, rm.placas = cli.alias, rm.tipodepago = cli.tipodepago, enviado = 0 " +
                        "WHERE cli.id = " + idTarjeta + " AND cli.tipodepago IN ( 'Tarjeta', 'Monedero', 'Vales' ) AND rm.id = " + idConsumo + " AND rm.cliente = 0 AND rm.uuid = '-----' AND rm.tipo_venta = 'D'";
        boolean updated = false;

        if (StringUtils.isNVL(idConsumo)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No se encontro el consumo"));
        }

        if (StringUtils.isNVL(idTarjeta)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "No se encontro la tarjeta"));
        }

        try {
            LogManager.info("Marcando venta con pago Tarjeta " + idConsumo + " " + idTarjeta);
            LogManager.debug(sql);
            updated = MySQLHelper.getInstance().execute(sql) && CuentasPorCobrarDAO.insertDespacho(idConsumo);
        } catch (DBException ex) {
            LogManager.info("Error marcando venta con pago Tarjeta " + idConsumo);
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
        if (parameters.isNVL(WS_PRMT_CLIENTE)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TARJETA"));
        }
        return this;
    }

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        DinamicVO<String, String> ven;

        try {
            ven = VendedoresDAO.getNameByNIP(parameters.NVL(WS_PRMT_AUTH));
        } catch (DBException dbex) {
            throw new DetiPOSFault(dbex.getMessage());
        }

        if ("INCORRECTO".equals(ven.NVL("nip")) || ven.isNVL("NOMBRE")) {
            throw new DetiPOSFault("Password incorrecto");
        }

        DinamicVO<String, String> ticket = retrieveData(parameters.NVL(WS_PRMT_TRANSACCION));

        if ("1".equals(ven.NVL("fix")) && !ticket.NVL("POSICION").equals(ven.NVL("posicion"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error marcando venta con pago Tarjeta", ven.NVL("NOMBRE") + " no está asignado a la posición " + ticket.NVL("POSICION")));
        }

        if (!marcaCobro(parameters.NVL(BaseAction.WS_PRMT_TRANSACCION), parameters.NVL(BaseAction.WS_PRMT_CLIENTE))) {
            throw new DetiPOSFault("Error insertando transaccion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Marcando venta con pago Tarjeta", "Marcando venta con pago Tarjeta"));
        }
        return super.getComprobante();
    }
}
