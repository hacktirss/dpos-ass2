package com.ass2.volumetrico.puntoventa.services;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import mx.com.detisa.server.OMICROMCorporateWSImpl;

public class RMIClientFactory {

    public static final String DPOS_SERVICE_NAME = "DetiPOS";
    public static final String DPOS_NAMESPACE = "http://detisa.com/omicrom/services/";

    public static final String CORP_SERVICE_NAME = "OMICROMCorporateWSImplService";
    public static final String CORP_PORT_NAME = "OMICROMCorporateWSImplPort";

    public static final QName DPOS_QNAME = new QName(DPOS_NAMESPACE, DPOS_SERVICE_NAME);

    public static final QName CORP_SQNAME = new QName(OMICROMCorporateWSImpl.CORP_NAMESPACE, CORP_SERVICE_NAME);
    public static final QName CORP_PQNAME = new QName(OMICROMCorporateWSImpl.CORP_NAMESPACE, CORP_PORT_NAME);

    public static DetiPOSPort getDPOSPort(String endpoint) throws MalformedURLException {

        Service service = Service.create(new URL(endpoint), DPOS_QNAME);
        return service.getPort(DPOS_QNAME, DetiPOSPort.class);
    }//getDPOSPort
    public static OMICROMCorporateWSImpl getOmicronCorporativoPort(String endpoint) throws MalformedURLException {

        Service service = Service.create(new URL(endpoint), CORP_SQNAME);
        return service.getPort(CORP_PQNAME, OMICROMCorporateWSImpl.class);
    }//getDPOSPort
}//RMIClientFactory
