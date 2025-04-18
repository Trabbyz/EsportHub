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
import com.example.esporthub_app.modelos.Torneo;
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


        toolbar = findViewById(R.id.toolbarMisTorneos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewTorneos);
        layoutSinTorneos = findViewById(R.id.layoutSinTorneos);
        btnBuscarTorneos = findViewById(R.id.btnBuscarTorneos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaTorneos = new ArrayList<>();
        adapter = new AdaptadorTorneos(listaTorneos); // Asegúrate de tener este adapter
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        cargarTorneos();

        btnBuscarTorneos.setOnClickListener(v -> {
            // Abre la actividad de buscar torneos
            startActivity(new Intent(this, Pantalla_TorneosDisponibles.class));
        });
}

    @SuppressLint("NotifyDataSetChanged")
    private void cargarTorneos() {
        if (user == null) return;

        db.collection("torneos")
                .whereEqualTo("creadorID", user.getUid()) // o campo que relacione al usuario
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTorneos.clear();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Torneo torneo = doc.toObject(Torneo.class);
                            listaTorneos.add(torneo);
                        }

                        layoutSinTorneos.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        layoutSinTorneos.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Error al cargar torneos: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
    }
}