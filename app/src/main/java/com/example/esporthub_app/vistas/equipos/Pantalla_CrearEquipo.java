package com.example.esporthub_app.vistas.equipos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.esporthub_app.adaptadores.AdaptadorJugadoresSeleccion;
import com.example.esporthub_app.modelos.Jugador;

import java.util.List;

public class Pantalla_CrearEquipo extends AppCompatActivity {
    private TextView tvContadorMiembros;
    private RecyclerView recyclerJugadores;
    private Toolbar toolbarCrearEquipo;
    private Button btnCrearEquipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_crear_equipo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutCrearEquipo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvContadorMiembros = findViewById(R.id.tvMiembros);
        recyclerJugadores = findViewById(R.id.recyclerJugadores);
        toolbarCrearEquipo = findViewById(R.id.toolbarCrearEquipo);
        btnCrearEquipo = findViewById(R.id.btnCrearEquipo);
        
        setSupportActionBar(toolbarCrearEquipo);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarCrearEquipo.setNavigationOnClickListener(v -> finish());
        
        List<Jugador> jugadoresDisponibles = obtenerJugadoresSinEquipo();

        AdaptadorJugadoresSeleccion adaptador = new AdaptadorJugadoresSeleccion(
                jugadoresDisponibles, new AdaptadorJugadoresSeleccion.OnJugadorSeleccionChangeListener() {
                    @Override
                    public void onJugadorSeleccionChange(int cantidadSeleccionados) {
                        tvContadorMiembros.setText("Miembros: " + cantidadSeleccionados + " / 5");
                    }
                }
        );

        recyclerJugadores.setLayoutManager(new LinearLayoutManager(this));
        recyclerJugadores.setAdapter(adaptador);
        
        
        btnCrearEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarEquipo();
            }
        });
    }

    private void guardarEquipo() {

    }

    private List<Jugador> obtenerJugadoresSinEquipo() {
        return null;
    }
}