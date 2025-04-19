package com.example.esporthub_app.modelos;

import java.util.List;

public class Torneo {
    private String idTorneo;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private List<Equipo> participantes;
    private String estado;
    private int maxParticipantes;

    public Torneo() {
    }

    public Torneo(String idTorneo, String nombre, String descripcion, String fechaInicio, String fechaFin, String estado, int maxParticipantes, List<Equipo> participantes) {
        this.idTorneo = idTorneo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.maxParticipantes = maxParticipantes;
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

    public int getMaxParticipantes() {
        return maxParticipantes;
    }

    public void setMaxParticipantes(int maxParticipantes) {
        this.maxParticipantes = maxParticipantes;
    }
}
