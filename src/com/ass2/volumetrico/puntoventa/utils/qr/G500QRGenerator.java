/*
 * G500QRGenerator
 * ASS2PuntoVenta®
 * © 2023, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since feb 2023
 */
package com.ass2.volumetrico.puntoventa.utils.qr;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.logging.LogManager;

public class G500QRGenerator implements QRGenerator {

    @Override
    public DinamicVO<String, String> getQR(int id) {
        DinamicVO<String, String> data = new DinamicVO<>();
        try {
            String sql =
                        "SELECT qr.qrc FOLIO, to_base64( CONCAT( cu.permiso, '|', ( UNIX_TIMESTAMP( rm.fin_venta ) * 1000 ), '|' , rm.id, '|',  qr.qrc, '|', ROUND( rm.pesos, 2 ) ) ) QR " 
                    +   "FROM rm " 
                    +   "JOIN v_rm_qrfae qr ON qr.id = rm.id " 
                    +   "JOIN ( SELECT IFNULL( ( SELECT permiso FROM permisos_cre WHERE catalogo =  'VARIABLES_EMPRESA' AND llave = 'PERMISO_CRE' ), ''  ) permiso ) cu ON TRUE " 
                    +   "JOIN ( SELECT CONVERT( IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'url_facturacion' ), 'omicrom.com.mx/GlobalFAE' ) USING utf8 ) FURL ) url ON TRUE "
                    +   "WHERE rm.id = " + id;
            DinamicVO <String, String> obj = OmicromSLQHelper.getUnique(sql);
            data.setField("FOLIO_FAE", obj.NVL("FOLIO"));
            data.setField("QRC_FAE", obj.NVL("QR"));
        } catch (DBException ex) {
            LogManager.error(ex);
        }
        return data;
    }
}
