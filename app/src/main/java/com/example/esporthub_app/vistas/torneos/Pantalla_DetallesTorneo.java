package com.example.esporthub_app.vistas.torneos;

import android.os.Bundle;
import android.widget.Button;
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
import com.example.esporthub_app.adaptadores.AdaptadorParticipantesTorneo;
import com.example.esporthub_app.modelos.Torneo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener ID del torneo desde el intent
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
                        Toast.makeText(this, "Torneo no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar el torneo", Toast.LENGTH_SHORT).show();
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

// Suponiendo que recibiste el objeto Torneo con la lista completa
            adaptadorParticipantes = new AdaptadorParticipantesTorneo(torneo.getParticipantes(), this);
            recyclerParticipantes.setAdapter(adaptadorParticipantes);
        }

        // Ejemplo de comportamiento para botón
        btnInscribirse.setOnClickListener(v -> {
            Toast.makeText(this, "Inscripción enviada (simulada)", Toast.LENGTH_SHORT).show();
        });
    }
}