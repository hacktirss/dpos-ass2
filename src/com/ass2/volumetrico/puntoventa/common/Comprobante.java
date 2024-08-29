package com.ass2.volumetrico.puntoventa.common;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.StringUtils;

public class Comprobante {

    private final DinamicVO<String, String> cmp = new DinamicVO<>();

    public Comprobante() {
    }

    public Comprobante(DinamicVO<String, String> comprobante, String pBelongsTO, String pFieldID, String pPrefix) {
        String belongsTO = StringUtils.NVL(pBelongsTO);
        String fieldID = StringUtils.NVL(pFieldID);
        String prefix = StringUtils.NVL(pPrefix);

        comprobante.getEntries().keySet().forEach((String field) -> {
            String key;
            if (!comprobante.isNVL(field)) {
                if (StringUtils.isNVL(pBelongsTO)) {
                    key = field;
                } else if (fieldID.equals(field)) {
                    key = belongsTO;
                } else {
                    key = field + "@" + belongsTO;
                }
                appendField(prefix, key, comprobante.NVL(field));
            }
        });
    }//toString

    public Comprobante(DinamicVO<String, String> comprobante, String prefix) {
        this(comprobante, "", "", prefix);
    }//toString

    public Comprobante(DinamicVO<String, String> comprobante) {
        this(comprobante, "", "", "");
    }//toString

    public DinamicVO<String, String> getCampos() {
        return cmp;
    }

    private void appendField(String prefix, String key, String value) {
        cmp.setField(prefix + key, value);
    }

    public Comprobante append(String key, String value) {
        appendField("", key, value);
        return this;
    }

    public Comprobante append(Comprobante comprobante) {
        this.cmp.setEntries(comprobante.cmp);
        return this;
    }

    public String serialize(String separador, String asignador) {
        StringBuilder serialized = new StringBuilder();
        cmp.getEntries().keySet().forEach((String field) -> {
            serialized.append(serialized.length() == 0 ? "" : separador).append(field).append(asignador).append(cmp.NVL(field));
        });
        LogManager.info(StringUtils.stripAccents(serialized.toString()));
        return StringUtils.stripAccents(serialized.toString());
    }

    public String serialize() {
        return serialize("&", "=");
    }

    public static DinamicVO<String, String> deserialize(String scomprobante) {
        DinamicVO<String, String> comprobante = new DinamicVO<>();
        String[] entry;
        LogManager.debug("Parseando " + scomprobante);
        for (String field : scomprobante.split("[&]")) {
            entry = field.trim().split("[=]");
            if (2 == entry.length) {
                comprobante.setField(entry[0].trim(), entry[1].trim());
            }
        }
        LogManager.debug(comprobante);
        return comprobante;
    }//parseComprobante

    @Override
    public String toString() {
        LogManager.info("Comprobante{comprobante=" + cmp.getEntries() + "}");
        return "Comprobante{comprobante=" + cmp.getEntries() + '}';
    }
}//Comrpobante
