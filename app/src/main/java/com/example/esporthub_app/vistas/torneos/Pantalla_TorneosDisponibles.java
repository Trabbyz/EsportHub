package com.example.esporthub_app.vistas.torneos;

import android.annotation.SuppressLint;
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
import com.example.esporthub_app.adaptadores.AdaptadorTorneosDisponibles;
import com.example.esporthub_app.modelos.Torneo;
import com.google.android.material.snackbar.Snackbar;
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

            }

            @Override
            public void onUnirme(Torneo torneo) {

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
                        if (torneo != null && torneo.getParticipantes().size() < torneo.getMaxParticipantes()) {
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

}