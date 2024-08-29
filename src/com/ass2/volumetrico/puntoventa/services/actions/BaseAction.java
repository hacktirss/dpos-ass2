/**
 * BaseAction Basic DetiPOSAction
 *
 * @author Rolando Esquivel Villafaña
 * @author REV@Softcoatl
 */
package com.ass2.volumetrico.puntoventa.services.actions;

import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.common.OmicromVO;
import com.ass2.volumetrico.puntoventa.data.EstacionDAO;
import com.ass2.volumetrico.puntoventa.data.ImpresionesDAO;
import com.softcoatl.database.mysql.MySQLDB;
import com.ass2.volumetrico.puntoventa.data.TerminalesDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.ass2.volumetrico.puntoventa.data.VersionesDAO;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.sockets.Updater;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.dao.BaseDAO;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAction implements DetiPOSAction {

    public static final String SQL_ESTACION = "com/detisa/omicrom/sql/SelectEstacion.sql";
    public static final String SQL_VERSION = "com/detisa/omicrom/sql/SelectVersion.sql";

    public static final String SQL_PRMTR_POSID = "[$]POS_ID";

    public static final String WS_PRMT_IP_CLIENT        = "ipClient";
    public static final String WS_PRMT_LAN_MAC_ADD      = "lanMacAdd";
    public static final String WS_PRMT_WLAN_MAC_ADD     = "wlanMacAdd";
    public static final String WS_PRMT_KERNEL_VERSION   = "kernelVersion";
    public static final String WS_PRMT_TERMINAL         = "idTerminal";
    public static final String WS_PRMT_VERSION          = "dposVersion";
    public static final String WS_PRMT_AUTH             = "authenticate";
    public static final String WS_PRMT_ISLA             = "isla";
    public static final String WS_PRMT_POSICION         = "posicion";
    public static final String WS_PRMT_TRANSACCION      = "transaccion";
    public static final String WS_PRMT_EFECTIVO         = "efectivo";
    public static final String WS_PRMT_CLIENTE          = "cliente";
    public static final String WS_PRMT_TARJETA          = "idTarjeta";
    public static final String WS_PRMT_BOLETO           = "boleto";
    public static final String WS_PRMT_DESP             = "despachador";

    protected OmicromVO parameters = null;
    protected DinamicVO<String, String> version = null;
    protected DinamicVO<String, String> terminal;

    protected final DinamicVO<String, String> estacion;

    
    public static enum MODULES {

        CON, //Consumo en terminal
        FAC, //Facturación
        INV, //Consulta de Inventario
        IMP  //Impresión de comprobantes
    }

    public BaseAction(DinamicVO<String, String> param) throws DetiPOSFault {
        parameters = new OmicromVO(param);
        try {
            LogManager.info("Request parameters (" + parameters.getClass().getName() + ") " + parameters.getEntries());
            estacion = EstacionDAO.getDatosEstacion();
            if (!parameters.isNVL(WS_PRMT_TERMINAL)) {
                terminal = TerminalesDAO.getTerminalBySerialNumber(parameters.NVL(WS_PRMT_TERMINAL));
                if (!terminal.isVoid()) {
                    TerminalesDAO.lastConnection(terminal.NVL("POS_ID"));
                }
            }
        } catch (DBException ex) {
            LogManager.info("Error en constuctor");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando estacion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Error consultando estacion"));
        }
    }//BaseAction Constructor

    protected <E extends Enum<E>> List<String> getActionFields(Class<E> clazz) {
        List<String> actionFields = new ArrayList <> ();

        for (E item : clazz.getEnumConstants()) {
            actionFields.add(item.name());
        }//for each enum
        return actionFields;
    }//getActionFields

    @Override
    public DetiPOSAction validatePOS() throws DetiPOSFault {
        String serial;

        try {
            if (parameters.isNVL(WS_PRMT_TERMINAL)) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro TERMINAL"));
            }

            serial = parameters.NVL(WS_PRMT_TERMINAL);
            LogManager.debug("Validando POS " + serial);

            if (!parameters.isNVL(WS_PRMT_VERSION)) {
                LogManager.info("DPOS Version Number "+parameters.NVL(WS_PRMT_VERSION));
                VersionesDAO.registerPOSVersion(parameters.NVL(WS_PRMT_VERSION));
                TerminalesDAO.updateAppVersion(terminal.NVL("POS_ID"), parameters.NVL(WS_PRMT_VERSION));
            }

            if (terminal.isVoid()) {
                throw new InvalidParameterException("La terminal " + serial + " no esta registrada");
            }

            if (!"A".equals(terminal.NVL("STATUS"))) {
                throw new InvalidParameterException("La terminal " + serial + " no esta activa");
            }

            if (!parameters.isNVL(WS_PRMT_LAN_MAC_ADD)) {
                try {
                    TerminalesDAO.updateLAN(terminal.NVL("POS_ID"), parameters.NVL(WS_PRMT_LAN_MAC_ADD));
                } catch (DBException ex) {
                    LogManager.info("Error updating Client LAN MacAddress");
                    LogManager.error(ex);
                    LogManager.debug("Trace", ex);
                }
            }
            if (!parameters.isNVL(WS_PRMT_WLAN_MAC_ADD)) {
                try {
                    TerminalesDAO.updateWLAN(terminal.NVL("POS_ID"), parameters.NVL(WS_PRMT_WLAN_MAC_ADD));
                } catch (DBException ex) {
                    LogManager.info("Error updating Client WLAN MacAddress");
                    LogManager.error(ex);
                    LogManager.debug("Trace", ex);
                }
            }
            if (!parameters.isNVL(WS_PRMT_KERNEL_VERSION)) {
                try {
                    TerminalesDAO.updateKernel(terminal.NVL("POS_ID"), parameters.NVL(WS_PRMT_KERNEL_VERSION).replaceAll("\n", "").replaceAll("\r", ""));
                } catch (DBException ex) {
                    LogManager.info("Error updating Client WLAN MacAddress");
                    LogManager.error(ex);
                    LogManager.debug("Trace", ex);
                }
            }
            if (!parameters.isNVL(WS_PRMT_VERSION)) {
                try {
                    TerminalesDAO.updateAppVersion(terminal.NVL("POS_ID"), parameters.NVL(WS_PRMT_VERSION));
                } catch (DBException ex) {
                    LogManager.info("Error updating Client WLAN MacAddress");
                    LogManager.error(ex);
                    LogManager.debug("Trace", ex);
                }
            }
            if (!parameters.isNVL(WS_PRMT_IP_CLIENT) && !terminal.NVL("IP").equals(parameters.NVL(WS_PRMT_IP_CLIENT))) {
                try {
                    TerminalesDAO.updateIP(terminal.NVL("POS_ID"), parameters.NVL(WS_PRMT_IP_CLIENT));
                } catch (DBException ex) {
                    LogManager.info("Error updating Client IP");
                    LogManager.error(ex);
                    LogManager.debug("Trace", ex);
                }
            }//if !=IP
        } catch (DetiPOSFault | DBException | InvalidParameterException ex) {
            LogManager.info("Error ingresando al sistema");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage(), new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Validando acceso al sistema"));
        }

        return this;
    }//validate

    protected VendedoresVO retrieveNIPDespachador(String nipDespachador, String posicion) throws DetiPOSFault {
        VendedoresVO ven;
        try {
            ven = VendedoresDAO.getNameByNIPI(nipDespachador, posicion);
            LogManager.debug(ven);
            ImpresionesDAO.evento(
                    Integer.parseInt(parameters.NVL(WS_PRMT_TRANSACCION)), 
                    Integer.parseInt(terminal.NVL("POS_ID")), 
                    "NO PIDE".equals(ven.NVL("nip")) ? 0 : Integer.parseInt(ven.NVL("ID")));
            return ven;
        } catch (DBException DBE) {
            throw new DetiPOSFault(DBE.getMessage());        
        }
    }

    public boolean login(String module) throws DetiPOSFault {
        StringBuilder sqlBuffer = new StringBuilder();
        String password = parameters.NVL(WS_PRMT_AUTH, "");
        String serial = parameters.NVL(WS_PRMT_TERMINAL, "");
        String login;

        sqlBuffer.append("SELECT CASE")
                .append("      WHEN pos_mod.password IS NULL THEN 'OK'")
                .append("      WHEN pos_mod.password = '' THEN 'OK'")
                .append("      WHEN pos_mod.password = '").append(password).append("' THEN 'OK'")
                .append("      ELSE 'ERROR' ")
                .append("END AS LOGIN ")
                .append("FROM pos_catalog ")
                .append("JOIN pos_modules ON 1=1 ")
                .append("LEFT JOIN pos_mod ON pos_mod.mod_id = pos_modules.mod_id AND pos_mod.POS_ID = pos_catalog.POS_ID ")
                .append("WHERE SERIAL = '").append(serial).append("' ")
                .append("AND NAME = '").append(module).append("'");

        try {
            login = BaseDAO.selectValue(MySQLDB.DB_NAME, sqlBuffer.toString(), "LOGIN");
            if (!"OK".equals(login)) {
                throw new InvalidParameterException("El Password proporcionado es incorrecto");
            }
        } catch (DBException | InvalidParameterException ex) {
            LogManager.info("Error ingresando al sistema");
            LogManager.error(ex);
            LogManager.info("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Validando acceso al sistema"));
        }

        return true;
    }//validate

    public boolean validatePassword(String password) throws DetiPOSFault {
        StringBuffer sqlBuffer = new StringBuffer();
        String login;

        sqlBuffer.append("SELECT COUNT(*) AS OK FROM cia LEFT JOIN (SELECT valor FROM variables_corporativo WHERE llave = 'encrypt_fields') v ON TRUE WHERE claveterminal = CASE WHEN IFNULL( v.valor, 0 ) = 1 THEN encrypt_data('").append(password).append("') ELSE '").append(password).append("' END");

        try {
            LogManager.debug(sqlBuffer);
            login = BaseDAO.selectValue(MySQLDB.DB_NAME, sqlBuffer.toString(), "OK");
            if ("0".equals(login)) {
                throw new InvalidParameterException("El Password proporcionado es incorrecto");
            }
            return true;
        } catch (DBException | InvalidParameterException ex) {
            LogManager.info("Error ingresando al sistema");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("El PASSWORD proporcionado es incorrecto", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Validando acceso al sistema"));
        }
    }

    public String getPermissionList() throws DetiPOSFault {
        StringBuffer sqlBuffer = new StringBuffer();
        String serial = parameters.NVL(WS_PRMT_TERMINAL, "");
        StringBuilder permissions = new StringBuilder();

        sqlBuffer.append("SELECT pos_modules.name AS NAME,")
                .append("CASE")
                .append("      WHEN pos_mod.status IS NULL THEN 1")
                .append("      WHEN pos_mod.status = '' THEN 1")
                .append("      WHEN pos_mod.status = 'A' THEN 1")
                .append("      ELSE 0 ")
                .append("END AS PERMISSION ")
                .append("FROM pos_catalog ")
                .append("JOIN pos_modules ON 1=1 ")
                .append("LEFT JOIN pos_mod ON pos_mod.mod_id = pos_modules.mod_id AND pos_mod.POS_ID = pos_catalog.POS_ID ")
                .append("WHERE SERIAL = '").append(serial).append("' ");

        try {
            for (DinamicVO<String, String> permission : OmicromSLQHelper.executeQuery(sqlBuffer)) {
                permissions.append("&").append(permission.NVL("NAME")).append("=").append(permission.NVL("PERMISSION"));
            }
        } catch (DBException EXC) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, EXC, "Validando acceso al sistema"));
        }

        return permissions.toString();
    }//getPermissionList

    protected void startUpdate(String version) {
        Updater updater = new Updater(version);
        updater.initService();
    }//startUpdate

    protected boolean checkForVersion() throws DetiPOSFault {
        String sql;
        boolean update;
        try {
            sql = BaseDAO.loadSQLSentenceAsResource(SQL_VERSION);
            LogManager.debug(sql);
            sql = sql.replaceAll(SQL_PRMTR_POSID, parameters.NVL(WS_PRMT_TERMINAL, ""));
            LogManager.debug(sql);
            version = OmicromSLQHelper.getUnique(sql);
            LogManager.debug(version);
            update = !version.isVoid();
            if (update) {
                startUpdate(version.NVL("VERSION_ID"));
            }
        } catch (DBException | IOException ex) {
            LogManager.info("Error obteniendo los datos de la estación");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Obteniendo datos de la Estacion"));
        }

        return update;
    }//checkForVersion

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        return new Comprobante(estacion).append("DATETIME", DateUtils.fncsFormat("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public Object getResponse() throws DetiPOSFault {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        return this;
    }//validateRequest
}//BaseAction
