package com.example.esporthub_app.vistas.jugador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.adaptadores.AdaptadorEquiposFavoritos;

import com.example.esporthub_app.modelos.Equipo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.esporthub_app.R;

public class Pantalla_PerfilJugador extends AppCompatActivity {
    private TextView txtNombreJugador, txtEquipoActual, txtRolJuego, txtEquiposFavoritos;
    private RecyclerView recyclerEquiposFavoritos;
    private FirebaseFirestore db;

    private String emailJugador;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_perfil_jugador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaPerfilJugador), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();


        // Obtener referencias de las vistas
        txtNombreJugador = findViewById(R.id.txtNombreJugador);
        txtEquipoActual = findViewById(R.id.txtEquipoActual);
        txtRolJuego = findViewById(R.id.txtRolJuego);
        txtEquiposFavoritos = findViewById(R.id.txtEquiposFavoritos);
        recyclerEquiposFavoritos = findViewById(R.id.recyclerEquiposFavoritos);

        toolbar = findViewById(R.id.toolbarPerfilJugador);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());


        emailJugador = getIntent().getStringExtra("email");
        // Configurar RecyclerView
        recyclerEquiposFavoritos.setLayoutManager(new LinearLayoutManager(this));

        // Cargar los datos del jugador desde Firestore
        cargarPerfilJugador(emailJugador);
    }

    private void cargarPerfilJugador(String email) {
        // Primero, buscar en la colección "usuarios" por el email
        db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Obtener el id del documento
                            String idUsuario = document.getId();  // Este es el id del documento en la colección "usuarios"

                            // Ahora que tenemos el idUsuario, buscamos en la colección "jugadores" usando este id
                            cargarDatosJugador(idUsuario);
                        }
                    } else {
                        Toast.makeText(Pantalla_PerfilJugador.this, "No se encontró el jugador", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Pantalla_PerfilJugador.this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para cargar los datos del jugador desde la colección "jugadores"
    private void cargarDatosJugador(String idUsuario) {
        // Buscar en la colección "jugadores" usando el campo idUsuario
        db.collection("jugadores")
                .whereEqualTo("idUsuario", idUsuario)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String nombre = document.getString("nombre");
                            String equipoActual = document.getString("equipoActual");
                            String rolJuego = document.getString("rolJuego");
                            List<Map<String, Object>> equiposFavoritosRaw = (List<Map<String, Object>>) document.get("equiposFavoritos");

                            if (equiposFavoritosRaw != null) {
                                ArrayList<String> nombresEquipos = new ArrayList<>();

                                for (Map<String, Object> equipo : equiposFavoritosRaw) {
                                    String nombreEquipo = (String) equipo.get("nombre");
                                    if (nombre != null) {
                                        nombresEquipos.add(nombreEquipo);
                                    }
                                }

                                AdaptadorEquiposFavoritos adapter = new AdaptadorEquiposFavoritos(nombresEquipos);
                                recyclerEquiposFavoritos.setAdapter(adapter);
                            }



                            // Actualizar la UI con los datos del jugador
                            txtNombreJugador.setText(nombre);
                            txtEquipoActual.setText(equipoActual);
                            txtRolJuego.setText(rolJuego);

                        }
                    } else {
                        Toast.makeText(Pantalla_PerfilJugador.this, "No se encontró el jugador en la colección 'jugadores'", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Pantalla_PerfilJugador.this, "Error al cargar los datos del jugador", Toast.LENGTH_SHORT).show();
                });
    }

}