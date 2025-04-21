package com.example.esporthub_app.vistas.jugador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorJugador;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.modelos.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_VerJugadores extends AppCompatActivity {
    private RecyclerView recyclerJugadores;
    private AdaptadorJugador adaptadorJugadores;
    private FirebaseFirestore db;
    private List<Usuario> listaJugadores;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_jugadores);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerJugadores), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerJugadores = findViewById(R.id.recyclerJugadores);
        recyclerJugadores.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        listaJugadores = new ArrayList<>();

        toolbar = findViewById(R.id.toolbarVerJugadores);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        adaptadorJugadores = new AdaptadorJugador(listaJugadores, new AdaptadorJugador.AccionJugadorListener() {
            @Override
            public void verPerfil(Usuario jugador) {
                // Aquí puedes abrir otra actividad con los datos
                Intent intent = new Intent(Pantalla_VerJugadores.this, Pantalla_PerfilJugador.class);
                intent.putExtra("email", jugador.getEmail()); // Asegúrate de que tiene ID
                startActivity(intent);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void eliminarJugador(Usuario jugador) {
                eliminarJugadorDeFirestore(jugador);
            }

        });
        recyclerJugadores.setAdapter(adaptadorJugadores);

        cargarJugadores();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarJugadores() {
        db.collection("usuarios")
                .whereEqualTo("rolUsuario", "Jugador")  // Filtrar solo los jugadores
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        listaJugadores.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String nombre = document.getString("nombre");
                            String email = document.getString("email");

                            // Crear un objeto Usuario para cada jugador
                            Usuario jugador = new Usuario(nombre, email);
                            listaJugadores.add(jugador);
                        }
                        adaptadorJugadores.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Pantalla_VerJugadores.this, "No se encontraron jugadores", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Pantalla_VerJugadores.this, "Error al cargar los jugadores", Toast.LENGTH_SHORT).show();
                });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void eliminarJugadorDeFirestore(Usuario jugador) {
        // Primero, buscar al jugador en la colección "usuarios" usando su correo
        db.collection("usuarios")
                .whereEqualTo("email", jugador.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Eliminar al jugador de la colección "usuarios"
                            String usuarioId = document.getId(); // ID del documento en la colección "usuarios"
                            db.collection("usuarios").document(usuarioId).delete()
                                    .addOnSuccessListener(unused -> {
                                        // Ahora, eliminar al jugador de la colección "jugadores"
                                        eliminarJugadorDeColeccionJugadores(usuarioId);
                                        adaptadorJugadores.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Pantalla_VerJugadores.this, "Error al eliminar el jugador de usuarios", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(Pantalla_VerJugadores.this, "No se encontró el jugador en usuarios", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Pantalla_VerJugadores.this, "Error al buscar el jugador en usuarios", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para eliminar al jugador de la colección "jugadores"
    @SuppressLint("NotifyDataSetChanged")
    private void eliminarJugadorDeColeccionJugadores(String usuarioId) {
        db.collection("jugadores")
                .whereEqualTo("idUsuario", usuarioId) // Buscar en "jugadores" usando el idUsuario (que es el uid del usuario)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Eliminar el jugador de la colección "jugadores"
                            db.collection("jugadores").document(document.getId()).delete()
                                    .addOnSuccessListener(unused -> {
                                        Jugador jugador = document.toObject(Jugador.class);
                                        Toast.makeText(Pantalla_VerJugadores.this, "Jugador eliminado de jugadores", Toast.LENGTH_SHORT).show();
                                        listaJugadores.remove(jugador); // Eliminar de la lista local
                                        adaptadorJugadores.notifyDataSetChanged(); // Notificar al adaptador para actualizar la UI
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Pantalla_VerJugadores.this, "Error al eliminar el jugador de jugadores", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(Pantalla_VerJugadores.this, "No se encontró el jugador en la colección jugadores", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Pantalla_VerJugadores.this, "Error al buscar el jugador en jugadores", Toast.LENGTH_SHORT).show();
                });
    }

}