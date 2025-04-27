package com.example.esporthub_app.modelos;

public class Notificacion {
    private String idNotificacion;
    private String idJugador;
    private String titulo;
    private String mensaje;
    private String fecha;

    public Notificacion() {
    }

    public Notificacion(String idNotificacion, String idJugador, String titulo, String mensaje, String fecha) {
        this.idNotificacion = idNotificacion;
        this.idJugador = idJugador;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }


    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
    }

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
