package com.example.esporthub_app.vistas.jugador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorEquipos;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.vistas.equipos.Pantalla_CrearEquipo;
import com.example.esporthub_app.vistas.equipos.Pantalla_EquiposDisponibles;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_VerMisEquipos extends AppCompatActivity {
    private LinearLayout layoutSinEquipos ;
    private Button btnCrearEquipo,btnBuscarEquipos;
    private RecyclerView recyclerView;
    private List<Equipo> listaEquipos;
    private List<String> listaIdsDocumentos;
    private Toolbar toolbarMisEquipos;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_mis_equipos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerMisEquipos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnBuscarEquipos = findViewById(R.id.btnBuscarEquipos);
        btnCrearEquipo= findViewById(R.id.btnCrearEquipo);
        layoutSinEquipos = findViewById(R.id.layoutSinEquipos);
        recyclerView = findViewById(R.id.recyclerViewEquipos);
        toolbarMisEquipos = findViewById(R.id.toolbarCrearEquipo);

        listaEquipos = new ArrayList<>();
        listaIdsDocumentos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbarMisEquipos);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarMisEquipos.setNavigationOnClickListener(v -> finish());

        cargarMisEquipos();
        if (listaEquipos == null || listaEquipos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutSinEquipos.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutSinEquipos.setVisibility(View.GONE);
        }


        btnCrearEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pantalla_VerMisEquipos.this, Pantalla_CrearEquipo.class);
                startActivity(intent);
            }
        });

        btnBuscarEquipos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pantalla_VerMisEquipos.this, Pantalla_EquiposDisponibles.class);
                startActivity(intent);
            }
        });

        cargarMisEquipos();

    }

    private void cargarMisEquipos() {
        String uidJugador = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("equipos")
                .get()
                .addOnSuccessListener(query -> {
                    listaEquipos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Equipo equipo = doc.toObject(Equipo.class);
                        listaIdsDocumentos.add(doc.getId());
                        for (Jugador j : equipo.getMiembros()) {
                            if (j.getUid().equals(uidJugador)) {
                                listaEquipos.add(equipo);

                                break;
                            }
                        }
                    }

                    if (listaEquipos.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutSinEquipos.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutSinEquipos.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(new AdaptadorEquipos(listaEquipos,listaIdsDocumentos));
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(recyclerView, "Error al cargar tus equipos", Snackbar.LENGTH_LONG).show();
                });
    }


}