/*
 *  CorporateClient
 *  © 2015, Softcoatl
 *  @author Rolando Esquivel Villafaña, Softcoatl
 *  @version 1.0
 *  nov 16, 2015
 */

package mx.com.detisa.server;

import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.RMIClientFactory;
import com.ass2.volumetrico.puntoventa.services.RMIConfiguration;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;
import java.math.BigDecimal;
import java.net.MalformedURLException;

/**
 *
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since nov 16, 2015
 */
@Deprecated
public class CorporateClient {

    public DinamicVO<String, String> authConsumo(BigDecimal importe, BigDecimal volumen, String estacion, String producto, String codigo, String password, String tipo) throws DetiPOSFault {
        RMIConfiguration conf = RMIConfiguration.getInstance();
        DinamicVO<String, String> auth = new DinamicVO<>();
        OMICROMCorporateWSImpl validator;
        CheckBalance cb;

        try {
            LogManager.debug("Solicitando autorización");
            validator = RMIClientFactory.getOmicronCorporativoPort(conf.getCorporativoEndpoint());
            cb = validator.autorizaVenta("", "", importe, volumen, producto, codigo, password, tipo, estacion);

            LogManager.debug(cb.getIdAutoriza());
            LogManager.debug(cb.getIntereses());
            LogManager.debug(cb.getImporteAutorizado());

            if (!cb.isValid()) {
                throw new DetiPOSFault("Error desconocido");
            }

            auth.setField("ID", cb.getIdAutoriza());
            auth.setField("INTERES", cb.getIntereses());
            auth.setField("IMPORTE", null != cb.getImporteAutorizado() ? cb.getImporteAutorizado().toPlainString() : "0");

        } catch (MalformedURLException | Exception_Exception | DetiPOSFault ex) {
            LogManager.error(ex.getMessage());
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
        return auth;
    }
    public DinamicVO<String, String> getUnidad(String codigo, String nip) throws DetiPOSFault {
        RMIConfiguration conf = RMIConfiguration.getInstance();
        DinamicVO<String, String> unidad = new DinamicVO<>();
        OMICROMCorporateWSImpl validator;
        DatosClientePrepagoTO to;

        try {
            LogManager.debug("Solicitando Unidad");
            validator = RMIClientFactory.getOmicronCorporativoPort(conf.getCorporativoEndpoint());
            to = validator.obtenUnidad(codigo, nip);

            if (!StringUtils.isNVL(to.getCodigo())) {
                unidad.setField("ALLOWED", to.getAllowed());
                unidad.setField("CLIENTE", to.getCliente());
                unidad.setField("CODIGO", to.getCodigo());
                unidad.setField("COMBUSTIBLE", to.getCombustible());
                unidad.setField("CONCEPTO", to.getConcepto());
                unidad.setField("DESCRIPCION", to.getDescripcion());
                unidad.setField("DIA", to.getDia());
                unidad.setField("ESTADO", to.getEstado());
                unidad.setField("HORAF", to.getHoraf());
                unidad.setField("HORAI", to.getHorai());
                unidad.setField("ID", to.getId());
                unidad.setField("IDESTACION", to.getIdEstacion());
                unidad.setField("IMPORTE", to.getImporte());
                unidad.setField("IMPRESO", to.getImpreso());
                unidad.setField("INTERESES", to.getIntereses());
                unidad.setField("LITROS", to.getLitros());
                unidad.setField("NIP", to.getNip());
                unidad.setField("PIDENIP", to.getPidenip());
                unidad.setField("PLACAS", to.getPlacas());
                unidad.setField("PRODUCTO", to.getProducto());
                unidad.setField("REFERENCIA", to.getReferencia());
            }
            LogManager.debug(unidad);
        } catch (MalformedURLException | Exception_Exception ex) {
            LogManager.error(ex.getMessage());
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
        return unidad;
    }
    public boolean reconciliate(String id, String referencia, BigDecimal importe, BigDecimal volumen) throws DetiPOSFault {
        RMIConfiguration conf = RMIConfiguration.getInstance();
        OMICROMCorporateWSImpl validator;
        String auth;
        try {
            LogManager.debug("Solicitando reconciliación");
            validator = RMIClientFactory.getOmicronCorporativoPort(conf.getCorporativoEndpoint());
            auth = validator.confirmaVenta("", "", id, importe, volumen, referencia);
            LogManager.debug(auth);
            return true;
        } catch (MalformedURLException | Exception_Exception ex) {
            LogManager.error(ex.getMessage());
            LogManager.debug("Trace", ex);
            throw new DetiPOSFault(ex.getMessage());
        }
    }
}
