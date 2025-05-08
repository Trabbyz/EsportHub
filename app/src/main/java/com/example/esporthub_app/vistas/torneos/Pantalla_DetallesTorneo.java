package com.example.esporthub_app.vistas.torneos;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorParticipantesTorneo;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Torneo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_DetallesTorneo extends AppCompatActivity {
    private TextView txtNombreTorneo, txtDescripcion, txtFechaInicio, txtFechaFin;
    private TextView txtEstado, txtMaxParticipantes, txtParticipantes;
    private RecyclerView recyclerParticipantes;
    private Button btnInscribirse;
    private AdaptadorParticipantesTorneo adaptadorParticipantes;
    private FirebaseFirestore db;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_detalles_torneo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaDetallesTorneo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombreTorneo = findViewById(R.id.txtNombreTorneo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtFechaInicio = findViewById(R.id.txtFechaInicio);
        txtFechaFin = findViewById(R.id.txtFechaFin);
        txtEstado = findViewById(R.id.txtEstado);
        txtMaxParticipantes = findViewById(R.id.txtMaxParticipantes);
        recyclerParticipantes = findViewById(R.id.recyclerParticipantes);
        btnInscribirse = findViewById(R.id.btnInscribirse);
        toolbar = findViewById(R.id.toolbarTorneo);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        String torneoId = getIntent().getStringExtra("idTorneo");
        if (torneoId != null) {
            cargarDatosTorneo(torneoId);
        }
    }

    private void cargarDatosTorneo(String idTorneo) {
        db.collection("torneos").document(idTorneo).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        mostrarDatos(documentSnapshot);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Torneo no encontrado", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al cargar el torneo", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void mostrarDatos(DocumentSnapshot doc) {
        Torneo torneo = doc.toObject(Torneo.class);
        txtNombreTorneo.setText(doc.getString("nombre"));
        txtDescripcion.setText(doc.getString("descripcion"));
        txtFechaInicio.setText(doc.getString("fechaInicio"));
        txtFechaFin.setText(doc.getString("fechaFin"));
        txtEstado.setText(doc.getString("estado"));
        txtMaxParticipantes.setText(String.valueOf(doc.getLong("maxParticipantes")));

        List<String> participantes = (List<String>) doc.get("participantes");
        if (participantes != null) {
            recyclerParticipantes.setLayoutManager(new LinearLayoutManager(this));
            adaptadorParticipantes = new AdaptadorParticipantesTorneo(torneo.getParticipantes(), this);
            recyclerParticipantes.setAdapter(adaptadorParticipantes);
        }

        btnInscribirse.setOnClickListener(v -> {
            unirmeAlTorneo(torneo);
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
                                        .anyMatch(eq -> eq.getNombre().equals(equipo.getNombre()));

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
