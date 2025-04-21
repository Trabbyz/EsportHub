package com.example.esporthub_app.modelos;

import java.util.ArrayList;
import java.util.List;

public class Equipo {
    private String id;
    private String nombre;
    private String descripcion;
    private List<Jugador> miembros;
    private int maxJugadores;
    private List<Partido> partidos;
    private List<String> idMiembros;

    public Equipo() {
    }

    public Equipo(String nombre, String descripcion, List<Jugador> miembros, int maxJugadores, List<Partido> partidos, List<String> idMiembros) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.miembros = miembros;
        this.maxJugadores = maxJugadores;
        this.partidos = partidos;
        this.idMiembros = idMiembros;
    }

    public Equipo(String id,String nombre, String descripcion, List<Jugador> miembros, int maxJugadores, List<Partido> partidos,List<String> idMiembros) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.miembros = miembros;
        this.maxJugadores = maxJugadores;
        this.partidos = partidos;
        this.idMiembros = idMiembros;
    }

    public Equipo(String nombre,String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Equipo(String nombre, String descripcion, List<Jugador> miembros, int maxJugadores, List<String> idMiembros) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.miembros = miembros;
        this.maxJugadores = maxJugadores;
        this.idMiembros = idMiembros;
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

    public List<String> getIdMiembros() {
        return idMiembros != null ? idMiembros : new ArrayList<>();
    }

    public void setIdMiembros(List<String> idMiembros) {
        this.idMiembros = idMiembros;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
