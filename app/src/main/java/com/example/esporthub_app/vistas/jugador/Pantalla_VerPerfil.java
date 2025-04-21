package com.example.esporthub_app.vistas.jugador;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporthub_app.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Pantalla_VerPerfil extends AppCompatActivity {

    private Toolbar toolbarPerfil;
    private Button btnEditarPerfil, btnGuardar, btnCancelar;
    private EditText editNombre, editEmail, editRol, editRolJuego;
    private FirebaseFirestore db;
    private String usuarioID;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_detalles_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerPerfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar
        toolbarPerfil = findViewById(R.id.toolbarPerfil);
        setSupportActionBar(toolbarPerfil);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarPerfil.setNavigationOnClickListener(v -> finish());

        // EditTexts
        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        editRol = findViewById(R.id.editRol);
        editRolJuego = findViewById(R.id.editRolJuego);

        // Botones
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            Log.d("Firebase", "Correo del usuario: " + email);

            db.collection("usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            usuarioID = document.getId();

                            // Cargar los datos del perfil
                            editNombre.setText(document.getString("nombre"));
                            editEmail.setText(document.getString("email")); // Si guardas el email también
                            editRol.setText(document.getString("rolUsuario"));
                            editRolJuego.setText(document.getString("rolJuego"));
                        } else {
                            mostrarMensaje("No se encontró el perfil del usuario.");
                        }
                    })
                    .addOnFailureListener(e -> mostrarMensaje("Error al cargar el perfil: " + e.getMessage()));
        }

        // Estado inicial
        setEditable(false);
        btnGuardar.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);

        // Acción al pulsar "Editar Perfil"
        btnEditarPerfil.setOnClickListener(v -> {
            setEditable(true);
            btnEditarPerfil.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);
        });

        // Acción al pulsar "Cancelar"
        btnCancelar.setOnClickListener(v -> {
            setEditable(false);
            btnEditarPerfil.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
        });

        // Acción al pulsar "Guardar Cambios"
        btnGuardar.setOnClickListener(v -> {
            actualizarPerfil();
            setEditable(false);
            btnEditarPerfil.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
        });
    }

    private void actualizarPerfil() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevoEmail = editEmail.getText().toString().trim();
        String nuevoRol = editRol.getText().toString().trim();
        String nuevoRolJuego = editRolJuego.getText().toString().trim();

        if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty() || nuevoRol.isEmpty() || nuevoRolJuego.isEmpty()) {
            mostrarMensaje("Por favor, completa todos los campos.");
            return;
        }

        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("nombre", nuevoNombre);
        datosActualizados.put("correoElectronico", nuevoEmail);
        datosActualizados.put("rolUsuario", nuevoRol);
        datosActualizados.put("rolJuego", nuevoRolJuego);

        db.collection("usuarios").document(usuarioID)
                .update(datosActualizados)
                .addOnSuccessListener(aVoid -> {
                    mostrarMensaje("Perfil actualizado correctamente.");
                    setEditable(false);
                    btnEditarPerfil.setVisibility(View.VISIBLE);
                    btnGuardar.setVisibility(View.GONE);
                    btnCancelar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> mostrarMensaje("Error al actualizar perfil: " + e.getMessage()));
    }


    private void mostrarMensaje(String mensaje) {
        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_SHORT).show();
    }


    // Método para activar o desactivar edición
    private void setEditable(boolean editable) {
        EditText[] campos = {editNombre, editEmail, editRol, editRolJuego};
        for (EditText campo : campos) {
            campo.setFocusable(editable);
            campo.setFocusableInTouchMode(editable);
            campo.setClickable(editable);
            campo.setCursorVisible(editable);
        }
    }
}
