package com.example.esporthub_app.vistas.notificaciones;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorNotificaciones;
import com.example.esporthub_app.modelos.Notificacion;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_Notificaciones_Jugador extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AdaptadorNotificaciones adapter;
    private List<Notificacion> listaNotificaciones;
    private FirebaseFirestore db;
    private TextView textSinNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_notificaciones_jugador);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaNotificaciones), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbarNotificaciones);
        recyclerView = findViewById(R.id.recyclerNotificaciones);

        listaNotificaciones = new ArrayList<>();
        db = FirebaseFirestore.getInstance();


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        textSinNotificaciones = findViewById(R.id.textSinNotificaciones);

        cargarNotificaciones();
        if (listaNotificaciones.isEmpty()) {
            textSinNotificaciones.setVisibility(View.VISIBLE);
        } else {
            textSinNotificaciones.setVisibility(View.GONE);
        }


        adapter = new AdaptadorNotificaciones(listaNotificaciones, new AdaptadorNotificaciones.OnNotificacionClickListener() {
            @Override
            public void onEliminarNotificacion(Notificacion notificacion) {
                db.collection("notificaciones").document(notificacion.getIdNotificacion())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            int index = listaNotificaciones.indexOf(notificacion);
                            if (index != -1) {
                                listaNotificaciones.remove(index);
                                adapter.notifyItemRemoved(index);
                                adapter.notifyItemRangeChanged(index, listaNotificaciones.size());
                            }
                        });
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarNotificaciones() {
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            Snackbar.make(findViewById(R.id.pantallaNotificaciones), "Usuario no autenticado", Snackbar.LENGTH_LONG).show();
            return;
        }

        String uid = usuarioActual.getUid();

        db.collection("jugadores")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot jugadorDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String idJugador = jugadorDoc.getId();

                        db.collection("notificaciones")
                                .whereEqualTo("idJugador", idJugador)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        listaNotificaciones.clear();
                                        for (QueryDocumentSnapshot documentSnapshotNoti : task.getResult()) {
                                            String idNotificacion = documentSnapshotNoti.getId();
                                            String titulo = documentSnapshotNoti.getString("titulo");
                                            String mensaje = documentSnapshotNoti.getString("mensaje");
                                            String fecha = documentSnapshotNoti.getString("fecha");

                                            Notificacion noti = new Notificacion(idNotificacion, idJugador, titulo, mensaje, fecha);
                                            listaNotificaciones.add(noti);
                                        }

                                        // Ordenar por fecha (más reciente primero)
                                        listaNotificaciones.sort((n1, n2) -> n2.getFecha().compareTo(n1.getFecha()));
                                        adapter.notifyDataSetChanged();

                                        // Mostrar u ocultar mensaje vacío
                                        if (listaNotificaciones.isEmpty()) {
                                            textSinNotificaciones.setVisibility(View.VISIBLE);
                                        } else {
                                            textSinNotificaciones.setVisibility(View.GONE);
                                        }
                                    }

                                });

                    } else {
                        Snackbar.make(findViewById(R.id.pantallaNotificaciones), "Jugador no encontrado", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e ->
                        Snackbar.make(findViewById(R.id.pantallaNotificaciones), "Error al obtener el jugador", Snackbar.LENGTH_LONG).show()
                );
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notificaciones, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_notifications) {

            FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
            if (usuarioActual == null) {
                Snackbar.make(findViewById(R.id.pantallaNotificaciones), "Usuario no autenticado", Snackbar.LENGTH_LONG).show();
                return true;
            }

            String uid = usuarioActual.getUid();

            db.collection("jugadores")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnSuccessListener(jugadorSnapshot -> {
                        if (!jugadorSnapshot.isEmpty()) {
                            String idJugador = jugadorSnapshot.getDocuments().get(0).getId();

                            db.collection("notificaciones")
                                    .whereEqualTo("idJugador", idJugador)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        WriteBatch batch = db.batch();
                                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                            batch.delete(doc.getReference());
                                        }

                                        batch.commit().addOnSuccessListener(unused -> {
                                            listaNotificaciones.clear();
                                            adapter.notifyDataSetChanged();
                                            Snackbar.make(findViewById(R.id.pantallaNotificaciones), "Notificaciones eliminadas", Snackbar.LENGTH_SHORT).show();
                                        });
                                    });
                        }
                    })
                    .addOnFailureListener(e ->
                            Snackbar.make(findViewById(R.id.pantallaNotificaciones), "Error al eliminar notificaciones", Snackbar.LENGTH_LONG).show()
                    );

            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
