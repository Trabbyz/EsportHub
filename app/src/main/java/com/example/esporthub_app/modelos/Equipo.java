package com.example.esporthub_app.modelos;

import java.util.List;

public class Equipo {
    private String nombre;
    private String descripcion;
    private List<Jugador> miembros;
    private int maxJugadores;
    private List<Partido> partidos;

    public Equipo() {
    }

    public Equipo(String nombre, String descripcion, List<Jugador> miembros, int maxJugadores, List<Partido> partidos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.miembros = miembros;
        this.maxJugadores = maxJugadores;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
