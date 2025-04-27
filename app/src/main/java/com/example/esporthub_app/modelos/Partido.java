package com.example.esporthub_app.modelos;

public class Partido {
    private String idPartido;
    private String idTorneo;
    private String equipo1ID;
    private String equipo2ID;
    private String fecha;
    private String resultado;
    private String urlPartido;

    public Partido() {
    }

    public Partido(String equipo1ID, String equipo2ID, String fecha, String idPartido, String idTorneo, String resultado, String urlPartido) {
        this.equipo1ID = equipo1ID;
        this.equipo2ID = equipo2ID;
        this.fecha = fecha;
        this.idPartido = idPartido;
        this.idTorneo = idTorneo;
        this.resultado = resultado;
        this.urlPartido = urlPartido;
    }

    public Partido(String equipo1ID, String equipo2ID, String fecha, String resultado, String urlPartido) {
        this.equipo1ID = equipo1ID;
        this.equipo2ID = equipo2ID;
        this.fecha = fecha;
        this.resultado = resultado;
        this.urlPartido = urlPartido;
    }

    public String getEquipo1ID() {
        return equipo1ID;
    }

    public void setEquipo1ID(String equipo1ID) {
        this.equipo1ID = equipo1ID;
    }

    public String getEquipo2ID() {
        return equipo2ID;
    }

    public void setEquipo2ID(String equipo2ID) {
        this.equipo2ID = equipo2ID;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(String idPartido) {
        this.idPartido = idPartido;
    }

    public String getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(String idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getUrlPartido() {
        return urlPartido;
    }

    public void setUrlPartido(String urlPartido) {
        this.urlPartido = urlPartido;
    }
}

