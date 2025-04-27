package com.example.esporthub_app.vistas.equipos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.adaptadores.AdaptadorEquiposAdmin;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.modelos.Partido;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_GestionarEquipos extends AppCompatActivity {
    private RecyclerView recyclerEquipos;
    private FloatingActionButton fabAgregarEquipo;
    private FirebaseFirestore db;
    private List<Equipo> listaEquipos = new ArrayList<>();
    private AdaptadorEquiposAdmin adaptadorEquipos;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_gestionar_equipos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaGestionEquipos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        recyclerEquipos = findViewById(R.id.recyclerEquipos);
        fabAgregarEquipo = findViewById(R.id.fabAgregarEquipo);
        recyclerEquipos.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbarEquipos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        adaptadorEquipos = new AdaptadorEquiposAdmin(listaEquipos, this::editarEquipo, this::eliminarEquipo);
        recyclerEquipos.setAdapter(adaptadorEquipos);

        fabAgregarEquipo.setOnClickListener(v -> mostrarDialogoEquipo(null));

        cargarEquipos();
    }



    @SuppressLint("NotifyDataSetChanged")
    private void cargarEquipos() {
        db.collection("equipos").get().addOnSuccessListener(snapshot -> {
            listaEquipos.clear();
            for (QueryDocumentSnapshot doc : snapshot) {
                Equipo equipo = doc.toObject(Equipo.class);
                equipo.setId(doc.getId());
                listaEquipos.add(equipo);
            }
            adaptadorEquipos.notifyDataSetChanged();
        });
    }

    private void mostrarDialogoEquipo(@Nullable Equipo equipoExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(equipoExistente == null ? "Agregar Equipo" : "Editar Equipo");

        // Contenedor con dos campos: nombre y descripción
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del equipo");
        layout.addView(inputNombre);

        final EditText inputDescripcion = new EditText(this);
        inputDescripcion.setHint("Descripción");
        layout.addView(inputDescripcion);

        if (equipoExistente != null) {
            inputNombre.setText(equipoExistente.getNombre());
            inputDescripcion.setText(equipoExistente.getDescripcion());
        }

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String descripcion = inputDescripcion.getText().toString().trim();
            List<Jugador> miembros = new ArrayList<>();
            List<Partido> partidos = new ArrayList<>();
            List<String> idMiembros = new ArrayList<>();
            if (!nombre.isEmpty() && !descripcion.isEmpty()) {
                if (equipoExistente == null) {
                    agregarEquipo(new Equipo(nombre, descripcion,miembros,5,partidos,idMiembros));
                } else {
                    equipoExistente.setNombre(nombre);
                    equipoExistente.setDescripcion(descripcion);
                    actualizarEquipo(equipoExistente);
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void agregarEquipo(Equipo equipo) {
        db.collection("equipos").add(equipo).addOnSuccessListener(ref -> {
            // Aquí ya tienes el ID generado por Firestore
            String idGenerado = ref.getId();
            equipo.setId(idGenerado);

            // Ahora actualizas el documento para incluir el ID dentro del objeto
            db.collection("equipos").document(idGenerado).set(equipo)
                    .addOnSuccessListener(unused -> {
                        cargarEquipos();
                        Toast.makeText(this, "Equipo agregado", Toast.LENGTH_SHORT).show();
                    });
        });
    }


    private void actualizarEquipo(Equipo equipo) {
        db.collection("equipos").document(equipo.getId()).set(equipo).addOnSuccessListener(unused -> {
            cargarEquipos();
            Toast.makeText(this, "Equipo actualizado", Toast.LENGTH_SHORT).show();
        });
    }

    private void editarEquipo(Equipo equipo) {
        mostrarDialogoEquipo(equipo);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void eliminarEquipo(Equipo equipo) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar equipo")
                .setMessage("¿Estás seguro de que quieres eliminar el equipo \"" + equipo.getNombre() + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    db.collection("equipos").document(equipo.getId()).delete()
                            .addOnSuccessListener(unused -> {
                                listaEquipos.remove(equipo);
                                adaptadorEquipos.notifyDataSetChanged();
                                Toast.makeText(this, "Equipo eliminado", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}