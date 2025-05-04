package com.example.esporthub_app.vistas.equipos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.modelos.Partido;
import com.example.esporthub_app.vistas.partidos.Pantalla_DetallesPartido;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Pantalla_VerDetallesEquipo extends AppCompatActivity {
    private TextView tvNombreEquipo, tvDescripcionEquipo;
    private RecyclerView recyclerMiembros;
    private FirebaseFirestore db;
    private String idEquipo;
    private Toolbar toolbarVerDetalles;
    private MaterialButton btnVerPartidos;
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
        btnVerPartidos = findViewById(R.id.buttonVerPartidos);
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

                FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
                if (usuarioActual != null) {
                    String uid = usuarioActual.getUid();

                    db.collection("jugadores")
                            .whereEqualTo("uid", uid)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    DocumentSnapshot jugadorDoc = queryDocumentSnapshots.getDocuments().get(0);
                                    String idJugador = jugadorDoc.getId();

                                    // Crear notificación
                                    Map<String, Object> notificacion = new HashMap<>();
                                    notificacion.put("idJugador", idJugador);
                                    notificacion.put("titulo", "Nuevo equipo favorito");
                                    notificacion.put("mensaje", "Has marcado a " + tvNombreEquipo.getText().toString() + " como tu equipo favorito.");
                                    notificacion.put("fecha", new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));

                                    db.collection("notificaciones").add(notificacion)
                                            .addOnSuccessListener(documentReference ->
                                                    Snackbar.make(findViewById(R.id.pantallaVerDetallesEquipo), "Equipo añadido a favoritos", Snackbar.LENGTH_SHORT).show())
                                            .addOnFailureListener(e ->
                                                    Snackbar.make(findViewById(R.id.pantallaVerDetallesEquipo), "Error al añadir a favoritos", Snackbar.LENGTH_SHORT).show());

                                    // Añadir a favoritos del jugador
                                    db.collection("equipos").document(idEquipo).get()
                                            .addOnSuccessListener(documentSnapshotEquipo -> {
                                                if (documentSnapshotEquipo.exists()) {
                                                    String nombre = documentSnapshotEquipo.getString("nombre");
                                                    String descripcion = documentSnapshotEquipo.getString("descripcion");
                                                    Long puntosLong = documentSnapshotEquipo.getLong("puntos");
                                                    int puntos = puntosLong != null ? puntosLong.intValue() : 0;
                                                    List<Jugador> miembros = (List<Jugador>) documentSnapshotEquipo.get("miembros");
                                                    List<Partido> partidos = (List<Partido>) documentSnapshotEquipo.get("partidos");
                                                    List<String> idMiembros = (List<String>) documentSnapshotEquipo.get("idMiembros");


                                                    Equipo equipoFavorito = new Equipo(idEquipo,nombre,descripcion,miembros,puntos,partidos,idMiembros);


                                                    db.collection("jugadores").document(idJugador)
                                                            .update("equiposFavoritos", FieldValue.arrayUnion(equipoFavorito))
                                                            .addOnSuccessListener(aVoid ->
                                                                    Log.d("FAVORITOS", "Equipo completo añadido a favoritos"))
                                                            .addOnFailureListener(e ->
                                                                    Log.e("FAVORITOS", "Error al añadir equipo completo", e));
                                                }
                                            });

                                }
                            });
                }

            } else {
                starButton.setImageResource(android.R.drawable.btn_star_big_off);
                // Aquí puedes opcionalmente eliminar de favoritos si quieres
            }
        });



        btnVerPartidos.setOnClickListener(v -> {
            String nombreEquipo = tvNombreEquipo.getText().toString();

            // Aquí puedes pasar tanto el idEquipo como el nombre del equipo a la actividad de partidos
            Intent intent = new Intent(Pantalla_VerDetallesEquipo.this, Pantalla_DetallesPartido.class);
            intent.putExtra("nombreEquipo", nombreEquipo);  // Pasar el nombre del equipo
            startActivity(intent);
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