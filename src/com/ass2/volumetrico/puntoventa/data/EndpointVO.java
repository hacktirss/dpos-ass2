
package com.ass2.volumetrico.puntoventa.data;

import lombok.Data;

@Data
public class EndpointVO {
    
    private int id_pac;
    private String clave_pac;
    private String nombre_pac;
    private String url_webservice;
    private String url_cancelacion;
    private String usuario;
    private String password;
    private String clave_aux;
    private String clave_aux2;
    private int activo;
    private int pruebas;
    private int prioridad;
    private String version;
    private String tipo;    
}
