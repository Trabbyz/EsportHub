package com.example.esporthub_app.vistas.jugador;

import android.annotation.SuppressLint;
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
import com.example.esporthub_app.vistas.equipos.Pantalla_VerDetallesEquipo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pantalla_VerMisEquipos extends AppCompatActivity {
    private LinearLayout layoutSinEquipos;
    private Button btnCrearEquipo, btnBuscarEquipos;
    private RecyclerView recyclerView;
    private List<Equipo> listaEquipos;
    private List<String> listaIdsDocumentos;
    private Toolbar toolbarMisEquipos;
    private AdaptadorEquipos adaptador;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_mis_equipos);

        // Ajuste de insets para pantallas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerMisEquipos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas
        btnBuscarEquipos = findViewById(R.id.btnBuscarEquipos);
        btnCrearEquipo = findViewById(R.id.btnCrearEquipo);
        layoutSinEquipos = findViewById(R.id.layoutSinEquipos);
        recyclerView = findViewById(R.id.recyclerViewEquipos);
        toolbarMisEquipos = findViewById(R.id.toolbarCrearEquipo);

        listaEquipos = new ArrayList<>();
        listaIdsDocumentos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Configurar la barra de herramientas
        setSupportActionBar(toolbarMisEquipos);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarMisEquipos.setNavigationOnClickListener(v -> finish());

        // Cargar equipos
        cargarMisEquipos();

        // Comprobar si hay equipos
        if (listaEquipos == null || listaEquipos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutSinEquipos.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutSinEquipos.setVisibility(View.GONE);
        }

        // Configurar botones
        btnCrearEquipo.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_VerMisEquipos.this, Pantalla_CrearEquipo.class);
            startActivity(intent);
        });

        btnBuscarEquipos.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_VerMisEquipos.this, Pantalla_EquiposDisponibles.class);
            startActivity(intent);
        });
    }

    // Método para cargar los equipos a los que pertenece el jugador
    private void cargarMisEquipos() {
        String uidJugador = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("equipos")
                .get()
                .addOnSuccessListener(query -> {
                    listaEquipos.clear();
                    listaIdsDocumentos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Equipo equipo = doc.toObject(Equipo.class);
                        equipo.setId(doc.getId());
                        listaIdsDocumentos.add(doc.getId());

                        if (equipo.getIdMiembros() != null && equipo.getIdMiembros().contains(uidJugador)) {
                            listaEquipos.add(equipo);
                        }
                    }

                    // Si no hay equipos, muestra el layout adecuado
                    if (listaEquipos.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutSinEquipos.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutSinEquipos.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                        // Instancia el adaptador y configura el listener
                        adaptador = new AdaptadorEquipos(listaEquipos, listaIdsDocumentos);
                        adaptador.setOnEquipoClickListener(new AdaptadorEquipos.OnEquipoClickListener() {
                            @Override
                            public void onVerDetalles(Equipo equipo) {
                                // Acción para ver detalles
                                Intent intent = new Intent(Pantalla_VerMisEquipos.this, Pantalla_VerDetallesEquipo.class);
                                intent.putExtra("idEquipo", equipo.getId()); // Suponiendo que el equipo tiene un ID
                                startActivity(intent);
                            }

                            @Override
                            public void onAbandonar(Equipo equipo, String idDocEquipo) {
                                // Acción para abandonar el equipo
                                abandonarEquipo(equipo, idDocEquipo);
                            }
                        });

                        recyclerView.setAdapter(adaptador);
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(recyclerView, "Error al cargar tus equipos", Snackbar.LENGTH_LONG).show();
                });
    }


    // Método para abandonar un equipo
    @SuppressLint("NotifyDataSetChanged")
    private void abandonarEquipo(Equipo equipo, String idDocEquipo) {
        String uidJugadorActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("equipos").document(idDocEquipo).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Equipo equipoActualizado = documentSnapshot.toObject(Equipo.class);
                    List<String> idMiembros = (List<String>) documentSnapshot.get("idMiembros");
                    equipoActualizado.setIdMiembros(idMiembros);
                    List<Map<String, Object>> miembrosRaw = (List<Map<String, Object>>) documentSnapshot.get("miembros");

                    List<Jugador> miembrosConvertidos = new ArrayList<>();
                    if (miembrosRaw != null) {
                        for (Map<String, Object> m : miembrosRaw) {
                            String uid = (String) m.get("uid");
                            String nombre = (String) m.get("nombre");
                            miembrosConvertidos.add(new Jugador(uid, nombre));
                        }
                    }
                    equipoActualizado.setMiembros(miembrosConvertidos);

                    if (equipoActualizado == null) {
                        Snackbar.make(recyclerView, "Error al obtener datos del equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (equipoActualizado.getIdMiembros() == null || !equipoActualizado.getIdMiembros().contains(uidJugadorActual)) {
                        Snackbar.make(recyclerView, "No perteneces a este equipo", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    equipoActualizado.getIdMiembros().remove(uidJugadorActual);

                    // Crear nuevo array sin el jugador actual
                    List<Map<String, Object>> nuevosMiembros = new ArrayList<>();
                    for (Jugador j : equipoActualizado.getMiembros()) {
                        if (j.getUid() != null && !j.getUid().equals(uidJugadorActual)) {
                            Map<String, Object> jugadorMap = new HashMap<>();
                            jugadorMap.put("uid", j.getUid());
                            jugadorMap.put("nombre", j.getNombre());
                            nuevosMiembros.add(jugadorMap);
                        }
                    }

                    Map<String, Object> datosActualizados = new HashMap<>();
                    datosActualizados.put("idMiembros", equipoActualizado.getIdMiembros());
                    datosActualizados.put("miembros", nuevosMiembros);

                    db.collection("equipos").document(idDocEquipo)
                            .update(datosActualizados)
                            .addOnSuccessListener(unused -> {
                                // Ahora actualizamos el campo "equipoActual" del jugador
                                db.collection("jugadores")
                                        .whereEqualTo("uid", uidJugadorActual)
                                        .get()
                                        .addOnSuccessListener(jugadorSnapshot -> {
                                            if (!jugadorSnapshot.isEmpty()) {
                                                DocumentSnapshot jugadorDoc = jugadorSnapshot.getDocuments().get(0);
                                                String jugadorDocId = jugadorDoc.getId();

                                                // Verificar en qué equipos sigue estando el jugador
                                                db.collection("equipos")
                                                        .whereArrayContains("idMiembros", uidJugadorActual)
                                                        .get()
                                                        .addOnSuccessListener(equiposSnapshot -> {
                                                            if (!equiposSnapshot.isEmpty()) {
                                                                // El jugador aún está en otros equipos
                                                                DocumentSnapshot equipoDoc = equiposSnapshot.getDocuments().get(0);
                                                                String nombreNuevoEquipo = equipoDoc.getString("nombre");
                                                                db.collection("jugadores").document(jugadorDocId)
                                                                        .update("equipoActual", nombreNuevoEquipo)
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            Snackbar.make(recyclerView, "Has abandonado el equipo", Snackbar.LENGTH_SHORT).show();
                                                                            cargarMisEquipos();
                                                                            // Eliminar el equipo de la lista para que el RecyclerView se actualice
                                                                            listaEquipos.remove(equipo);
                                                                            adaptador.notifyDataSetChanged();
                                                                        });
                                                            } else {
                                                                // Ya no está en ningún equipo, vaciar el campo
                                                                db.collection("jugadores").document(jugadorDocId)
                                                                        .update("equipoActual", "")
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            Snackbar.make(recyclerView, "Has abandonado el equipo", Snackbar.LENGTH_SHORT).show();
                                                                            cargarMisEquipos();
                                                                            // Eliminar el equipo de la lista para que el RecyclerView se actualice
                                                                            listaEquipos.remove(equipo);
                                                                            adaptador.notifyDataSetChanged();
                                                                        });
                                                            }
                                                        });
                                            }
                                        });

                            });
                });
    }

}
