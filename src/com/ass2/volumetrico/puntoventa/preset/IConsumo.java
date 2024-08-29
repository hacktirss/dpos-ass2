package com.ass2.volumetrico.puntoventa.preset;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFault;
import com.softcoatl.data.DinamicVO;

public interface IConsumo {
    public void init(DinamicVO<String, String> parameters) throws DetiPOSFault;
    public boolean exec() throws DetiPOSFault;
    public boolean cancel();
    public boolean validate() throws DetiPOSFault;
}
