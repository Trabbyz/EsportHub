package com.example.esporthub_app.modelos;

import java.util.List;

public class Jugador {
    private String uid;
    private String nombre;
    private String equipoActual;
    private String rolJuego;
    private List<Equipo> equiposFavoritos;

    public Jugador() {
    }

    public Jugador(String id, String nombre, String rolJuego) {
        this.uid = id;
        this.nombre = nombre;
        this.rolJuego = rolJuego;
    }

    public Jugador(String uid,String nombre, String equipoActual, String rolJuego, List<Equipo> equiposFavoritos) {
        this.uid = uid;
        this.nombre = nombre;
        this.equipoActual = equipoActual;
        this.rolJuego = rolJuego;
        this.equiposFavoritos = equiposFavoritos;
    }

    public Jugador(String nombre, String rolJuego) {
        this.nombre = nombre;
        this.rolJuego = rolJuego;
    }

    public List<Equipo> getEquiposFavoritos() {
        return equiposFavoritos;
    }

    public void setEquiposFavoritos(List<Equipo> equiposFavoritos) {
        this.equiposFavoritos = equiposFavoritos;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }

    public String getEquipoActual() {
        return equipoActual;
    }

    public void setEquipoActual(String equipoActual) {
        this.equipoActual = equipoActual;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRolJuego() {
        return rolJuego;
    }

    public void setRolJuego(String rolJuego) {
        this.rolJuego = rolJuego;
    }


}
