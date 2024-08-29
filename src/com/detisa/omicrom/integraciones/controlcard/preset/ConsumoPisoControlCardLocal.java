package com.detisa.omicrom.integraciones.controlcard.preset;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;
import com.ass2.volumetrico.puntoventa.data.EstadoPosicionesVO;
import com.ass2.volumetrico.puntoventa.data.VendedoresDAO;
import com.ass2.volumetrico.puntoventa.data.VendedoresVO;
import com.ass2.volumetrico.puntoventa.preset.Consumo;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_ACCOUNT;
import static com.ass2.volumetrico.puntoventa.preset.Consumo.PRMT_CNS_TYP;
import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.database.DBException;
import com.softcoatl.data.DinamicVO;
import java.math.BigDecimal;

public class ConsumoPisoControlCardLocal extends Consumo {

    private DinamicVO<String, String> vendedor;
    private static final BigDecimal CARGA_MINIMA = new BigDecimal(1);

    public ConsumoPisoControlCardLocal() {
        super();
    }//Constructor

    @Override
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault {
        super.init(parameters);
        try {
            vendedor = VendedoresDAO.getByAlias(parameters.NVL(PRMT_EMPLOYEE));
        } catch (DBException ex) {
            throw new DetiPOSFault(ex.getMessage());
        }
    }//init

    @Override
    public boolean exec() throws DetiPOSFault {
        return super.exec();
    }//exec

    @Override
    public boolean validate() throws DetiPOSFault {

        boolean validated = super.validate();
        if (!"Si".equals(vendedor.NVL(VendedoresVO.VEN_FIELDS.activo.name()))) {
            throw new DetiPOSFault("El vendedor no esta activo");
        }
        if (parameters.isNVL(PRMT_PIN_EMPLOYEE)
                || !parameters.NVL(PRMT_PIN_EMPLOYEE).equals(vendedor.NVL(VendedoresVO.VEN_FIELDS.nip.name()))) {
            throw new DetiPOSFault("El NIP del vendedor es incorrecto");
        }

        comando = ComandosVO.parse(manguera, parameters.NVL(PRMT_CNS_TYP), getCant().toPlainString(), "auto=" + parameters.NVL(PRMT_CNS_FP) + "|" + parameters.NVL(PRMT_ACCOUNT), false);
        posicion.setField(EstadoPosicionesVO.EP_FIELDS.codigo, "auto=" + parameters.NVL(PRMT_CNS_FP) + "|" + parameters.NVL(PRMT_ACCOUNT));
        comando.setField(ComandosVO.CMD_FIELDS.idtarea.name(), "0");
    
        return validated;
    }
}