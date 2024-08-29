package com.ass2.volumetrico.puntoventa.services;

import com.softcoatl.utils.PropertyLoader;
import java.util.Properties;

/**
 *
 * @author ROLANDO
 */
public class RMIConfiguration {
    private static final RMIConfiguration INSTANCE = new RMIConfiguration();

    public static final String RMI_PROPERTIES = "dposrmi.properties";

    public static final String RMI_CORPORATIVO = "dpos.rmi.omicrom.endpoint";

    public static final String RMI_CONSULTA_CLIENTE = "dpos.rmi.query.cliente.endpoint";
    public static final String RMI_CONSULTA_TARJETA = "dpos.rmi.query.tarjeta.endpoint";

    public static final String RMI_CONSULTA_SALDO_CLIENTE = "dpos.rmi.balance.cliente.endpoint";
    public static final String RMI_CONSULTA_SALDO_TARJETA = "dpos.rmi.balance.tarjeta.endpoint";
    public static final String RMI_CONSULTA_SALDO_BOLETOS = "dpos.rmi.balance.boletos.endpoint";

    private static  final Properties properties = PropertyLoader.load(RMI_PROPERTIES);

    private RMIConfiguration() {}

    public static RMIConfiguration getInstance() { return INSTANCE; }

    public String getClienteEndpoint() {
        return properties.getProperty(RMI_CONSULTA_CLIENTE);
    }//getClienteEndpoint
    
    public String getTarjetaEndpoint() {
        return properties.getProperty(RMI_CONSULTA_TARJETA);
    }//getTarjetaEndpoint
    
    public String getCorporativoEndpoint() {
        return properties.getProperty(RMI_CORPORATIVO);
    }//getTarjetaEndpoint

    public String getSaldoClienteEndpoint() {
        return properties.getProperty(RMI_CONSULTA_SALDO_CLIENTE);
    }//getSaldoClienteEndpoint
    
    public String getSaldoTarjetaEndpoint() {
        return properties.getProperty(RMI_CONSULTA_SALDO_TARJETA);
    }//getSaldoTarjetaEndpoint
    
    public String getSaldoBoletoEndpoint() {
        return properties.getProperty(RMI_CONSULTA_SALDO_BOLETOS);
    }//getSaldoBoletoEndpoint
}//RMIConfiguration
