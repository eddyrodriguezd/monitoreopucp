package com.monitoreopucp.entities;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Incidencia implements Serializable {
    private String id;
    private int idUsuario;
    private String idFoto;//era int y lo cambie a String (JA)
    private String titulo;
    private String descripcion;
    private double latitud;
    private double longitud;
    private List<Anotacion> anotaciones;
    private Date fechaRegistro;
    private Date fechaRevision;
    private String estado;

    public Incidencia() {
    }

    public Incidencia(String id, int idUsuario, String idFoto, String titulo, String descripcion,
                      double latitud, double longitud, List<Anotacion> anotaciones, Date fechaRegistro,
                      Date fechaRevision, String estado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idFoto = idFoto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.anotaciones = anotaciones;
        this.fechaRegistro = fechaRegistro;
        this.fechaRevision = fechaRevision;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public List<Anotacion> getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(List<Anotacion> anotaciones) {
        this.anotaciones = anotaciones;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    //GEO-POINT
    public void setUbicacion(GeoPoint ubicacion){
        setLatitud(ubicacion.getLatitude());
        setLongitud(ubicacion.getLongitude());
    }
}
