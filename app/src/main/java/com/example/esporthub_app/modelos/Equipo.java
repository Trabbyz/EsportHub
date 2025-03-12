package com.example.esporthub_app.modelos;

import java.util.List;

public class Equipo {
    private String nombre;
    private List<Jugador> miembros;
    private int maxJugadores;
    private List<Partido> partidos;

    public Equipo() {
    }

    public Equipo(int maxJugadores, List<Jugador> miembros, String nombre, List<Partido> partidos) {
        this.maxJugadores = maxJugadores;
        this.miembros = miembros;
        this.nombre = nombre;
        this.partidos = partidos;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public void setMaxJugadores(int maxJugadores) {
        this.maxJugadores = maxJugadores;
    }

    public List<Jugador> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<Jugador> miembros) {
        this.miembros = miembros;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }
}
