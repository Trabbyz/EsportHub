package com.example.esporthub_app.vistas.jugador;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import java.util.List;
import java.util.Map;

public class Pantalla_VerPerfil extends AppCompatActivity {

    private Toolbar toolbarPerfil;
    private Button btnEditarPerfil, btnGuardar, btnCancelar;
    private EditText editNombre, editEmail, editRol;
    private Spinner spinnerRolJuego;
    private FirebaseFirestore db;
    private String usuarioID;
    private FirebaseUser user;
    private ArrayAdapter<String> adapterRoles;

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

        // Inicializar vistas
        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        editRol = findViewById(R.id.editRol);
        spinnerRolJuego = findViewById(R.id.spinnerRolJuego);

        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Configurar Spinner de Roles
        adapterRoles = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Top", "Jungla", "Mid", "Adc", "Support"});
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRolJuego.setAdapter(adapterRoles);

        // Cargar datos de usuario
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

                            // Cargar datos en los campos
                            editNombre.setText(document.getString("nombre"));
                            editEmail.setText(document.getString("email"));
                            editRol.setText(document.getString("rolUsuario"));
                            String rolJuego = document.getString("rolJuego");

                            if (rolJuego != null) {
                                int position = adapterRoles.getPosition(rolJuego);
                                if (position >= 0) {
                                    spinnerRolJuego.setSelection(position);
                                }
                            }
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

        // Botón "Editar Perfil"
        btnEditarPerfil.setOnClickListener(v -> {
            setEditable(true);
            btnEditarPerfil.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);
        });

        // Botón "Cancelar"
        btnCancelar.setOnClickListener(v -> {
            setEditable(false);
            btnEditarPerfil.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
        });

        // Botón "Guardar Cambios"
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
        String nuevoRol = editRol.getText().toString().trim(); // no editable
        String nuevoRolJuego = spinnerRolJuego.getSelectedItem().toString().trim();

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
                    actualizarEnColeccionJugadores(nuevoNombre, nuevoEmail, nuevoRolJuego);
                    actualizarEnEquipo(nuevoNombre, nuevoEmail, nuevoRolJuego);
                })
                .addOnFailureListener(e -> mostrarMensaje("Error al actualizar perfil: " + e.getMessage()));
    }

    private void actualizarEnColeccionJugadores(String nombre, String email, String rolJuego) {
        db.collection("jugadores")
                .whereEqualTo("idUsuario", usuarioID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().update("nombre", nombre,
                                "email", email,
                                "rolJuego", rolJuego);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error actualizando en jugadores: " + e.getMessage()));
    }

    private void actualizarEnEquipo(String nombre, String email, String rolJuego) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Firebase", "Usuario no autenticado");
            return;
        }

        String currentUID = currentUser.getUid();

        db.collection("equipos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot equipo : querySnapshot.getDocuments()) {
                        List<Map<String, Object>> miembrosData = (List<Map<String, Object>>) equipo.get("miembros");

                        if (miembrosData != null) {
                            boolean actualizado = false;

                            for (Map<String, Object> jugador : miembrosData) {
                                if (jugador.containsKey("uid") &&
                                        currentUID.equals(jugador.get("uid"))) {
                                    jugador.put("nombre", nombre);
                                    jugador.put("rolJuego", rolJuego);
                                    actualizado = true;
                                    break; // solo uno debe coincidir
                                }
                            }

                            if (actualizado) {
                                equipo.getReference()
                                        .update("miembros", miembrosData)
                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Jugador actualizado en el equipo"))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Error al actualizar miembro: " + e.getMessage()));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error obteniendo equipos: " + e.getMessage()));
    }




    private void mostrarMensaje(String mensaje) {
        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_SHORT).show();
    }

    // Activar/desactivar edición
    private void setEditable(boolean editable) {
        editNombre.setFocusable(editable);
        editNombre.setFocusableInTouchMode(editable);
        editNombre.setClickable(editable);
        editNombre.setCursorVisible(editable);

        editEmail.setFocusable(editable);
        editEmail.setFocusableInTouchMode(editable);
        editEmail.setClickable(editable);
        editEmail.setCursorVisible(editable);

        // El rolUsuario NO se puede editar nunca
        editRol.setFocusable(false);
        editRol.setFocusableInTouchMode(false);
        editRol.setClickable(false);

        // El spinner de rolJuego sólo se activa/desactiva
        spinnerRolJuego.setEnabled(editable);
    }
}

