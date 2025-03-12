package com.example.esporthub_app.modelos;

import java.util.List;

public class Jugador {
    private String idJugador;
    private String idEquipo;
    private String rolJuego;
    private List<Equipo> equiposFavoritos;

    public Jugador() {
    }

    public Jugador(List<Equipo> equiposFavoritos, String idJugador, String idEquipo, String rolJuego) {
        this.equiposFavoritos = equiposFavoritos;
        this.idJugador = idJugador;
        this.idEquipo = idEquipo;
        this.rolJuego = rolJuego;
    }

    public List<Equipo> getEquiposFavoritos() {
        return equiposFavoritos;
    }

    public void setEquiposFavoritos(List<Equipo> equiposFavoritos) {
        this.equiposFavoritos = equiposFavoritos;
    }

    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String id) {
        this.idJugador = id;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getRolJuego() {
        return rolJuego;
    }

    public void setRolJuego(String rolJuego) {
        this.rolJuego = rolJuego;
    }
}
