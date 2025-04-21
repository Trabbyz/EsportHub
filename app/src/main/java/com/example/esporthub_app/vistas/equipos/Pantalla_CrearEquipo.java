package com.example.esporthub_app.vistas.equipos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.esporthub_app.adaptadores.AdaptadorJugadoresSeleccion;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_CrearEquipo extends AppCompatActivity {

    private TextView tvContadorMiembros;
    private RecyclerView recyclerJugadores;
    private Toolbar toolbarCrearEquipo;
    private Button btnCrearEquipo;
    private EditText etNombreEquipo, etDescripcionEquipo;
    private AdaptadorJugadoresSeleccion adaptador;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_crear_equipo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaCrearEquipo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvContadorMiembros = findViewById(R.id.tvMiembros);
        recyclerJugadores = findViewById(R.id.recyclerJugadores);
        toolbarCrearEquipo = findViewById(R.id.toolbarCrearEquipo);
        btnCrearEquipo = findViewById(R.id.btnCrearEquipo);
        etNombreEquipo = findViewById(R.id.etNombreEquipo);
        etDescripcionEquipo = findViewById(R.id.etDescripcionEquipo);

        db = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbarCrearEquipo);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarCrearEquipo.setNavigationOnClickListener(v -> finish());

        cargarJugadoresSinEquipo();

        btnCrearEquipo.setOnClickListener(v -> guardarEquipo());
    }

    private void cargarJugadoresSinEquipo() {
        String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("jugadores")
                .whereEqualTo("equipoActual", "")
                .get()
                .addOnCompleteListener(jugadorTask -> {
                    if (jugadorTask.isSuccessful() && jugadorTask.getResult() != null) {
                        List<Jugador> jugadores = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : jugadorTask.getResult()) {
                            String id = doc.getString("uid");

                            // Excluir al usuario actual
                            if (id == null || id.equals(idUsuarioActual)) continue;

                            String nombre = doc.getString("nombre");
                            String rolJuego = doc.getString("rolJuego");
                            Jugador jugador = new Jugador(id, nombre, rolJuego);
                            jugadores.add(jugador);
                        }

                        adaptador = new AdaptadorJugadoresSeleccion(jugadores, cantidadSeleccionados ->
                                tvContadorMiembros.setText("Miembros: " + cantidadSeleccionados + " / 5")
                        );

                        recyclerJugadores.setLayoutManager(new LinearLayoutManager(this));
                        recyclerJugadores.setAdapter(adaptador);

                    } else {
                        Log.e("Firestore", "Error al obtener jugadores sin equipo", jugadorTask.getException());
                        Toast.makeText(this, "Error al cargar jugadores", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void guardarEquipo() {
        String nombre = etNombreEquipo.getText().toString().trim();
        String descripcion = etDescripcionEquipo.getText().toString().trim();

        List<Jugador> miembros = adaptador.getSeleccionados();

        if (miembros.size() < 1) {
            Toast.makeText(this, "Selecciona al menos un jugador", Toast.LENGTH_SHORT).show();
            return;
        }

        String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<String> idMiembros = new ArrayList<>();
        for (Jugador j : miembros) {
            idMiembros.add(j.getUid());
        }

        if (!idMiembros.contains(idUsuarioActual)) {
            db.collection("jugadores")
                    .whereEqualTo("uid", idUsuarioActual)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            DocumentSnapshot doc = snapshot.getDocuments().get(0);
                            Jugador jugadorActual = doc.toObject(Jugador.class);
                            miembros.add(jugadorActual);
                            idMiembros.add(idUsuarioActual);

                            Equipo nuevoEquipo = new Equipo(nombre, descripcion, miembros, 5, idMiembros);
                            db.collection("equipos")
                                    .add(nuevoEquipo)
                                    .addOnSuccessListener(documentReference -> {
                                        // 🔧 Actualizar el campo idEquipo con el ID generado por Firestore
                                        String idGenerado = documentReference.getId();
                                        documentReference.update("idEquipo", idGenerado)
                                                .addOnSuccessListener(unused -> {
                                                    actualizarJugadoresConEquipoActual(miembros, nombre);
                                                    Toast.makeText(this, "Equipo creado con éxito", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Error al actualizar el ID del equipo", e);
                                                    Toast.makeText(this, "Error al guardar el ID del equipo", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error al crear equipo", e);
                                        Toast.makeText(this, "Error al guardar el equipo", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Toast.makeText(this, "No se encontró el usuario actual en la colección de jugadores", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al buscar el jugador actual", e);
                        Toast.makeText(this, "Error al obtener datos del jugador actual", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Equipo nuevoEquipo = new Equipo(nombre, descripcion, miembros, 5, idMiembros);

            db.collection("equipos")
                    .add(nuevoEquipo)
                    .addOnSuccessListener(documentReference -> {
                        // 🔧 También aquí, actualizar el campo idEquipo
                        String idGenerado = documentReference.getId();
                        documentReference.update("idEquipo", idGenerado)
                                .addOnSuccessListener(unused -> {
                                    actualizarJugadoresConEquipoActual(miembros, nombre);
                                    Toast.makeText(this, "Equipo creado con éxito", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al actualizar el ID del equipo", e);
                                    Toast.makeText(this, "Error al guardar el ID del equipo", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al crear equipo", e);
                        Toast.makeText(this, "Error al guardar el equipo", Toast.LENGTH_SHORT).show();
                    });
        }

    }


    private void actualizarJugadoresConEquipoActual(List<Jugador> miembros, String nombreEquipo) {
        for (Jugador jugador : miembros) {
            db.collection("jugadores")
                    .whereEqualTo("uid", jugador.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            db.collection("jugadores")
                                    .document(doc.getId()) // Aquí ya tienes el ID real
                                    .update("equipoActual", nombreEquipo)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Jugador actualizado con equipo actual: " + nombreEquipo);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error al actualizar jugador: " + jugador.getNombre(), e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error buscando jugador: " + jugador.getNombre(), e);
                    });
        }
    }



}
