package com.monitoreopucp.utilities;

import com.monitoreopucp.entities.Incidencia;

public class DtoIncidencias {

    private Incidencia[] lista;

    public DtoIncidencias() {
    }

    public DtoIncidencias(Incidencia[] lista) {
        this.lista = lista;
    }

    public Incidencia[] getLista() {
        return lista;
    }

    public void setLista(Incidencia[] lista) {
        this.lista = lista;
    }
}
