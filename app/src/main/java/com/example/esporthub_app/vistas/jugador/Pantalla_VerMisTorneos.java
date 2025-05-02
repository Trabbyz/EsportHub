package com.example.esporthub_app.vistas.jugador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorTorneos;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.modelos.Torneo;
import com.example.esporthub_app.vistas.torneos.Pantalla_DetallesTorneo;
import com.example.esporthub_app.vistas.torneos.Pantalla_TorneosDisponibles;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_VerMisTorneos extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout layoutSinTorneos;
    private Button btnBuscarTorneos;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<Torneo> listaTorneos;
    private AdaptadorTorneos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_mis_torneos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerMisTorneos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas
        toolbar = findViewById(R.id.toolbarMisTorneos);
        recyclerView = findViewById(R.id.recyclerViewTorneos);
        layoutSinTorneos = findViewById(R.id.layoutSinTorneos);
        btnBuscarTorneos = findViewById(R.id.btnBuscarTorneos);

        // Configuración de la barra de herramientas
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaTorneos = new ArrayList<>();
        adapter = new AdaptadorTorneos(listaTorneos, new AdaptadorTorneos.OnAbandonarTorneoListener() {
            @Override
            public void onVerDetalles(Torneo torneo) {
                Intent intent = new Intent(Pantalla_VerMisTorneos.this, Pantalla_DetallesTorneo.class);
                intent.putExtra("idTorneo", torneo.getIdTorneo());
                startActivity(intent);
            }

            @Override
            public void onAbandonar(Torneo torneo, String idDocTorneo) {
                abandonarTorneo(torneo, idDocTorneo);  // Lógica de abandonar torneo
            }
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        cargarTorneos();

        // Configuración de la acción para buscar torneos
        btnBuscarTorneos.setOnClickListener(v -> {
            startActivity(new Intent(this, Pantalla_TorneosDisponibles.class));
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarTorneos() {
        if (user == null) return;
        String uidJugador = user.getUid();

        db.collection("torneos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTorneos.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Torneo torneo = doc.toObject(Torneo.class);
                        torneo.setIdTorneo(doc.getId()); // Si necesitas luego el ID

                        boolean estaEnTorneo = false;

                        if (torneo.getParticipantes() != null) {
                            for (Equipo equipo : torneo.getParticipantes()) {
                                if (equipo.getMiembros() != null) {
                                    for (Jugador jugador : equipo.getMiembros()) {
                                        if (jugador.getUid().equals(uidJugador)) {
                                            estaEnTorneo = true;
                                            break;
                                        }
                                    }
                                }

                                if (estaEnTorneo) break;
                            }
                        }

                        if (estaEnTorneo) {
                            listaTorneos.add(torneo);
                        }
                    }

                    if (listaTorneos.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutSinTorneos.setVisibility(View.VISIBLE);
                    } else {
                        layoutSinTorneos.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al cargar torneos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
    }


    // Método para manejar el abandono de un torneo
    private void abandonarTorneo(Torneo torneo, String idDocTorneo) {
        String uidJugador = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<Equipo> participantesActuales = torneo.getParticipantes();
        if (participantesActuales == null) return;

        // Buscar el equipo del usuario
        Equipo equipoDelUsuario = null;
        for (Equipo equipo : participantesActuales) {
            if (equipo.getMiembros() != null) {
                for (Jugador jugador : equipo.getMiembros()) {
                    if (jugador.getUid().equals(uidJugador)) {
                        equipoDelUsuario = equipo;
                        break;
                    }
                }
            }
            if (equipoDelUsuario != null) break;
        }

        if (equipoDelUsuario != null) {
            participantesActuales.remove(equipoDelUsuario);

            db.collection("torneos").document(idDocTorneo)
                    .update("participantes", participantesActuales)
                    .addOnSuccessListener(aVoid -> {
                        Snackbar.make(recyclerView, "Tu equipo ha abandonado el torneo", Snackbar.LENGTH_SHORT).show();
                        cargarTorneos();  // Recargar la lista
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(recyclerView, "Error al abandonar el torneo: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
        } else {
            Snackbar.make(recyclerView, "No se encontró tu equipo en el torneo", Snackbar.LENGTH_LONG).show();
        }
    }

}
