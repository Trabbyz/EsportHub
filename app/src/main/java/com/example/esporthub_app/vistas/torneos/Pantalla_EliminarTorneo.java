package com.example.esporthub_app.vistas.torneos;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.esporthub_app.adaptadores.AdaptadorEliminarTorneo;
import com.example.esporthub_app.modelos.Torneo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_EliminarTorneo extends AppCompatActivity {
    private RecyclerView recyclerTorneos;
    private List<Torneo> listaTorneos;
    private AdaptadorEliminarTorneo adapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_eliminar_torneo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaEliminarTorneo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerTorneos = findViewById(R.id.recyclerTorneos);
        listaTorneos = new ArrayList<>();
        toolbar = findViewById(R.id.toolbarEliminarTorneo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new AdaptadorEliminarTorneo(listaTorneos, this::eliminarTorneo);
        recyclerTorneos.setLayoutManager(new LinearLayoutManager(this));
        recyclerTorneos.setAdapter(adapter);

        cargarTorneosActivos();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarTorneosActivos() {
        FirebaseFirestore.getInstance().collection("torneos")
                .whereEqualTo("estado", "Activo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTorneos.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Torneo torneo = doc.toObject(Torneo.class);
                        listaTorneos.add(torneo);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar torneos", Toast.LENGTH_SHORT).show()
                );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void eliminarTorneo(Torneo torneo) {
        FirebaseFirestore.getInstance().collection("torneos")
                .document(torneo.getIdTorneo())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Torneo eliminado", Toast.LENGTH_SHORT).show();
                    listaTorneos.remove(torneo);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                });
    }

}