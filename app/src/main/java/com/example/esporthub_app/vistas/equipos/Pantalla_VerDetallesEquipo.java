package com.example.esporthub_app.vistas.equipos;

import android.os.Bundle;
import android.widget.ImageButton;
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
import com.example.esporthub_app.adaptadores.AdaptadorMiembroEquipo;
import com.example.esporthub_app.modelos.Jugador;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pantalla_VerDetallesEquipo extends AppCompatActivity {
    private TextView tvNombreEquipo, tvDescripcionEquipo;
    private RecyclerView recyclerMiembros;
    private FirebaseFirestore db;
    private String idEquipo;
    private Toolbar toolbarVerDetalles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_detalles_equipo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerDetallesEquipo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvNombreEquipo = findViewById(R.id.textViewNombreEquipo);
        tvDescripcionEquipo = findViewById(R.id.textViewDescripcion);
        toolbarVerDetalles = findViewById(R.id.toolbarDetallesEquipo);
        recyclerMiembros = findViewById(R.id.recyclerViewMiembros);
        setSupportActionBar(toolbarVerDetalles);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarVerDetalles.setNavigationOnClickListener(v -> finish());

        idEquipo = getIntent().getStringExtra("idEquipo");

        // Iniciar Firestore
        db = FirebaseFirestore.getInstance();

        ImageButton starButton = findViewById(R.id.starFavorite);
        final boolean[] esFavorito = {false};

        starButton.setOnClickListener(v -> {
            esFavorito[0] = !esFavorito[0];
            if (esFavorito[0]) {
                starButton.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                starButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
        });

        cargarDatosEquipo();

    }

    private void cargarDatosEquipo() {
        db.collection("equipos").document(idEquipo).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String descripcion = documentSnapshot.getString("descripcion");

                        tvNombreEquipo.setText(nombre != null ? nombre : "Sin nombre");
                        tvDescripcionEquipo.setText(descripcion != null ? descripcion : "Sin descripción");

                        // Obtener la lista de miembros de tipo HashMap y convertirla en una lista de Jugador
                        List<HashMap<String, Object>> miembrosData = (List<HashMap<String, Object>>) documentSnapshot.get("miembros");
                        List<Jugador> listaMiembros = new ArrayList<>();

                        if (miembrosData != null) {
                            for (HashMap<String, Object> miembroData : miembrosData) {
                                // Aquí debes crear un Jugador a partir del HashMap
                                String nombreMiembro = (String) miembroData.get("nombre");
                                String rolMiembro = (String) miembroData.get("rolJuego");

                                Jugador jugador = new Jugador(nombreMiembro, rolMiembro);
                                listaMiembros.add(jugador);
                            }
                            // Cargar los miembros en el RecyclerView

                            recyclerMiembros.setLayoutManager(new LinearLayoutManager(this));
                            recyclerMiembros.setAdapter(new AdaptadorMiembroEquipo(listaMiembros));
                        }

                    } else {
                        Snackbar.make(findViewById(R.id.pantallaVerDetallesEquipo),
                                "El equipo no existe",
                                Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(R.id.pantallaVerDetallesEquipo),
                            "Error al obtener datos: " + e.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                });
    }





}