package com.example.esporthub_app.vistas.partidos;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import com.example.esporthub_app.adaptadores.AdaptadorPartidos;
import com.example.esporthub_app.modelos.Partido;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pantalla_EliminarPartido extends AppCompatActivity {
    private AutoCompleteTextView inputTorneo;
    private RecyclerView recyclerPartidos;
    private MaterialButton btnEliminarPartido;
    private TextView txtConfirmacion;
    private FirebaseFirestore db;
    private List<Partido> partidos;
    private AdaptadorPartidos partidoAdapter;
    private Map<String, String> mapaPartidoIdTorneo;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_eliminar_partido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaEliminarPartido), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputTorneo = findViewById(R.id.inputTorneo);
        recyclerPartidos = findViewById(R.id.recyclerPartidos);
        txtConfirmacion = findViewById(R.id.txtConfirmacion);
        toolbar = findViewById(R.id.toolbarEliminarPartido);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        db = FirebaseFirestore.getInstance();
        partidos = new ArrayList<>();
        mapaPartidoIdTorneo = new HashMap<>();

        partidoAdapter = new AdaptadorPartidos(partidos, partido -> eliminarPartido(partido.getIdPartido()));
        recyclerPartidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPartidos.setAdapter(partidoAdapter);

        cargarTorneos();
    }

    private void cargarTorneos() {
        db.collection("torneos").get().addOnSuccessListener(querySnapshot -> {
            List<String> nombresTorneosList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String nombre = doc.getString("nombre");
                String id = doc.getId();

                if (nombre != null) {
                    nombresTorneosList.add(nombre);
                    mapaPartidoIdTorneo.put(nombre, id);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresTorneosList);
            inputTorneo.setAdapter(adapter);

            inputTorneo.setOnItemClickListener((parent, view, position, id) -> {
                String torneoSeleccionado = inputTorneo.getText().toString().trim();
                cargarPartidos(torneoSeleccionado);
            });

        }).addOnFailureListener(e -> {
            Snackbar.make(findViewById(android.R.id.content), "Error al cargar los torneos", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void cargarPartidos(String torneoSeleccionado) {
        String idTorneo = mapaPartidoIdTorneo.get(torneoSeleccionado);
        if (idTorneo != null) {
            db.collection("partidos")
                    .whereEqualTo("idTorneo", idTorneo)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Partido> nuevosPartidos = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Partido partido = doc.toObject(Partido.class);
                            partido.setIdPartido(doc.getId());
                            nuevosPartidos.add(partido);
                        }

                        partidoAdapter.updatePartidos(nuevosPartidos);
                        recyclerPartidos.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(findViewById(android.R.id.content), "Error al cargar los partidos", Snackbar.LENGTH_SHORT).show();
                    });
        }
    }

    private void onPartidoSelected(String partidoNombre) {
        String partidoId = mapaPartidoIdTorneo.get(partidoNombre);
        if (partidoId != null) {
            btnEliminarPartido.setOnClickListener(v -> eliminarPartido(partidoId));
        }
    }

    private void eliminarPartido(String partidoId) {
        if (partidoId != null) {
            db.collection("partidos").document(partidoId).delete()
                    .addOnSuccessListener(aVoid -> {
                        txtConfirmacion.setText("Partido eliminado correctamente");
                        Snackbar.make(findViewById(android.R.id.content), "Partido eliminado", Snackbar.LENGTH_SHORT).show();
                        cargarPartidos(inputTorneo.getText().toString().trim());
                    })
                    .addOnFailureListener(e -> {
                        txtConfirmacion.setText("Error al eliminar el partido");
                        Snackbar.make(findViewById(android.R.id.content), "Error al eliminar el partido", Snackbar.LENGTH_SHORT).show();
                    });
        }
    }
}
