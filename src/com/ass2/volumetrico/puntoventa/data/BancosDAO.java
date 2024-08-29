
/*
 * BancosDAO
 * ASS2PuntoVenta®
 * © 2018, ASS2
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2018
 */
package com.ass2.volumetrico.puntoventa.data;

import com.ass2.volumetrico.puntoventa.common.OmicromLogManager;
import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSDataBaseFaultInfo;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import java.util.List;

public class BancosDAO {
    
    private static final String MAIN_ERROR = "Error consultando la lista de bancos";

    public static List <DinamicVO<String, String>> getBancos() throws DetiPOSFault {
        String sql = "SELECT cli.id, unidades.flotilla flotilla, IF( cli.tipodepago REGEXP 'Tarjeta', '1', '0' ) factura, SUBSTRING( IF( alias IS NULL OR alias = '', nombre, alias ), 1, 30 ) alias "
                + "FROM cli "
                + "JOIN unidades ON unidades.cliente = cli.id AND unidades.codigo REGEXP unidades.cliente "
                + "WHERE cli.tipodepago REGEXP 'Tarjeta|Monedero|Vales|Reembolso' AND cli.observaciones NOT LIKE 'Autorizador' "
                + "AND cli.activo = 'Si'";

        LogManager.info("Consultando lista de bancos");
        LogManager.debug(sql);

        try {
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            OmicromLogManager.error(MAIN_ERROR, ex);
            throw new DetiPOSFault(MAIN_ERROR, new DetiPOSFaultInfo(DetiPOSFaultInfo.DBA_ERROR, ex, MAIN_ERROR));
        }
    }

    public static List <DinamicVO<String, String>> getMonederos() throws DetiPOSFault {
        String sql = "SELECT " +
                        "conf.idintegracion id, conf.title alias, conf.flotilla, conf.device, conf.benefits, conf.balance, " +
                        "conf.employee, conf.numeco, conf.nip auth, conf.nipsize, conf.nipview, conf.tanquelleno " +
                    "FROM cli " +
                    "JOIN conf_integraciones conf ON conf.idcliente = cli.id " +
                    "JOIN integraciones i ON i.clave = conf.idintegracion " +
                    "WHERE cli.tipodepago REGEXP 'Monedero'  AND cli.observaciones LIKE 'Autorizador' " +
                    "AND cli.activo = 'Si';";

        LogManager.info("Consultando lista de monederos");
        LogManager.debug(sql);
        try {
            return OmicromSLQHelper.executeQuery(sql);
        } catch (DBException ex) {
            OmicromLogManager.error(MAIN_ERROR, ex);
            throw new DetiPOSFault(MAIN_ERROR, new DetiPOSDataBaseFaultInfo(ex, MAIN_ERROR));
        }
    }
}
