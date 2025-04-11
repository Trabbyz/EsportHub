package com.example.esporthub_app.vistas.jugador;

import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class Pantalla_VerMisTorneos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdaptadorTorneos torneosAdapter;
    private List<Torneo> listaTorneos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_mis_torneos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewTorneos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        torneosAdapter = new AdaptadorTorneos(listaTorneos);

        recyclerView.setAdapter(torneosAdapter);
    }
}