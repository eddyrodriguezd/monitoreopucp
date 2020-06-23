package com.monitoreopucp.utilities;

import com.monitoreopucp.entities.Anotacion;

public class DtoAnotacion {

    private Anotacion[] lista;

    public DtoAnotacion() {
    }

    public DtoAnotacion(Anotacion[] lista) {
        this.lista = lista;
    }

    public Anotacion[] getLista() {
        return lista;
    }

    public void setLista(Anotacion[] lista) {
        this.lista = lista;
    }
}
