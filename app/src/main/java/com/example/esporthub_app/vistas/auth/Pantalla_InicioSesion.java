package com.example.esporthub_app.vistas.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporthub_app.R;
import com.example.esporthub_app.vistas.home.Pantalla_InicioAdministradores;
import com.example.esporthub_app.vistas.home.Pantalla_InicioJugadores;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Pantalla_InicioSesion extends AppCompatActivity {
    private EditText correo, contrasena;
    private Button botonInicioSesion,botonRegistro;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaInicioSesion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        correo = findViewById(R.id.editTextCorreo);
        contrasena = findViewById(R.id.editTextPassword);
        botonInicioSesion = findViewById(R.id.botonInicioSesion);
        botonRegistro = findViewById(R.id.botonRegistro);

        firebaseAuth = FirebaseAuth.getInstance();

        botonRegistro.setOnClickListener(view -> {
            Intent intent = new Intent(Pantalla_InicioSesion.this, Pantalla_Registro.class);
            startActivity(intent);
        });

        botonInicioSesion.setOnClickListener(this::iniciarSesion);
    }

    private void iniciarSesion(View view) {
        String usuarioIntroducido = correo.getText().toString().trim();
        String contrasenaIntroducida = contrasena.getText().toString().trim();

        // Validar campos
        if (usuarioIntroducido.isEmpty()) {
            Snackbar.make(view, "Por favor, ingrese un correo electrónico", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (contrasenaIntroducida.isEmpty()) {
            Snackbar.make(view, "Por favor, ingrese una contraseña", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (contrasenaIntroducida.length() < 6) {
            Snackbar.make(view, "La contraseña debe tener al menos 6 caracteres", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Autenticación con Firebase
        firebaseAuth.signInWithEmailAndPassword(usuarioIntroducido, contrasenaIntroducida)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Usuario autenticado => obtener correo y rol
                            String email = user.getEmail();
                            if (email != null) {
                                obtenerRolUsuario(email);
                            }
                        }
                    } else {
                        Snackbar.make(view, "Error al iniciar sesión: " +
                                task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void obtenerRolUsuario(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String rol = document.getString("rolUsuario");

                        if (rol != null) {
                            // Redirigir
                            redirigirPorRol(rol);

                        } else {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Rol no asignado", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Usuario no encontrado", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error al obtener el rol: " + e.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                });
    }

    private void redirigirPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "Jugador":
                intent = new Intent(this, Pantalla_InicioJugadores.class);
                break;
            case "Administrador":
                intent = new Intent(this, Pantalla_InicioAdministradores.class);
                break;
            default:
                Snackbar.make(findViewById(android.R.id.content),
                        "Rol no válido", Snackbar.LENGTH_LONG).show();
                return;
        }
        startActivity(intent);
    }
}