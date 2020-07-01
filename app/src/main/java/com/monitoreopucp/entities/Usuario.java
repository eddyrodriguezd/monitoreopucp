package com.monitoreopucp.entities;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String id;
    private String nombre;
    private String apellido;
    private String correo;
    private String codigo;
    private boolean infra;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String apellido, String correo, String codigo, boolean infra) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.codigo = codigo;
        this.infra = infra;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public boolean isInfra() {
        return infra;
    }

    public void setInfra(boolean infra) {
        this.infra = infra;
    }
}