/*
 * CorteOmicrom
 * ASS2PuntoVenta®
 * © 2024, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2017
 */
package com.detisa.omicrom.bussiness;

import com.detisa.omicrom.bussiness.corte.Common;
import com.ass2.volumetrico.puntoventa.data.CorteDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleDAO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleVO;
import com.ass2.volumetrico.puntoventa.data.CorteDetalleVO.CDT_FIELDS;
import com.ass2.volumetrico.puntoventa.data.CorteVO;
import com.ass2.volumetrico.puntoventa.data.CorteVO.CT_FIELDS;
import com.softcoatl.utils.logging.LogManager;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 *
 * @author Rolando Esquivel
 */
public class CorteOmicrom {

    @Getter private CorteVO corte;
    @Getter private List<CorteDetalleVO> detalles = new ArrayList <> ();

    public CorteOmicrom(String isla, boolean openOnly) throws DetiPOSFault {

        DinamicVO<String, String> stat = Common.getStatusIsla(isla);
        String ctid;
        if (openOnly) {

            if (!"0".equals(stat.NVL("id")) && "Abierto".equals(stat.NVL("cst"))) {
                throw new DetiPOSFault("La isla ya fue abierta con el corte "+stat.isNVL("id"));
            }

            ctid = CorteDAO.getUltimoCorteCerrado(isla);
        } else {
            ctid = CorteDAO.getCorteAbierto(isla);
        }

        if ("0".equals(ctid)) {
            LogManager.info("No existe ningún corte. Creando corte inicial.");
            corte = new CorteVO();
            corte.setField(CT_FIELDS.isla, isla);
            corte.setField(CT_FIELDS.id, "1");
            detalles = CorteDetalleDAO.getDetallesZeroCorte(isla);
        } else {
            corte = CorteDAO.getCorte(ctid);
            detalles = CorteDetalleDAO.getDetallesCorte(ctid);
        }
    }

    public CorteDetalleVO getDetalle(String posicion) {
        return detalles.stream()
                .filter(detalle -> (detalle.NVL(CDT_FIELDS.posicion.name()).equals(posicion)))
                .collect(Collectors.collectingAndThen(Collectors.toList(), items -> items.isEmpty() ? new CorteDetalleVO() : items.get(0)));
    }

    public void reload() throws DetiPOSFault {
        try {
            corte = CorteDAO.getCorte(isla(), id());
            detalles = CorteDetalleDAO.getDetallesCorte(id());
        } finally {
            LogManager.info(corte);
            detalles.forEach((CorteDetalleVO detalle) -> LogManager.info(detalle));
        }
    }

    public CorteDetalleVO cloneDetalle(String corte, String posicion) {

        return detalles.stream()
                .filter(detalle -> (detalle.NVL(CDT_FIELDS.posicion.name()).equals(posicion)))
                .map(detalle -> {
            CorteDetalleVO cloned = new CorteDetalleVO();
            cloned.setEntries(detalle);
            cloned.setId(corte);
            cloned.setIdnvo("");
            cloned.setImonto1(detalle.getFmonto1());
            cloned.setImonto2(detalle.getFmonto2());
            cloned.setImonto3(detalle.getFmonto3());
            cloned.setIvolumen1(detalle.getFvolumen1());
            cloned.setIvolumen2(detalle.getFvolumen2());
            cloned.setIvolumen3(detalle.getFvolumen3());
            return cloned;
        }).findFirst().get();
    }

    public String id() {
        return corte.NVL(CT_FIELDS.id.name());
    }
    public String isla() {
        return corte.NVL(CT_FIELDS.isla.name());
    }

    @Override
    public String toString() {
        return "CorteOmicrom{" + "corte=" + corte + ", detalles=" + detalles + '}';
    }
        
}
