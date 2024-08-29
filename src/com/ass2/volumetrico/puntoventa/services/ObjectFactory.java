package com.ass2.volumetrico.puntoventa.services;

import com.ass2.volumetrico.puntoventa.services.exception.DetiPOSFaultInfo;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public CapturaDepositoRequest createCapturaDepositoRequest() {
        return new CapturaDepositoRequest();
    }

    public CheckUpdatesRequest createCheckUpdatesRequest() {
        return new CheckUpdatesRequest();
    }
    
    public CheckUpdatesResponse createCheckUpdatesResponse() {
        return new CheckUpdatesResponse();
    }
 
    public CobroServicioARequest createCobroServicioARequest() {
        return new CobroServicioARequest();
    }

    public CobroServicioRequest createCobroServicioRequest() {
        return new CobroServicioRequest();
    }
    
    public CobroTarjetaPosicionRequest createCobroTarjetaPosicionRequest() {
        return new CobroTarjetaPosicionRequest();
    }
    
    public ComprobanteResponse createComprobanteResponse() {
        return new ComprobanteResponse();
    }

    public CondicionesTarjetaRequest createCondicionesTarjetaRequest() {
        return new CondicionesTarjetaRequest();
    }

    public ConsultaBoletoRequest createConsultaBoletoRequest() {
        return new ConsultaBoletoRequest();
    }

    public ConsultaClienteByRFCRequest createConsultaClienteByRFCRequest() {
        return new ConsultaClienteByRFCRequest();
    }
    
    public ConsultaClienteRequest createConsultaClienteRequest() {
        return new ConsultaClienteRequest();
    }

    public ConsultaSaldoClienteRequest createConsultaSaldoClienteRequest() {
        return new ConsultaSaldoClienteRequest();
    }

    public ConsultaSaldoTarjetaRequest createConsultaSaldoTarjetaRequest() {
        return new ConsultaSaldoTarjetaRequest();
    }

    public ConsultaTarjetaRequest createConsultaTarjetaRequest() {
        return new ConsultaTarjetaRequest();
    }

    public ConsultaTiempoAireRequest createConsultaTiempoAireRequest() {
        return new ConsultaTiempoAireRequest();
    }

    public ConsumoIntegracionRequest createConsumoIntegracionRequest() {
        return new ConsumoIntegracionRequest();
    }

    public ConsumoRequest createConsumoRequest() {
        return new ConsumoRequest();
    }

    public CorteOmicromRequest createCorteOmicromRequest() {
        return new CorteOmicromRequest();
    }

    public CorteRequest createCorteRequest() {
        return new CorteRequest();
    }

    public GetConfigFilesRequest createGetConfigFilesRequest() {
        return new GetConfigFilesRequest();
    }

    public GetEstacionRequest createGetEstacionRequest() {
        return new GetEstacionRequest();
    }

    public GetFontsRequest createGetFontsRequest() {
        return new GetFontsRequest();
    }

    public GetLogoFacturacionRequest createGetLogoFacturacionRequest() {
        return new GetLogoFacturacionRequest();
    }

    public GetLogoRequest createGetLogoRequest() {
        return new GetLogoRequest();
    }

    public ImprimeComprobanteRequest createImprimeComprobanteRequest() {
        return new ImprimeComprobanteRequest();
    }

    public ImprimeCorteRequest createImprimeCorteRequest() {
        return new ImprimeCorteRequest();
    }

    public ImprimeDepositoRequest createImprimeDepositoRequest() {
        return new ImprimeDepositoRequest();
    }

    public ImprimeJarreoRequest createImprimeJarreoRequest() {
        return new ImprimeJarreoRequest();
    }

    public ImprimePuntosRequest createImprimePuntosRequest() {
        return new ImprimePuntosRequest();
    }

    public ImprimeTransaccionComplexRequest createImprimeTransaccionComplexRequest() {
        return new ImprimeTransaccionComplexRequest();
    }

    public ImprimeTransaccionRequest createImprimeTransaccionRequest() {
        return new ImprimeTransaccionRequest();
    }

    public InventarioAditivosRequest createInventarioAditivosRequest() {
        return new InventarioAditivosRequest();
    }

    public InventarioTanquesRequest createInventarioTanquesRequest() {
        return new InventarioTanquesRequest();
    }//createInventarioTanquesRequest

    public ListMontosTARequest createListMontosTARequest() {
        return new ListMontosTARequest();
    }

    public ListServiciosRequest createListServiciosRequest() {
        return new ListServiciosRequest();
    }

    public MarcaCobroConsumoRequest createMarcaCobroConsumoRequest() {
        return new MarcaCobroConsumoRequest();
    }

    public RecuperaUltimosConsumosRequest createRecuperaUltimosConsumosRequest() {
        return new RecuperaUltimosConsumosRequest();
    }

    public RecuperaUltimosCortesRequest createRecuperaUltimosCortesRequest() {
        return new RecuperaUltimosCortesRequest();
    }

    public RegistroTarjetaRequest createRegistroTarjetaRequest() {
        return new RegistroTarjetaRequest();
    }

    public RegistroTarjetasDTO createRegistroTarjetasDTO() {
        return new RegistroTarjetasDTO();
    }

    public SaldoClienteRequest createSaldoClienteRequest() {
        return new SaldoClienteRequest();
    }
    
    public TiempoAireARequest createTiempoAireARequest() {
        return new TiempoAireARequest();
    }

    public TiempoAireRequest createTiempoAireRequest() {
        return new TiempoAireRequest();
    }

    public UpdateBoletoRequest createUpdateBoletoRequest() {
        return new UpdateBoletoRequest();
    }

    public UpdateDTO createUpdateDTO() {
        return new UpdateDTO();
    }

    public ValesRequest createValesRequest() {
        return new ValesRequest();
    }

    public VentaAditivo createVentaAditivo() {
        return new VentaAditivo();
    }

    public VentaAditivosRequest createVentaAditivosRequest() {
        return new VentaAditivosRequest();
    }

    public VentaDivisasRequest createVentaDivisasRequest() {
        return new VentaDivisasRequest();
    }
}
