package com.example.esporthub_app.vistas.notificaciones;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_Notificaciones_Jugador extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AdaptadorNotificaciones adapter;
    private List<Notificacion> listaNotificaciones;
    private FirebaseFirestore db;
    private NavigationView limpiarNotificaciones;

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

        adapter = new AdaptadorNotificaciones(listaNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarNotificaciones();
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
                                        adapter.notifyDataSetChanged();
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
            listaNotificaciones.clear();
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
