package com.monitoreopucp.entities;

import java.io.Serializable;

public class Usuario implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private String correo;
    private int codigo;
    private boolean infra;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String apellido, String correo, int codigo, boolean infra) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.codigo = codigo;
        this.infra = infra;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public boolean isInfra() {
        return infra;
    }

    public void setInfra(boolean infra) {
        this.infra = infra;
    }
}