package com.ass2.volumetrico.puntoventa.common;

import com.softcoatl.data.DinamicVO;
import com.softcoatl.database.entity.vo.CaseInsensitiveKeyMap;
import com.softcoatl.utils.StringUtils;
import com.softcoatl.utils.logging.LogManager;

public class OmicromVO extends DinamicVO<String, String> {

    public OmicromVO() {
        this.entries = new CaseInsensitiveKeyMap();
    }
    
    public OmicromVO(DinamicVO<String, String> vo) {
        LogManager.debug(vo);
        this.entries = new CaseInsensitiveKeyMap();
        vo.getEntries().entrySet().stream().forEach(item -> {
            this.entries.put(item.getKey(), null == item.getValue() ? item.getValue(): item.getValue().replaceAll("\\p{C}", ""));
        });
        LogManager.debug(this.entries);
    }

    @Override
    public String getField(String key) {
        return entries.containsKey(key) ? 
                entries.get(key) : "";
    }//getCampo

    @Override
    public boolean isNVL(String key) {
        return StringUtils.isNVL(getField(key));
    }

    @Override
    public String NVL(String field, String valDefault) {
        return isNVL(field) ? valDefault : getField(field);
    }//NVL

    @Override
    public String NVL(String psFieldID) {
        return NVL(psFieldID, "");
    }//NVL
}
