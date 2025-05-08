package com.example.esporthub_app.vistas.jugador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;


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
import com.google.android.material.snackbar.Snackbar;
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
                Intent intent = new Intent(Pantalla_VerJugadores.this, Pantalla_PerfilJugador.class);
                intent.putExtra("email", jugador.getEmail());
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
                .whereEqualTo("rolUsuario", "Jugador")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        listaJugadores.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String nombre = document.getString("nombre");
                            String email = document.getString("email");
                            Usuario jugador = new Usuario(nombre, email);
                            listaJugadores.add(jugador);
                        }
                        adaptadorJugadores.notifyDataSetChanged();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "No se encontraron jugadores", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al cargar los jugadores", Snackbar.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void eliminarJugadorDeFirestore(Usuario jugador) {
        db.collection("usuarios")
                .whereEqualTo("email", jugador.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String usuarioId = document.getId();
                            db.collection("usuarios").document(usuarioId).delete()
                                    .addOnSuccessListener(unused -> {
                                        eliminarJugadorDeColeccionJugadores(usuarioId);
                                        adaptadorJugadores.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Snackbar.make(findViewById(android.R.id.content), "Error al eliminar el jugador de usuarios", Snackbar.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "No se encontró el jugador en usuarios", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al buscar el jugador en usuarios", Snackbar.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void eliminarJugadorDeColeccionJugadores(String usuarioId) {
        db.collection("jugadores")
                .whereEqualTo("idUsuario", usuarioId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            db.collection("jugadores").document(document.getId()).delete()
                                    .addOnSuccessListener(unused -> {
                                        Jugador jugador = document.toObject(Jugador.class);
                                        Snackbar.make(findViewById(android.R.id.content), "Jugador eliminado de jugadores", Snackbar.LENGTH_SHORT).show();
                                        listaJugadores.remove(jugador);
                                        adaptadorJugadores.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Snackbar.make(findViewById(android.R.id.content), "Error al eliminar el jugador de jugadores", Snackbar.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "No se encontró el jugador en la colección jugadores", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al buscar el jugador en jugadores", Snackbar.LENGTH_SHORT).show();
                });
    }
}
