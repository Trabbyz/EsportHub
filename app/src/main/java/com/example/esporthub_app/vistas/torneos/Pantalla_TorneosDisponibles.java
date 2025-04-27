package com.example.esporthub_app.vistas.torneos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
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
import java.util.List;

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
        db.collection("torneos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTorneos.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Torneo torneo = doc.toObject(Torneo.class);
                        torneo.setIdTorneo(doc.getId());
                        if (torneo.getParticipantes().size() < torneo.getMaxParticipantes()) {
                            listaTorneos.add(torneo);
                        }
                    }
                    adaptador.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error al cargar torneos: " + e.getMessage(),
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

                    DocumentSnapshot equipoDoc = equipoQuery.getDocuments().get(0);
                    Equipo equipo = equipoDoc.toObject(Equipo.class);

                    if (equipo == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Error al obtener tu equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

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

                                boolean yaInscrito = participantes.stream()
                                        .anyMatch(eq -> eq.getNombre().equals(equipo.getNombre())); // o comparar por ID si tenés uno

                                if (yaInscrito) {
                                    Snackbar.make(findViewById(android.R.id.content), "Tu equipo ya está inscrito en este torneo", Snackbar.LENGTH_SHORT).show();
                                    return;
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
                                Log.e("Firestore", "Error al obtener el torneo actualizado", e);
                                Snackbar.make(findViewById(android.R.id.content), "Error al obtener el torneo", Snackbar.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al buscar tu equipo", e);
                    Snackbar.make(findViewById(android.R.id.content), "Error al buscar tu equipo", Snackbar.LENGTH_SHORT).show();
                });
    }



}