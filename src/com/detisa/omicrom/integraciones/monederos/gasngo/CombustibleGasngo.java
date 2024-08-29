package com.detisa.omicrom.integraciones.monederos.gasngo;

public class CombustibleGasngo {

    public static String mapping(String codigo) {
        switch (codigo) {
            case "34006": return "D";
            case "32011": return "M";
            case "32012": return "P";
            default: return "M";
        }
    }

    public static boolean match(String condiciones, String combustible) {
        return condiciones.contains(mapping(combustible));
    }
}
