package com.example.esporthub_app.vistas.equipos;

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
import com.example.esporthub_app.adaptadores.AdaptadorEquiposDisponibles;
import com.example.esporthub_app.modelos.Equipo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        toolbar = findViewById(R.id.toolbarEquiposDisponibles);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerEquiposDisponibles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaEquipos = new ArrayList<>();
        adapter = new AdaptadorEquiposDisponibles(listaEquipos, this, new AdaptadorEquiposDisponibles.OnEquipoClickListener() {
            @Override
            public void onVerDetalles(Equipo equipo) {

            }

            @Override
            public void onUnirme(Equipo equipo) {

            }
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarEquipos();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarEquipos() {
        db.collection("equipos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaEquipos.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Equipo equipo = doc.toObject(Equipo.class);

                        if (equipo != null && equipo.getMiembros() != null && equipo.getMiembros().size() < equipo.getMaxJugadores()) {
                            listaEquipos.add(equipo);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error al cargar equipos: " + e.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                });
    }

}