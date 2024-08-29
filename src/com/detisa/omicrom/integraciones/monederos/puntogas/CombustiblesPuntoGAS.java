
package com.detisa.omicrom.integraciones.monederos.puntogas;

public class CombustiblesPuntoGAS {

    public static String mapping(String codigo) {
        switch (codigo) {
            case "34006": return "Diesel";
            case "32011": return "Magna";
            case "32012": return "Premium";
            default: return "Magna";
        }
    }

    public static boolean match(String condiciones, String combustible) {
        return condiciones.contains(mapping(combustible));
    }
}
