package com.detisa.omicrom.integraciones;

import com.google.gson.JsonObject;

public abstract class ImprimeIntegracionBase implements ImprimeIntegracion {
    
    protected JsonObject json;

    protected ImprimeIntegracionBase(JsonObject json) {
        this.json = json;
    }

    protected boolean exists(JsonObject object, String field) {
        return object.has(field) && !object.get(field).isJsonNull();
    }
    
    protected boolean exists(String field) {
        return exists(json, field);
    }

    protected String nvl(JsonObject object, String field, String def) {
        return exists(object, field) ? object.get(field).getAsString() : def;
    }

    protected String nvl(JsonObject object, String field) {
        return nvl(object, field, "");
    }

    protected String nvl(String field, String def) {
        return nvl(json, field, def);
    }

    protected String nvl(String field) {
        return nvl(json, field);
    }
}
