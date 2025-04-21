package com.example.esporthub_app.vistas.torneos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Torneo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.snackbar.Snackbar;

public class Pantalla_CrearTorneo extends AppCompatActivity {

    private TextInputEditText etNombreTorneo, etDescripcion, etFechaInicio, etFechaFin, etMaxParticipantes;
    private MaterialButton btnCrearTorneo;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_crear_torneo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaCrearTorneo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vincular vistas
        etNombreTorneo = findViewById(R.id.etNombreTorneo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        etMaxParticipantes = findViewById(R.id.etMaxParticipantes);
        btnCrearTorneo = findViewById(R.id.btnCrearTorneo);
        toolbar = findViewById(R.id.toolbarCrearTorneo);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Mostrar DatePicker al hacer clic
        etFechaInicio.setOnClickListener(view -> mostrarDatePicker(etFechaInicio));
        etFechaFin.setOnClickListener(view -> mostrarDatePicker(etFechaFin));

        // Guardar torneo al hacer clic en el botón
        btnCrearTorneo.setOnClickListener(v -> guardarTorneoEnFirestore());
    }

    private void mostrarDatePicker(TextInputEditText campoFecha) {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    campoFecha.setText(fechaSeleccionada);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void guardarTorneoEnFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String nombre = etNombreTorneo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicio = etFechaInicio.getText().toString().trim();
        String fechaFin = etFechaFin.getText().toString().trim();
        String maxPartText = etMaxParticipantes.getText().toString().trim();

        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty() || maxPartText.isEmpty()) {
            mostrarSnackbar("Completa todos los campos");
            return;
        }

        // Verificar que la fecha de inicio sea antes que la fecha de fin
        if (!esFechaValida(fechaInicio, fechaFin)) {
            mostrarSnackbar("La fecha de inicio debe ser antes que la fecha de fin");
            return;
        }

        int maxParticipantes = Integer.parseInt(maxPartText);
        String estado = "Activo";
        String idTorneo = db.collection("torneos").document().getId();
        List<Equipo> participantes = new ArrayList<>();

        // Crear el objeto Torneo
        Torneo torneo = new Torneo(idTorneo, nombre, descripcion, fechaInicio, fechaFin, estado, maxParticipantes, participantes);

        // Guardar el torneo en Firestore
        db.collection("torneos")
                .document(idTorneo)
                .set(torneo)
                .addOnSuccessListener(unused -> {
                    mostrarSnackbar("Torneo guardado correctamente");
                    limpiarCampos();
                })
                .addOnFailureListener(e -> {
                    mostrarSnackbar("Error al guardar: " + e.getMessage());
                });
    }

    private boolean esFechaValida(String fechaInicio, String fechaFin) {
        // Convertir las fechas a objetos Date para compararlas
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date dateInicio = sdf.parse(fechaInicio);
            Date dateFin = sdf.parse(fechaFin);
            return dateInicio != null && dateFin != null && dateInicio.before(dateFin);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mostrarSnackbar(String mensaje) {
        Snackbar.make(findViewById(R.id.pantallaCrearTorneo), mensaje, Snackbar.LENGTH_SHORT).show();
    }

    private void limpiarCampos() {
        etNombreTorneo.setText("");
        etDescripcion.setText("");
        etFechaInicio.setText("");
        etFechaFin.setText("");
        etMaxParticipantes.setText("");
    }
}

