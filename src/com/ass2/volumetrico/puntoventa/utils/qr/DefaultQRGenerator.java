/*
 * DefaultQRGenerator
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

public class DefaultQRGenerator implements QRGenerator {

    @Override
    public DinamicVO<String, String> getQR(int id) {
        DinamicVO<String, String> data = new DinamicVO<>();
        try {
            String sql =
                    "SELECT qr.codigo, CONCAT( a.url, IF( b.fact = 1, CONCAT( 'rfc.php?codigo=', qr.codigo, '&ticket=', rm.id ), '' ) ) qr " +
                    "FROM rm " +
                    "JOIN v_rm_code qr ON qr.id = rm.id " +
                    "JOIN ( SELECT CONVERT( IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'urlfact' ), 'http://10.101.22.172/ASS2CommonFact/' ) USING utf8 ) url ) a ON TRUE " +
                    "JOIN ( SELECT CONVERT( IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'fonline' ),  '1' ) USING utf8 ) fact ) b ON TRUE " +
                    "WHERE rm.id = " + id;
            DinamicVO <String, String> obj = OmicromSLQHelper.getUnique(sql);
            data.setField("FOLIO_FAE", obj.NVL("codigo"));
            data.setField("QRC_FAE", obj.NVL("qr"));
        } catch (DBException ex) {
            LogManager.error(ex);
        }
        return data;
    }
}
