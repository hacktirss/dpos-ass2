/*
 * ConsumoCliente
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.preset;

import com.detisa.fae.CheckBalance;
import com.detisa.fae.DataServer;
import com.detisa.fae.Exception_Exception;
import com.detisa.fae.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.data.ClientesBalanceVO;
import com.ass2.volumetrico.puntoventa.data.ClientesDAO;
import com.ass2.volumetrico.puntoventa.data.ClientesVO;
import com.ass2.volumetrico.puntoventa.data.ComandosDAO;
import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.CombustibleVO;
import com.ass2.volumetrico.puntoventa.data.ConsumosDAO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasDAO;
import com.ass2.volumetrico.puntoventa.data.ManguerasVO;
import com.ass2.volumetrico.puntoventa.data.UnidadVO;
import com.ass2.volumetrico.puntoventa.data.UnidadesDAO;
import com.ass2.volumetrico.puntoventa.data.VariablesDAO;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_BANK_ID;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_PIN_ACCOUNT;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSInternalFaultInfo;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSParametersFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import javax.xml.ws.WebServiceException;
import lombok.Getter;

public class ConsumoCliente extends Consumo {

    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    @Getter private final ClientesVO cliente;
    @Getter private final UnidadVO unidad;
    @Getter private final ComandosVO interes;

    public ConsumoCliente() {
        super();
        unidad = new UnidadVO();
        cliente = new ClientesVO();
        interes = new ComandosVO();
    }//Constructor

    private void initUnidad() throws DetiPOSFault {
        try {
            LogManager.info("Consultando unidad " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)));
            unidad.setEntries(UnidadesDAO.getUnidadV01(parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID))));
            LogManager.debug(unidad);
            if (unidad.isVoid()) {
                throw new DetiPOSFault("No existe el código " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)));
            }
            parameters.setField(PRMT_ACCOUNT, unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()));
            LogManager.info("Consultando cliente " + unidad.NVL(UnidadVO.UND_FIELDS.cliente.name()));
            cliente.setEntries(ClientesDAO.getClienteBalanceByID(unidad.NVL(UnidadVO.UND_FIELDS.cliente.name())));
            LogManager.debug(cliente);
        } catch (DetiPOSFault ex) {
            LogManager.info("Error iniciando Unidad");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando la tarjeta", new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, ex, "Consultando la tarjeta " + parameters.NVL(PRMT_ACCOUNT)));
        }
    }//initUnidad

    private void setInteres() {
        if ("g".equals(unidad.NVL("INTERES"))) {
            interes.setEntries(ComandosVO.gerencial(getManguera()));
        } else if (!"-".equals(unidad.NVL("INTERES")) && "1".equals(manguera.NVL("ENABLE")) && StringUtils.isNumber(unidad.NVL("INTERES"))) {
            interes.setEntries(ComandosVO.interes(getManguera(), unidad.NVL("INTERES")));
        }
    }

    private boolean execInteres() throws DetiPOSFault {
        if (!interes.isVoid() && manguera.getCampoAsInt(ManguerasVO.DSP_FIELDS.enable.name())>0) {
            ComandosDAO.create((ComandosVO) interes);
            if (ComandosDAO.isExecuted(interes.NVL(ComandosVO.CMD_FIELDS.id.name()), "1", "1", MAX_WAITING_TIME)
                    && !"P9750000".equals(interes.NVL("comando"))) {
                ManguerasDAO.updateFactor(getManguera().NVL("POSICION"), 
                        getManguera().NVL("MANGUERA"),
                        getUnidad().NVL("INTERES"));
            }//if executed
        }//is interes
        return true;
    }

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        initUnidad();
    }//init

    @Override
    public boolean exec() throws DetiPOSFault {
        execInteres();
        return super.exec();
    }//exec

    private void autorizacionLocal() throws DetiPOSFault {
        DinamicVO<String, String> permitido;

        try {
            LogManager.info("Consultando consumo permitido " + unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()));
            permitido = ConsumosDAO.getConsumoDisponible(
                            unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()),
                            unidad.NVL(UnidadVO.UND_FIELDS.periodo.name()));
            LogManager.debug(permitido);
            setInteres();
        } catch (DetiPOSFault ex) {
            LogManager.info("Error iniciando Unidad");
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault("Error consultando la tarjeta", new DetiPOSInternalFaultInfo(ex, "Consultando la tarjeta " + parameters.NVL(PRMT_ACCOUNT)));
        }

        if ("d".equals(unidad.NVL("ESTADO"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("El codigo " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)) + "no esta activo."));
        }

        if (cliente.isVoid()) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("El codigo " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)) + " no está asignado a un cliente o el cliente no existe."));
        }

        if ("No".equals(cliente.NVL("ACTIVO"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Cliente inactivo", "El cliente asignado al código " + unidad.NVL(UnidadVO.UND_FIELDS.codigo.name()) + " no esta activo."));
        }
        
        if ("Si".equals(unidad.NVL("PIDENIP")) 
                && !unidad.NVL("NIP").equals("-----") 
                && !parameters.NVL(PRMT_PIN_ACCOUNT).equals(unidad.NVL("NIP"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el NIP", "El NIP ingresado es incorrecto."));
        }

        if (!"S".equals(unidad.NVL("ALLOWED"))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "Codigo fuera del horario autorizado."));
        }

        if (!unidad.NVL("COMBUSTIBLE").contains("*")
                && !unidad.NVL("COMBUSTIBLE").contains(getCombustible().NVL(CombustibleVO.COM_FIELDS.descripcion.name()))) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "Combustible " + getCombustible().NVL(CombustibleVO.COM_FIELDS.descripcion.name()) + " no permitido"));
        }

        boolean fullRequest = isLleno();
        if ("B".equals(permitido.NVL("periodo"))) { // Tarjeta con saldo. No se valida ningún tipo de límite.
            if (getImporte().compareTo(new BigDecimal(permitido.NVL("consumo_permitido")))>0) {
                if (BigDecimal.ZERO.compareTo(new BigDecimal(permitido.NVL("consumo_permitido")))==0) {
                    throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "Tarjeta sin saldo."));
                } else if (fullRequest) {
                    LogManager.info("Tanque lleno, Estableciendo consumo como IMPORTE");
                    transform("IMPORTE", permitido.NVL("consumo_permitido"));
                    LogManager.debug(getComando());
                } else {
                    throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "Saldo insuficiente $" + permitido.NVL("consumo_permitido")));
                }
            }
        } else
        if ("S".equals(cliente.NVL("CHECK_IMPORTES"))) {
            // Validate card limits 
            if ((unidad.isNVL("IMPORTE") || "0".equals(unidad.NVL("IMPORTE"))) 
                    && (unidad.isNVL("LITROS") || "0".equals(unidad.NVL("LITROS")))) {
                throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "Tarjeta sin definir limites."));
            }

            // Fits to unit's limits
            if (fullRequest) {
                LogManager.info("Tanque lleno "+unidad.NVL("LITROS")+" lts. $"+unidad.NVL("IMPORTE"));
                if (!unidad.isNVL("IMPORTE") && !"0".equals(unidad.NVL("IMPORTE"))) {
                    transform("IMPORTE", unidad.NVL("IMPORTE"));
                } else if (!unidad.isNVL("LITROS") && !"0".equals(unidad.NVL("LITROS"))) {
                    transform("VOLUMEN", unidad.NVL("LITROS"));
                }
            }//fullRequest

            // Checks client's balance
            if ("S".equals(cliente.NVL("CHECK_BALANCE")) && getImporte().compareTo(new BigDecimal(cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")))>0) {
                if (fullRequest) {
                    LogManager.info("Tanque lleno");
                    // Fits to current balance
                    if (getVolumen().compareTo(BigDecimal.ONE)>=0
                            && new BigDecimal(cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")).compareTo(BigDecimal.ZERO)>0) {
                        LogManager.info("Estableciendo consumo como IMPORTE");
                        transform("IMPORTE", cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO.name()));
                        LogManager.debug(getComando());
                    } else {
                        throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Saldo insuficiente", "No es posible llenar el tanque, el saldo es " + cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")));
                    }
                } else {
                    throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Saldo insuficiente", "Saldo insuficiente " + cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")));
                }
            }

            if (!unidad.isNVL("IMPORTE") && !"0".equals(unidad.NVL("IMPORTE"))) {
                if (getImporte().compareTo(new BigDecimal(unidad.NVL("IMPORTE")))>0) {
                    throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "No puede consumir mas de $" + unidad.NVL("IMPORTE")));
                }
                if (getImporte().compareTo(new BigDecimal(permitido.NVL("consumo_permitido")))>0) {
                    if (fullRequest && BigDecimal.ZERO.compareTo(new BigDecimal(permitido.NVL("consumo_permitido")))<0) {
                        LogManager.info("Tanque lleno, Estableciendo consumo como IMPORTE");
                        transform("IMPORTE", permitido.NVL("consumo_permitido"));
                        LogManager.debug(getComando());
                    } else {
                        throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "No puede consumir mas de $" + unidad.NVL("IMPORTE") + " el mismo dia"));
                    }
                }
            } else if (!unidad.isNVL("LITROS") && !"0".equals(unidad.NVL("LITROS"))) {
                if (getVolumen().compareTo(new BigDecimal(unidad.NVL("LITROS")))>0) {
                    throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "No puede consumir mas de " + unidad.NVL("LITROS") + " lts."));
                }
                if (getVolumen().compareTo(new BigDecimal(permitido.NVL("volumen_permitido")))>0) {
                    if (fullRequest && BigDecimal.ZERO.compareTo(new BigDecimal(permitido.NVL("volumen_permitido")))<0) {
                        LogManager.debug("Tanque lleno. Estableciendo consumo como VOLUMEN");
                        transform("VOLUMEN", permitido.NVL("volumen_permitido"));
                        LogManager.debug(getComando());
                    } else {
                        throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Error en el codigo", "No puede consumir mas de $" + unidad.NVL("LITROS") + " litros el mismo dia"));
                    }
                }
            }

            if ("S".equals(cliente.NVL("CHECK_BALANCE"))) {
                setSaldo(new BigDecimal(cliente.NVL(ClientesBalanceVO.BAL_FIELDS.SALDO, "0")).subtract(getImporte()).toPlainString());
            }
        }
    }

    private void autorizacionCorporativo() throws DetiPOSFault {
        CheckBalance balance;
        DataServer port;

        try {

            port = RMIClientFactory.getDataServerPort(VariablesDAO.getCorporativo("url_sync_data"), "DataServer", "DataServer", 30000);
            balance = port.validaConsumo(
                            VariablesDAO.getIdFAE(), 
                            parameters.NVL(PRMT_ACCOUNT), 
                            parameters.NVL(PRMT_PIN_ACCOUNT), 
                            getImporte().doubleValue(), 
                            getVolumen().doubleValue(), 
                            getCombustible().NVL("ID"));

            BigDecimal montoAutorizado;
            BigDecimal volumenAutorizado;

            if ("L".equals(balance.getTipoMonto())) {

                volumenAutorizado = BigDecimal.valueOf(balance.getMontoAutorizado());
                montoAutorizado = volumenAutorizado.multiply(combustible.getCampoAsDecimal("PRECIO"), new MathContext(2, RoundingMode.HALF_EVEN));
            } else {

                montoAutorizado = BigDecimal.valueOf(balance.getMontoAutorizado());
                volumenAutorizado = montoAutorizado.divide(combustible.getCampoAsDecimal("PRECIO"), new MathContext(2, RoundingMode.HALF_EVEN));
            }

            if (montoAutorizado.compareTo(CARGA_MINIMA)<=0) {
                throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("El importe autorizado es menor a la carga minima definida en " + CARGA_MINIMA + " pesos"));
            }

            if (isLleno()) {
                transform("IMPORTE", montoAutorizado.toPlainString());
            } else {
                if (isImporte() && montoAutorizado.compareTo(getImporte())<0) {
                    transform("IMPORTE", montoAutorizado.toPlainString());
                } else if (isVolumen() && montoAutorizado.compareTo(getImporte())<0) {
                    transform("VOLUMEN", volumenAutorizado.toPlainString());
                }
            }//true
        } catch (WebServiceException | MalformedURLException | DetiPOSFault | Exception_Exception ex) {
            LogManager.info("Error solicitando autorización a Corporativo");
            LogManager.error(ex);
            LogManager.info("Trace", ex);
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSFaultInfo(DetiPOSFaultInfo.INT_ERROR, ex.getMessage(), ex.getMessage()));
        }
    }

    @Override
    public boolean validate() throws DetiPOSFault {
        super.validate();

        if (!isLleno() && getImporte().compareTo(BigDecimal.ZERO) <= 0 && getVolumen().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Se esperaba el Importe o el Volumen del Comsumo"));
        }

        if (unidad.isVoid() || unidad.isNVL(UnidadVO.UND_FIELDS.id.name())) {
            throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("El codigo " + parameters.NVL(PRMT_ACCOUNT, parameters.NVL(PRMT_BANK_ID)) + "no existe."));
        }

        if (unidad.getCampoAsInt("SIMULTANEO")==0) {
            int despachoActual = EstadoPosicionesDAO.checkCardCode(parameters.NVL(PRMT_ACCOUNT));
            if (despachoActual!=0) {
                throw new DetiPOSFault(ERR_PRESET, new DetiPOSParametersFaultInfo("Codigo en uso", "El codigo se encuentra consumiendo en la posicion " + despachoActual));
            }
        }

        if ("1".equals(cliente.NVL("CORPORATIVO"))) {
            autorizacionCorporativo();
        } else {
            autorizacionLocal();
        }

        determineComando();
        return true;
    }//validate

    @Override
    public String toString() {
        return new StringBuilder("Consumo ").append(this.getClass().getName()).append("[MANGUERA[").append(manguera)
                .append("], ISLA[").append(isla)
                .append("], POSICION[").append(posicion)
                .append("], COMBUSTIBLE[").append(combustible)
                .append("], COMANDO[").append(comando)
                .append("], UNIDAD[").append(unidad)
                .append("], CLIENTE[").append(cliente)
                .append("], INTERES[").append(interes)
                .append("]]").toString();
    }//toString
}//ConsumoCliente
