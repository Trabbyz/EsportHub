package com.example.esporthub_app.vistas.torneos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorTorneosDisponibles;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Torneo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pantalla_TorneosDisponibles extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdaptadorTorneosDisponibles adaptador;
    private List<Torneo> listaTorneos = new ArrayList<>();
    private FirebaseFirestore db;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_torneos_disponibles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaTorneosDisponibles), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbarTorneosDisponibles);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerTorneosDisponibles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        adaptador = new AdaptadorTorneosDisponibles(listaTorneos, this, new AdaptadorTorneosDisponibles.OnTorneoClickListener() {
            @Override
            public void onVerDetalles(Torneo torneo) {
                Intent intent = new Intent(Pantalla_TorneosDisponibles.this, Pantalla_DetallesTorneo.class);
                intent.putExtra("idTorneo", torneo.getIdTorneo());
                startActivity(intent);
            }

            @Override
            public void onUnirme(Torneo torneo) {
                unirmeAlTorneo(torneo);
            }
        });

        recyclerView.setAdapter(adaptador);

        cargarTorneosDisponibles();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void cargarTorneosDisponibles() {
        String uidJugadorActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("equipos").whereArrayContains("idMiembros", uidJugadorActual).get()
                .addOnSuccessListener(equipoQuery -> {
                    if (equipoQuery.isEmpty()) {
                        Snackbar.make(findViewById(android.R.id.content), "No estás en ningún equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot equipoDoc = equipoQuery.getDocuments().get(0);
                    Equipo equipoActual = equipoDoc.toObject(Equipo.class);

                    if (equipoActual == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Error al obtener tu equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    String nombreEquipoActual = equipoActual.getNombre(); // O el id si prefieres usar id

                    // Ahora cargamos los torneos
                    db.collection("torneos")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                listaTorneos.clear();
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    Torneo torneo = doc.toObject(Torneo.class);
                                    torneo.setIdTorneo(doc.getId());

                                    // Filtro: el torneo debe tener hueco
                                    if (torneo.getParticipantes().size() < torneo.getMaxParticipantes()) {

                                        // Filtro: el equipo no debe estar inscrito
                                        boolean equipoYaInscrito = torneo.getParticipantes().stream()
                                                .anyMatch(eq -> eq.getNombre().equals(nombreEquipoActual)); // Comparar por nombre (o id)

                                        if (!equipoYaInscrito) {
                                            listaTorneos.add(torneo);
                                        }
                                    }
                                }
                                adaptador.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(findViewById(android.R.id.content),
                                        "Error al cargar torneos: " + e.getMessage(),
                                        Snackbar.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error al buscar tu equipo: " + e.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                });
    }


    private void unirmeAlTorneo(Torneo torneo) {
        String uidJugadorActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("equipos").whereArrayContains("idMiembros", uidJugadorActual).get()
                .addOnSuccessListener(equipoQuery -> {
                    if (equipoQuery.isEmpty()) {
                        Snackbar.make(findViewById(android.R.id.content), "No estás en ningún equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    List<Equipo> equiposJugador = new ArrayList<>();
                    for (DocumentSnapshot doc : equipoQuery.getDocuments()) {
                        Equipo equipo = doc.toObject(Equipo.class);
                        if (equipo != null) {
                            equiposJugador.add(equipo);
                        }
                    }

                    if (equiposJugador.size() == 1) {
                        // Solo tiene un equipo → continuar
                        intentarUnirseAlTorneo(torneo, equiposJugador.get(0), uidJugadorActual);
                    } else {
                        // Tiene varios equipos → mostrar diálogo para elegir
                        String[] nombresEquipos = new String[equiposJugador.size()];
                        for (int i = 0; i < equiposJugador.size(); i++) {
                            nombresEquipos[i] = equiposJugador.get(i).getNombre();
                        }

                        new AlertDialog.Builder(this)
                                .setTitle("Selecciona un equipo")
                                .setItems(nombresEquipos, (dialog, which) -> {
                                    Equipo equipoSeleccionado = equiposJugador.get(which);
                                    intentarUnirseAlTorneo(torneo, equipoSeleccionado, uidJugadorActual);
                                })
                                .setCancelable(true)
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al buscar tus equipos", e);
                    Snackbar.make(findViewById(android.R.id.content), "Error al buscar tus equipos", Snackbar.LENGTH_SHORT).show();
                });
    }


    private void intentarUnirseAlTorneo(Torneo torneo, Equipo equipo, String uidJugadorActual) {
        if (equipo.getIdMiembros().size() != 5) {
            Snackbar.make(findViewById(android.R.id.content), "Tu equipo debe tener exactamente 5 jugadores", Snackbar.LENGTH_SHORT).show();
            return;
        }

        db.collection("torneos").document(torneo.getIdTorneo()).get()
                .addOnSuccessListener(torneoSnapshot -> {
                    Torneo torneoActualizado = torneoSnapshot.toObject(Torneo.class);

                    if (torneoActualizado == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Error al obtener el torneo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    List<Equipo> participantes = torneoActualizado.getParticipantes() != null ?
                            torneoActualizado.getParticipantes() : new ArrayList<>();

                    // Verificar si el equipo ya está inscrito
                    boolean equipoYaInscrito = participantes.stream()
                            .anyMatch(eq -> eq.getNombre().equals(equipo.getNombre()));

                    if (equipoYaInscrito) {
                        Snackbar.make(findViewById(android.R.id.content), "Ese equipo ya está inscrito en el torneo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Verificar si algún jugador del equipo ya está participando con otro equipo
                    Set<String> idsJugadoresEquipoSeleccionado = new HashSet<>(equipo.getIdMiembros());

                    for (Equipo equipoInscrito : participantes) {
                        List<String> miembros = equipoInscrito.getIdMiembros();
                        if (miembros != null) {
                            for (String idMiembro : miembros) {
                                if (idsJugadoresEquipoSeleccionado.contains(idMiembro)) {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Uno o más jugadores ya están participando en el torneo con otro equipo", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    }

                    if (participantes.size() >= torneoActualizado.getMaxParticipantes()) {
                        Snackbar.make(findViewById(android.R.id.content), "El torneo ya está lleno", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    participantes.add(equipo);

                    db.collection("torneos").document(torneo.getIdTorneo())
                            .update("participantes", participantes)
                            .addOnSuccessListener(aVoid -> {
                                Snackbar.make(findViewById(android.R.id.content), "Tu equipo se ha inscrito correctamente", Snackbar.LENGTH_SHORT).show();
                                cargarTorneosDisponibles();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error al inscribirse al torneo", e);
                                Snackbar.make(findViewById(android.R.id.content), "Error al inscribirse al torneo", Snackbar.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener el torneo", e);
                    Snackbar.make(findViewById(android.R.id.content), "Error al obtener el torneo", Snackbar.LENGTH_SHORT).show();
                });
    }







}