package com.example.esporthub_app.modelos;

public class Usuario {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rolUsuario;
    private String rolJuego;

    public Usuario() {
    }

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public Usuario(String nombre, String apellido, String email, String password, String rolUsuario, String rolJuego) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rolUsuario = rolUsuario;
        this.rolJuego = rolJuego;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public String getRolJuego() {
        return rolJuego;
    }

    public void setRolJuego(String rolJuego) {
        this.rolJuego = rolJuego;
    }
}
