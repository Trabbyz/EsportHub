package com.example.esporthub_app.vistas.partidos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.example.esporthub_app.adaptadores.AdaptadorDetallesPartido;
import com.example.esporthub_app.modelos.Partido;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_DetallesPartido extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdaptadorDetallesPartido adaptador;
    private List<Partido> partidosList = new ArrayList<>();
    private FirebaseFirestore db;
    private String nombreEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_detalles_partido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaDetallesPartido), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarDetallePartido);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        nombreEquipo = getIntent().getStringExtra("nombreEquipo");

        recyclerView = findViewById(R.id.recyclerViewPartidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorDetallesPartido(partidosList, this::onVerPartido);
        recyclerView.setAdapter(adaptador);

        obtenerPartidosPorEquipo(nombreEquipo);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void obtenerPartidosPorEquipo(String nombreEquipo) {
        Log.d("Firestore", "Consultando partidos donde el equipo está como equipo1ID: " + nombreEquipo);

        db.collection("partidos")
                .whereEqualTo("equipo1ID", nombreEquipo)
                .get()
                .addOnSuccessListener(querySnapshot1 -> {
                    if (!querySnapshot1.isEmpty()) {
                        for (DocumentSnapshot doc : querySnapshot1) {
                            String equipoA = doc.getString("equipo1ID");
                            String equipoB = doc.getString("equipo2ID");
                            String fecha    = doc.getString("fecha");
                            String resultado= doc.getString("resultado");
                            String link     = doc.getString("urlPartido");

                            partidosList.add(new Partido(equipoA, equipoB, fecha, resultado, link));
                        }
                        Log.d("Firestore", "Partidos encontrados como equipo1ID.");
                    } else {
                        Log.d("Firestore", "No se encontraron partidos para este equipo en equipo1ID.");
                    }

                    Log.d("Firestore", "Consultando partidos donde el equipo está como equipo2ID: " + nombreEquipo);
                    db.collection("partidos")
                            .whereEqualTo("equipo2ID", nombreEquipo)
                            .get()
                            .addOnSuccessListener(querySnapshot2 -> {
                                if (!querySnapshot2.isEmpty()) {
                                    for (DocumentSnapshot doc : querySnapshot2) {
                                        String equipoA = doc.getString("equipo1ID");
                                        String equipoB = doc.getString("equipo2ID");
                                        String fecha    = doc.getString("fecha");
                                        String resultado= doc.getString("resultado");
                                        String link     = doc.getString("urlPartido");

                                        partidosList.add(new Partido(equipoA, equipoB, fecha, resultado, link));
                                    }
                                    Log.d("Firestore", "Partidos encontrados como equipo2ID.");
                                } else {
                                    Log.d("Firestore", "No se encontraron partidos para este equipo en equipo2ID.");
                                }

                                if (!partidosList.isEmpty()) {
                                    adaptador.notifyDataSetChanged();
                                } else {
                                    Snackbar.make(findViewById(android.R.id.content),
                                                    "No se encontraron partidos para este equipo",
                                                    Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error al obtener partidos como equipo2ID: " + e.getMessage());
                                Snackbar.make(findViewById(android.R.id.content),
                                                "Error al obtener los partidos como equipo2ID: " + e.getMessage(),
                                                Snackbar.LENGTH_SHORT)
                                        .show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener partidos como equipo1ID: " + e.getMessage());
                    Snackbar.make(findViewById(android.R.id.content),
                                    "Error al obtener los partidos como equipo1ID: " + e.getMessage(),
                                    Snackbar.LENGTH_SHORT)
                            .show();
                });
    }

    private void onVerPartido(Partido partido) {
        String url = partido.getUrlPartido();
        if (url != null && !url.isEmpty()) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                            "No hay link disponible para este partido",
                            Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}