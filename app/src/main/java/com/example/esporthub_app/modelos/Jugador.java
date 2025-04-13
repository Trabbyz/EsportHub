package com.example.esporthub_app.modelos;

import java.util.List;

public class Jugador {
    private String nombre;
    private String idJugador;
    private String idEquipo;
    private String rolJuego;
    private List<Equipo> equiposFavoritos;

    public Jugador() {
    }

    public Jugador(String nombre, String idJugador, String idEquipo, String rolJuego, List<Equipo> equiposFavoritos) {
        this.nombre = nombre;
        this.idJugador = idJugador;
        this.idEquipo = idEquipo;
        this.rolJuego = rolJuego;
        this.equiposFavoritos = equiposFavoritos;
    }

    public List<Equipo> getEquiposFavoritos() {
        return equiposFavoritos;
    }

    public void setEquiposFavoritos(List<Equipo> equiposFavoritos) {
        this.equiposFavoritos = equiposFavoritos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
