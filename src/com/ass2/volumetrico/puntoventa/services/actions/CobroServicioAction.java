/*
 *  CobroServicioAction
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
import static com.ass2.volumetrico.puntoventa.data.GycseOperacion.*;
import com.ass2.volumetrico.puntoventa.data.GycseOperacion;
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
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsResPagoServicios;
import org.datacontract.schemas._2004._07.gycse_btservicios.GYCSEInfoRouteadorClsRespuesta;
import serviciosame.RMIClientFactory.STATUS;

public class CobroServicioAction extends BaseAction {

    public static final String RMI_PROPERTIES = "samermi.properties";
    public static final String RMI_ENDPOINT = "same.endpoint";

    public static final String PRMTR_CODIGO_SERVICIO = "SERVICIO";
    public static final String PRMTR_CODIGO_PRODUCTO = "PRODUCTO";
    public static final String PRMTR_LINEA_DE_CAPTURA = "LINEA_DE_CAPTURA";
    public static final String PRMTR_IMPORTE = "IMPORTE";
    public static final String PRMTR_AUTH = "PASSWORD";
    public static final String PRMTR_FORMA_PAGO = "FORMA_PAG";
    public static final String PRMTR_AUTH_BANCO = "AUTH_BANCO";

    static {
        DateUtils.registerDateFormat("yyyy-MM-dd HH:mm:ssz");
    }

    public CobroServicioAction(DinamicVO<String, String> param) throws DetiPOSFault {
        super(param);
    }

    @Override
    public DetiPOSAction validateRequest() throws DetiPOSFault {
        if (!"1".equals(VariablesDAO.getCorporativo("gycse_enabled", "0"))) {
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, "Error de configuracion.", "Cobro de servicios no habilitado"));
        }
        if (parameters.isNVL(PRMTR_FORMA_PAGO)
                || (Integer.parseInt(parameters.NVL(PRMTR_FORMA_PAGO))>0 && parameters.isNVL(PRMTR_AUTH_BANCO)))
                throw new DetiPOSFault("Error", new DetiPOSFaultInfo(DetiPOSFaultInfo.PRM_ERROR, "Error de parametros.", "Se requiere Forma de Pago y Numero de Autorizacion Bancaria"));
        return super.validateRequest();
    }//validatreRequest

    @Override
    public Comprobante getComprobante() throws DetiPOSFault {
        GycseOperacion operacion;
        GycseClient client;
        DinamicVO<String, String> despachador;
        GYCSEInfoRouteadorClsRespuesta wsResponse;
        GycseRepositorioWS repositorioWS;
        String fechaRespuesta;
        Comprobante comprobante = new Comprobante();

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
            operacion = GycseOperacion.newServicio("VENTANILLA".equals(parameters.NVL(PRMTR_AUTH)) ? "0" : despachador.NVL("id"),
                    parameters.NVL(PRMTR_CODIGO_SERVICIO),
                    parameters.NVL(PRMTR_CODIGO_PRODUCTO),
                    parameters.NVL(PRMTR_LINEA_DE_CAPTURA),
                    parameters.NVL(PRMTR_IMPORTE),
                    parameters.NVL(PRMTR_FORMA_PAGO),
                    parameters.NVL(PRMTR_AUTH_BANCO),
                    client.getEndpoint().NVL(GycseEndpoints.FIELDS.pruebas.name()));
            LogManager.info("Solicitando autorización " + operacion.NVL(GycseOperacion.FIELDS.id.name()));
            wsResponse = client.invokeServicio(
                    operacion.NVL(FIELDS.operador.name()),
                    parameters.NVL(PRMTR_CODIGO_SERVICIO), 
                    parameters.NVL(PRMTR_CODIGO_PRODUCTO), 
                    operacion.NVL(FIELDS.peticion.name()),
                    parameters.NVL(PRMTR_LINEA_DE_CAPTURA), 
                    Integer.valueOf(parameters.NVL(PRMTR_IMPORTE)));

            fechaRespuesta = DateUtils.fncsFormat(
                    "yyyy-MM-dd HH:mm:ssz",
                    wsResponse.getDtFechaRespuesta().toGregorianCalendar(),
                    DateUtils.REGIONAL_MEXICO, DateUtils.TIMEZONE_MEXICO);

            GYCSEInfoRouteadorClsResPagoServicios respuestaServicio = wsResponse.getClsResPagoServicios().getValue();
            GYCSEInfoRouteadorClsResConsultaRecarga respuestaConsulta = wsResponse.getClsResConsultaRecarga().getValue();

            LogManager.debug(respuestaServicio);
            LogManager.debug(respuestaConsulta);
            if (null==respuestaServicio.getSAutorizacion().getValue()) {
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
                        respuestaServicio.getSAutorizacion().getValue());
                operacion.setField(FIELDS.codigo_respuesta,
                        StringUtils.fncsLeftPadding(
                                respuestaServicio.getSCodigoRespuesta().getValue()
                        ,'0', 2));
                operacion.setField(FIELDS.mensaje_respuesta,
                        respuestaServicio.getSMensajeFinal().getValue());
                operacion.setField(FIELDS.transaccion,
                        respuestaServicio.getSTransaccion().getValue());
                operacion.setField(FIELDS.saldo_inicial, 
                        respuestaServicio.getSSaldoAntes().getValue());
                operacion.setField(FIELDS.saldo_final, 
                        respuestaServicio.getSSaldoDespues().getValue());
                operacion.setField(FIELDS.status,
                        STATUS.decode(STATUS.valueOf(wsResponse.getSEstatus().getValue())));
            }
            LogManager.debug(operacion);
            GycseDAO.updateOperacion(operacion);
            new Comprobante(estacion).append("TIPO_IMPRESION", "COPIA CLIENTE").append(new Comprobante(GycseDAO.getServicio(operacion.NVL(GycseOperacion.FIELDS.id.name()))));
        } catch (IOException | DBException | GycseFault ex) {
            LogManager.error(ex);
            LogManager.info("Trace", ex);
            throw new DetiPOSFault("Error procesando transaccion", new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, "Error insertando registro", ex.getMessage()));
        }
        return comprobante;
    }//getComprobante
}//CobroServicioAction