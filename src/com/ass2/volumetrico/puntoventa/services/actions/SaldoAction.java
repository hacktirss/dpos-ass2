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
import java.util.List;

public class SaldoAction extends BaseAction {

    public static final String SQL_SALDO_CLIENTE = "com/detisa/omicrom/sql/SelectSaldoCliente.sql";
    public static final String SQL_CLIENTE = "[$]CLI_ID";

    public static final String CVE_ABONOS = "H";
    public static final String CVE_CARGOS = "C";

    public static enum SALDO_FIELDS {

        CLI_ID,
        NOMBRE,
        DIRECCION,
        COLONIA,
        MUNICIPIO,
        ALIAS,
        TELEFONO,
        ACTIVO,
        CONTACTO,
        OBSERVACIONES,
        TIPODEPAGO,
        LIMITE,
        RFC,
        CODIGO,
        CORREO,
        NUMEROEXT,
        NUMEROINT,
        ENVIARCORREO,
        CUENTABAN,
        ESTADO,
        FORMADEPAGO,
        ABONOS,
        CARGOS,
        SALDO
    }

    public SaldoAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }// Constructor

    public DinamicVO<String, String> retrieveData(String cliente) throws DetiPOSFault {

        DinamicVO<String, String> data = new DinamicVO<>();

        try {
            String sql = BaseDAO.loadSQLSentenceAsResource(SQL_SALDO_CLIENTE).replaceAll(SQL_CLIENTE, cliente);
            LogManager.info("Consultando saldo del cliente " + cliente);
            LogManager.debug(sql);
            data.setEntries(OmicromSLQHelper.getUnique(sql));
        } catch (DBException | IOException ex) {
            LogManager.info("Error consultando saldo del cliente " + cliente);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando la informacion del cliente "+cliente));
        }

        return data;
    }//retrieveData

    protected String getClienteTarjeta(String idTarjeta) throws DetiPOSFault {
        List<DinamicVO<String, String>> returnedData;
        String cliente = "";
        String sql = "SELECT cliente FROM unidades WHERE codigo = '" + idTarjeta + "'";

        try {
            LogManager.info("Consultando cliente de la tarjeta " + idTarjeta);
            LogManager.debug(sql);
            returnedData = OmicromSLQHelper.executeQuery(sql);
            if (returnedData.isEmpty()) {
                throw new DBException("No se encontro informacion");
            }
            return returnedData.get(0).NVL("CLIENTE");
        } catch (DBException ex) {
            LogManager.info("Error consultando cliente de la tarjeta " + idTarjeta);
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, "Recuperando la información de la tarjeta "+idTarjeta));
        }
    }//getClienteTarjeta

    private Comprobante consultaSaldoTarjeta() throws DetiPOSFault {
        return new DposActionFactory().getAnonymousAction(DposActionFactory.ACTIONS.SALDO_TARJETA, parameters).getComprobante();
    }//consultaSaldoTarjeta

    private Comprobante consultaSaldoCliente() throws DetiPOSFault {
        return new DposActionFactory().getAnonymousAction(DposActionFactory.ACTIONS.SALDO_CLIENTE, parameters).getComprobante();
    }//consultaSaldoCliente

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        super.validateRequest();
        if (parameters.isNVL(WS_PRMT_CLIENTE) && parameters.isNVL(WS_PRMT_TARJETA)) {
            throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se esperaba el parametro CLIENTE o el parametro TARJETA"));
        }
        return this;
    }//validateRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        if (!parameters.isNVL(WS_PRMT_TARJETA)) {
            return consultaSaldoTarjeta();
        } else {
            return consultaSaldoCliente();
        }
    }//getComprobante
}//SaldoAction
