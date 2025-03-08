package com.example.esporthub_app.models;

import java.util.List;

public class Torneo {
    private String idTorneo;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private List<Equipo> participantes;
    private String estado;

    public Torneo() {
    }

    public Torneo(String descripcion, String estado, String fechaFin, String fechaInicio, String idTorneo, String nombre, List<Equipo> participantes) {
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaFin = fechaFin;
        this.fechaInicio = fechaInicio;
        this.idTorneo = idTorneo;
        this.nombre = nombre;
        this.participantes = participantes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(String idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Equipo> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Equipo> participantes) {
        this.participantes = participantes;
    }
}
