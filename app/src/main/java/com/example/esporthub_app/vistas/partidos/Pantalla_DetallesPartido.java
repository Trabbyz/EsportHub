package com.example.esporthub_app.vistas.partidos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import com.example.esporthub_app.adaptadores.AdaptadorDetallesPartido;
import com.example.esporthub_app.modelos.Partido;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
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

        // Configurar el toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetallePartido);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener datos pasados por Intent
        Intent intent = getIntent();
        nombreEquipo = intent.getStringExtra("nombreEquipo");

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPartidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorDetallesPartido(partidosList, this::onVerPartido);
        recyclerView.setAdapter(adaptador);

        // Realizar la consulta a Firestore para obtener los partidos del equipo
        obtenerPartidosPorEquipo(nombreEquipo);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void obtenerPartidosPorEquipo(String nombreEquipo) {
        Log.d("Firestore", "Consultando partidos donde el equipo está como equipo1ID: " + nombreEquipo);

        // Consultar los partidos donde el equipo está como equipo1ID
        db.collection("partidos")
                .whereEqualTo("equipo1ID", nombreEquipo)
                .get()
                .addOnSuccessListener(querySnapshot1 -> {
                    if (!querySnapshot1.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot1) {
                            String equipoA = documentSnapshot.getString("equipo1ID");
                            String equipoB = documentSnapshot.getString("equipo2ID");
                            String fecha = documentSnapshot.getString("fecha");
                            String resultado = documentSnapshot.getString("resultado");
                            String link = documentSnapshot.getString("urlPartido");

                            Partido partido = new Partido(equipoA, equipoB, fecha, resultado, link);
                            partidosList.add(partido);
                        }
                        Log.d("Firestore", "Partidos encontrados como equipo1ID.");
                    } else {
                        Log.d("Firestore", "No se encontraron partidos para este equipo en equipo1ID.");
                    }

                    // Consulta los partidos donde el equipo está como equipo2ID
                    Log.d("Firestore", "Consultando partidos donde el equipo está como equipo2ID: " + nombreEquipo);
                    db.collection("partidos")
                            .whereEqualTo("equipo2ID", nombreEquipo)
                            .get()
                            .addOnSuccessListener(querySnapshot2 -> {
                                if (!querySnapshot2.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot : querySnapshot2) {
                                        String equipoA = documentSnapshot.getString("equipo1ID");
                                        String equipoB = documentSnapshot.getString("equipo2ID");
                                        String fecha = documentSnapshot.getString("fecha");
                                        String resultado = documentSnapshot.getString("resultado");
                                        String link = documentSnapshot.getString("urlPartido");

                                        Partido partido = new Partido(equipoA, equipoB, fecha, resultado, link);
                                        partidosList.add(partido);
                                    }
                                    Log.d("Firestore", "Partidos encontrados como equipo2ID.");
                                } else {
                                    Log.d("Firestore", "No se encontraron partidos para este equipo en equipo2ID.");
                                }

                                // Si se encontraron partidos, actualizar el RecyclerView
                                if (!partidosList.isEmpty()) {
                                    adaptador.notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
                                } else {
                                    Snackbar.make(findViewById(android.R.id.content),
                                                    "No se encontraron partidos para este equipo", Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error al obtener partidos como equipo2ID: " + e.getMessage());
                                Snackbar.make(findViewById(android.R.id.content),
                                                "Error al obtener los partidos como equipo2ID: " + e.getMessage(), Snackbar.LENGTH_SHORT)
                                        .show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener partidos como equipo1ID: " + e.getMessage());
                    Snackbar.make(findViewById(android.R.id.content),
                                    "Error al obtener los partidos como equipo1ID: " + e.getMessage(), Snackbar.LENGTH_SHORT)
                            .show();
                });
    }




    // Método que será llamado cuando se haga clic en el botón "Ver Partido" en el adaptador
    private void onVerPartido(Partido partido) {
        String url = partido.getUrlPartido();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No hay link disponible para este partido", Toast.LENGTH_SHORT).show();
        }
    }
}


