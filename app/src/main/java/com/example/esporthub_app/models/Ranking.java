package com.example.esporthub_app.models;

public class Ranking {
    private String idTorneo;
    private String idEquipo;
    private int puntos;
    private int posicion;

    public Ranking() {
    }

    public Ranking(String idEquipo, String idTorneo, int posicion, int puntos) {
        this.idEquipo = idEquipo;
        this.idTorneo = idTorneo;
        this.posicion = posicion;
        this.puntos = puntos;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(String idTorneo) {
        this.idTorneo = idTorneo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
