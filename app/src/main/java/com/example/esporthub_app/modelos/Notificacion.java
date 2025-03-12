package com.example.esporthub_app.modelos;

public class Notificacion {
    private String idNotificacion;
    private String idJugador;
    private String mensaje;

    public Notificacion() {
    }

    public Notificacion(String idJugador, String idNotificacion, String mensaje) {
        this.idJugador = idJugador;
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
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
}
