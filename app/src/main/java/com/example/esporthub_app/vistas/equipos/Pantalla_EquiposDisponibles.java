package com.example.esporthub_app.vistas.equipos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.esporthub_app.adaptadores.AdaptadorEquiposDisponibles;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_EquiposDisponibles extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Equipo> listaEquipos;
    private AdaptadorEquiposDisponibles adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_equipos_disponibles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaEquiposDisponibles), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup de Toolbar
        toolbar = findViewById(R.id.toolbarEquiposDisponibles);
        recyclerView = findViewById(R.id.recyclerEquiposDisponibles);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicialización de Firestore y lista de equipos
        db = FirebaseFirestore.getInstance();
        listaEquipos = new ArrayList<>();

        // Inicialización del adaptador
        adapter = new AdaptadorEquiposDisponibles(
                listaEquipos,
                new ArrayList<>(), // Lista de IDs de documentos (aún vacía)
                this,
                new AdaptadorEquiposDisponibles.OnEquipoClickListener() {
                    @Override
                    public void onVerDetalles(Equipo equipo) {
                        // Acción para ver detalles del equipo
                        Intent intent = new Intent(Pantalla_EquiposDisponibles.this, Pantalla_VerDetallesEquipo.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onUnirme(Equipo equipo, String idDocEquipo) {
                        // Acción para unirse al equipo
                        unirmeAlEquipo(equipo, idDocEquipo);
                    }
                });

        // Asignar el adaptador al RecyclerView
        recyclerView.setAdapter(adapter);

        // Cargar los equipos de Firestore
        cargarEquipos();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarEquipos() {
        String uidJugadorActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Buscar el jugador actual por su UID
        db.collection("jugadores").whereEqualTo("uid", uidJugadorActual).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot jugadorDoc = querySnapshot.getDocuments().get(0);
                        Jugador jugador = jugadorDoc.toObject(Jugador.class);

                        if (jugador == null) {
                            Snackbar.make(findViewById(android.R.id.content), "No se encontraron datos del jugador", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        // Obtener equipos desde Firestore
                        db.collection("equipos").get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    listaEquipos.clear();
                                    List<String> listaIdsDocumentos = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                        Equipo equipo = doc.toObject(Equipo.class);
                                        String idDoc = doc.getId();

                                        boolean equipoDisponible = equipo.getMiembros() != null &&
                                                equipo.getMiembros().size() < equipo.getMaxJugadores();

                                        if (equipoDisponible) {
                                            listaEquipos.add(equipo);
                                            listaIdsDocumentos.add(idDoc);
                                        }
                                    }

                                    // Crear y asignar adaptador
                                    adapter = new AdaptadorEquiposDisponibles(listaEquipos, listaIdsDocumentos, this, new AdaptadorEquiposDisponibles.OnEquipoClickListener() {
                                        @Override
                                        public void onVerDetalles(Equipo equipo) {
                                            int position = listaEquipos.indexOf(equipo);
                                            String idDocEquipo = listaIdsDocumentos.get(position);
                                            Intent intent = new Intent(Pantalla_EquiposDisponibles.this, Pantalla_VerDetallesEquipo.class);
                                            intent.putExtra("idEquipo", idDocEquipo);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onUnirme(Equipo equipo, String idDocEquipo) {
                                            unirmeAlEquipo(equipo, idDocEquipo);
                                        }
                                    });

                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Snackbar.make(findViewById(android.R.id.content), "Error al cargar equipos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                });

                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "No se encontró tu jugador", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al obtener datos del jugador", Snackbar.LENGTH_SHORT).show();
                });
    }




    private void unirmeAlEquipo(Equipo equipo, String idDocEquipo) {
        String uidJugadorActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Refrescamos el equipo desde Firestore
        db.collection("equipos").document(idDocEquipo).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Equipo equipoActualizado = documentSnapshot.toObject(Equipo.class);

                    if (equipoActualizado == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Error al obtener datos del equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (equipoActualizado.getIdMiembros().contains(uidJugadorActual)) {
                        Snackbar.make(findViewById(android.R.id.content), "Ya perteneces a este equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (equipoActualizado.getIdMiembros().size() >= equipoActualizado.getMaxJugadores()) {
                        Snackbar.make(findViewById(android.R.id.content), "Este equipo ya está lleno", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Buscar jugador por UID
                    db.collection("jugadores").whereEqualTo("uid", uidJugadorActual).get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot jugadorDoc = querySnapshot.getDocuments().get(0);
                                    Jugador jugador = jugadorDoc.toObject(Jugador.class);

                                    // Actualizar datos en Firestore
                                    equipoActualizado.getIdMiembros().add(uidJugadorActual);
                                    equipoActualizado.getMiembros().add(jugador);

                                    db.collection("equipos").document(idDocEquipo)
                                            .update("idMiembros", equipoActualizado.getIdMiembros(),
                                                    "miembros", equipoActualizado.getMiembros())
                                            .addOnSuccessListener(aVoid -> {
                                                db.collection("jugadores").document(jugadorDoc.getId())
                                                        .update("equipoActual", equipoActualizado.getNombre())
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            Snackbar.make(findViewById(android.R.id.content), "Te has unido al equipo", Snackbar.LENGTH_SHORT).show();
                                                            cargarEquipos(); // Recargar lista para reflejar cambios
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("Firestore", "Error al actualizar equipoActual del jugador", e);
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firestore", "Error al unirse al equipo", e);
                                                Snackbar.make(findViewById(android.R.id.content), "Error al unirse al equipo", Snackbar.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.e("Firestore", "No se encontró ningún jugador con ese UID");
                                    Snackbar.make(findViewById(android.R.id.content), "No se encontró tu jugador", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error al buscar el jugador", e);
                                Snackbar.make(findViewById(android.R.id.content), "Error al obtener datos del jugador", Snackbar.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener datos del equipo actualizado", e);
                    Snackbar.make(findViewById(android.R.id.content), "Error al obtener el equipo", Snackbar.LENGTH_SHORT).show();
                });
    }

}
