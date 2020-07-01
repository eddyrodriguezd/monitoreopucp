package com.monitoreopucp.entities;

import java.io.Serializable;

public class Anotacion implements Serializable {

    private String idUsuario;
    private String contenido;

    public Anotacion(){

    }

    public Anotacion(String idUsuario, String contenido) {
        this.idUsuario = idUsuario;
        this.contenido = contenido;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
