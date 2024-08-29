package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.FacturacionUtils;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.data.BitacoraDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.softcoatl.database.mysql.MySQLHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import static com.ass2.volumetrico.puntoventa.services.actions.BaseAction.WS_PRMT_AUTH;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.database.entity.dao.BaseDAO;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ImprimePuntosAction extends ImprimeTransaccionAction {

    public static final String SQL_CONSUMO_POSICION = "com/detisa/omicrom/sql/SelectConsumoPuntos.sql";
    public static final String SQL_POSICION = "[$]POSICION";

    public ImprimePuntosAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }//Constructor

    private DinamicVO<String, String> getConsumoPosicion(String posicion) throws DetiPOSFault {

        DinamicVO<String, String> data = new DinamicVO<>();
        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_CONSUMO_POSICION).replaceAll(SQL_POSICION, posicion);
            LogManager.info("Consultando último consumo en la posición " + posicion);
            LogManager.debug(sql);
            data.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Ocurrió un error recuperando el consumo en la posición "+posicion);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error recuperando el consumo en la posicion "+posicion));
        }
        return data;
    }

    private boolean updateComprobante(String id, String puntos, String fecha, String vigencia) throws DetiPOSFault {
        String sqlBuffer = "INSERT INTO rm_puntos( id, puntos, fecha, vigencia ) VALUES( ?, ?, ?, ? )";

        LogManager.info("Registrando los puntos para " + id);
        try (PreparedStatement ps = MySQLHelper.getInstance().getConnection().prepareStatement(sqlBuffer)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.setInt(2, Integer.parseInt(puntos));
            ps.setTimestamp(3, new Timestamp(DateUtils.fncoCalendar(fecha).getTimeInMillis()));
            ps.setTimestamp(4, new Timestamp(DateUtils.fncoCalendar(vigencia).getTimeInMillis()));
            return ps.executeUpdate()>0;
        } catch (SQLException ex) {
            LogManager.info("Error incrementando contador de impresión " + id);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Ocurrio un error actualizando el comprobante "+id));
        }
    }//updateComprobante

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
        Comprobante comprobante = super.getComprobante();
        DinamicVO<String, String> ticket;
        VendedoresVO ven;

        ven = retrieveNIPDespachador(parameters.NVL(WS_PRMT_AUTH), parameters.NVL(WS_PRMT_POSICION));
        if (ven.isInvalidLogin()) {  // TODO patch for DPOS lower versions
            LogManager.error("Error validando despachador con nip (" + parameters.NVL(WS_PRMT_AUTH) + ") " + ven);
            if (parameters.getField(WS_PRMT_AUTH)!=null) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error validando despachador", "Password incorrecto"));
            }
        }
        if (!ven.isAssigned(parameters.NVL(WS_PRMT_POSICION))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error imprimiendo comprobante", ven.NVL("nombre") + " no está asignado a la posición " + parameters.NVL(WS_PRMT_POSICION)));
        }

        BitacoraDAO.evento("Impresión de Promoción " + parameters.NVL("transaccion"), ven.NVL("id"), this.terminal.NVL("IP"));

        ticket = getConsumoPosicion(parameters.NVL(WS_PRMT_POSICION));
        if (ticket.isNVL("PPT") || "0".equals(ticket.NVL("PPT"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error recuperando puntos", "El consumo "+parameters.NVL(WS_PRMT_POSICION)+" no genero puntos"));
        }

        ticket.setField("LETRA", FacturacionUtils.importeLetra(ticket.NVL("PESOS", "0.0")));
        if (!updateComprobante(ticket.NVL("TR"), ticket.NVL("PPT"), ticket.NVL("FECHA_IMPRESION"), ticket.NVL("FECHA_VIGENCIA"))) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error recuperando puntos", "Para la posicion "+parameters.NVL(WS_PRMT_POSICION)));
        }

        if (!ven.isNVL("NOMBRE") && !"NO PIDE".equals(ven.NVL("nip"))) {
            comprobante.append("DESPACHADOR", ven.NVL("NOMBRE"));
        }

        comprobante.append(new Comprobante(ticket));

        return comprobante;
    }
}
