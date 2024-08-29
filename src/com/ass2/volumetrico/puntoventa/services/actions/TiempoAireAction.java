/*
 *  TiempoAireAction
 *  ® 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  mar 20, 2015
 */

package com.ass2.volumetrico.puntoventa.services.actions;

import com.detisa.gycse.GycseClient;
import com.ass2.volumetrico.puntoventa.common.Comprobante;
import com.ass2.volumetrico.puntoventa.data.GycseDAO;
import com.ass2.volumetrico.puntoventa.data.GycseEndpoints;
import com.ass2.volumetrico.puntoventa.data.GycseOperacion;
import static com.ass2.volumetrico.puntoventa.data.GycseOperacion.*;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.detisa.omicrom.gycse.GycseFault;
import com.detisa.omicrom.gycse.GycseRepositorioWS;
import com.detisa.omicrom.gycse.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.context.APPContext;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.math.BigDecimal;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsResConsultaRecarga;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsResRecarga;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsRespuesta;
import serviciosame.RMIClientFactory.STATUS;

public class TiempoAireAction extends BaseAction {

    public static final String PRMTR_CODIGO_OPERADOR = "CODIGO_OPERADOR";
    public static final String PRMTR_NUMERO = "NUMERO";
    public static final String PRMTR_IMPORTE = "IMPORTE";
    public static final String PRMTR_TRANSACCION = "TRANSACCION";
    public static final String PRMTR_AUTH = "PASSWORD";
    public static final String PRMTR_FORMA_PAGO = "FORMA_PAG";
    public static final String PRMTR_AUTH_BANCO = "AUTH_BANCO";

    static {
        DateUtils.registerDateFormat("yyyy-MM-dd HH:mm:ssz");
    }

    public TiempoAireAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        if (!"1".equals(VariablesDAO.getCorporativo("gycse_enabled", "0"))) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error de configuracion.", "Cobro de servicios no habilitado"));
        }
        if (parameters.isNVL(PRMTR_FORMA_PAGO)
                || (Integer.parseInt(parameters.NVL(PRMTR_FORMA_PAGO))>0 && parameters.isNVL(PRMTR_AUTH_BANCO)))
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de Parametros", "Se requiere Forma de Pago y Numero de Autorizacion Bancaria"));
        return super.validateRequest();
    }//validatreRequest
    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        GycseOperacion operacion;
        GycseClient client;
        GYCSEInfoRouteadorClsRespuesta wsResponse;
        GycseRepositorioWS repositorioWS;
        DinamicVO<String, String> despachador;
        Comprobante comprobante;
        String fechaRespuesta;

        try {
            repositorioWS = RMIClientFactory.getGycsePort(APPContext.getInitParameter(RMIClientFactory.GYCSE_ENDPOINT), 0);
            BigDecimal saldo = repositorioWS.getSaldo(estacion.NVL("RUBRO"), Integer.parseInt(estacion.NVL("NUMESTACION")));
            if(0 > saldo.compareTo(new BigDecimal(parameters.NVL(PRMTR_IMPORTE)))) {
                throw new DetiPOSFault("Error solicitando servicio", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error de Gycse", "Saldo insuficiente " + saldo.toPlainString()));
            }
            despachador = VendedoresDAO.getByNIP(parameters.NVL(PRMTR_AUTH));
            if (!"VENTANILLA".equals(parameters.NVL(PRMTR_AUTH)) && despachador.isVoid()) {
                throw new DetiPOSFault("Error solicitando servicio", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error de Gycse", "Error validando el nip del despachador"));
            }
            client = new GycseClient();
            operacion = GycseOperacion.newRecarga("VENTANILLA".equals(parameters.NVL(PRMTR_AUTH)) ? "0" : despachador.NVL("id"),
                    parameters.NVL(PRMTR_CODIGO_OPERADOR),
                    parameters.NVL(PRMTR_NUMERO),
                    parameters.NVL(PRMTR_IMPORTE),
                    parameters.NVL(PRMTR_FORMA_PAGO),
                    parameters.NVL(PRMTR_AUTH_BANCO),
                    client.getEndpoint().NVL(GycseEndpoints.FIELDS.pruebas.name()));
            LogManager.info("Solicitando autorización " + operacion.NVL(GycseOperacion.FIELDS.id.name()));
            wsResponse = client.invokeRecarga(parameters.NVL(PRMTR_CODIGO_OPERADOR),
                    operacion.NVL(FIELDS.peticion.name()),
                    parameters.NVL(PRMTR_NUMERO),
                    Integer.parseInt(parameters.NVL(PRMTR_IMPORTE)));

            fechaRespuesta = DateUtils.fncsFormat(
                    "yyyy-MM-dd HH:mm:ssz",
                    wsResponse.getDtFechaRespuesta().toGregorianCalendar(),
                    DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO);

            GYCSEInfoRouteadorClsResRecarga respuestaRecarga = wsResponse.getClsResRecarga().getValue();
            GYCSEInfoRouteadorClsResConsultaRecarga respuestaConsulta = wsResponse.getClsResConsultaRecarga().getValue();

            LogManager.debug(respuestaRecarga);
            LogManager.debug(respuestaConsulta);
            if (null==respuestaRecarga.getSAutorizacion().getValue()) {
                String mensaje = 
                        "Transaccion encontrada".equals(respuestaConsulta.getSObservaciones().getValue()) ? 
                         "NO_CONFIRMADA".equals(wsResponse.getSEstatus().getValue()) ? 
                                 "Transaccion no autorizada" :
                                 "Transaccion autorizada" :
                        respuestaConsulta.getSObservaciones().getValue();
                operacion.setField(FIELDS.fecha_respuesta, 
                        fechaRespuesta);
                operacion.setField(FIELDS.autorizacion, 
                        respuestaConsulta.getSAutoNo().getValue());
                operacion.setField(FIELDS.codigo_respuesta, 
                        StringUtils.fncsLeftPadding(
                                respuestaConsulta.getSCodigoRespuesta().getValue()
                        ,'0', 2));
                operacion.setField(FIELDS.mensaje_respuesta, mensaje);
                operacion.setField(FIELDS.descripcion_respuesta, mensaje);
                operacion.setField(FIELDS.transaccion, 
                        respuestaConsulta.getSTransNumber().getValue());
                operacion.setField(FIELDS.status, 
                        STATUS.decode(STATUS.valueOf(wsResponse.getSEstatus().getValue())));
            } else {
                operacion.setField(FIELDS.fecha_respuesta, 
                        fechaRespuesta);
                operacion.setField(FIELDS.autorizacion, 
                        respuestaRecarga.getSAutorizacion().getValue());
                operacion.setField(FIELDS.codigo_respuesta, 
                        StringUtils.fncsLeftPadding(
                                respuestaRecarga.getSCodigoRespuesta().getValue()
                        ,'0', 2));
                operacion.setField(FIELDS.mensaje_respuesta, 
                        respuestaRecarga.getSMensajeFinal().getValue());
                operacion.setField(FIELDS.descripcion_respuesta, 
                        respuestaRecarga.getSDescripcionRespuesta().getValue());
                operacion.setField(FIELDS.transaccion, 
                        respuestaRecarga.getSTransaccion().getValue());
                operacion.setField(FIELDS.saldo_inicial, 
                        respuestaRecarga.getSSaldoAntes().getValue());
                operacion.setField(FIELDS.saldo_final, 
                        respuestaRecarga.getSSaldoDespues().getValue());
                operacion.setField(FIELDS.status, 
                        STATUS.decode(STATUS.valueOf(wsResponse.getSEstatus().getValue())));
            }
            LogManager.debug(operacion);
            GycseDAO.updateOperacion(operacion);
           comprobante = new Comprobante(estacion).append(new Comprobante(GycseDAO.getRecarga(operacion.NVL(GycseOperacion.FIELDS.id.name()))));
        } catch (DetiPOSFault | IOException | GycseFault | DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error procesando transaccion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error insertando registro", ex.getMessage()));
        }

        return comprobante;
    }//getComprobante
}//TiempoAireAction