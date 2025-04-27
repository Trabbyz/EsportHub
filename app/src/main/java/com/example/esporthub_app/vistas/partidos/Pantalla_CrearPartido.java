package com.example.esporthub_app.vistas.partidos;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.example.esporthub_app.modelos.Partido;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.example.esporthub_app.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Pantalla_CrearPartido extends AppCompatActivity {
    private AutoCompleteTextView inputEquipo1, inputEquipo2;
    private EditText inputFecha, inputResultado, inputUrl;
    private MaterialButton btnGuardarPartido;
    private FirebaseFirestore db;
    private List<String> nombresEquipos;
    private AutoCompleteTextView inputTorneo;
    private Map<String, String> mapaNombreIdTorneo = new HashMap<>();
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_crear_partido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaCrearPartido), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbarCrearPartido);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());


        inputEquipo1 = findViewById(R.id.inputEquipo1);
        inputEquipo2 = findViewById(R.id.inputEquipo2);
        inputFecha = findViewById(R.id.inputFecha);
        inputResultado = findViewById(R.id.inputResultado);
        inputUrl = findViewById(R.id.inputUrl);
        inputTorneo = findViewById(R.id.inputTorneo);

        btnGuardarPartido = findViewById(R.id.btnGuardarPartido);


        inputResultado.setText("0-0");
        inputResultado.setEnabled(false);
        inputResultado.setFocusable(false);
        inputResultado.setClickable(false);

        inputUrl.setText("https://www.twitch.tv/esporthub1");
        inputUrl.setEnabled(false);
        inputUrl.setFocusable(false);
        inputUrl.setClickable(false);
        db = FirebaseFirestore.getInstance();
        nombresEquipos = new ArrayList<>();

        cargarEquipos();
        cargarTorneos();


        inputFecha.setOnClickListener(v -> mostrarDatePicker());

        btnGuardarPartido.setOnClickListener(v -> guardarPartido());
    }

    private void cargarEquipos() {
        db.collection("equipos").get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String nombre = doc.getString("nombre");
                if (nombre != null) {
                    nombresEquipos.add(nombre);
                }
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, nombresEquipos
            );
            inputEquipo1.setAdapter(adapter);
            inputEquipo2.setAdapter(adapter);
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al cargar equipos", Toast.LENGTH_SHORT).show()
        );
    }

    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int anyo = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    inputFecha.setText(fechaSeleccionada);
                },
                anyo, mes, dia
        );

        datePickerDialog.show();
    }

    private void cargarTorneos() {
        db.collection("torneos").get().addOnSuccessListener(querySnapshot -> {
            List<String> nombresTorneos = new ArrayList<>();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                String nombre = doc.getString("nombre");
                String id = doc.getId();

                if (nombre != null) {
                    nombresTorneos.add(nombre);
                    mapaNombreIdTorneo.put(nombre, id);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresTorneos);
            inputTorneo.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar torneos", Toast.LENGTH_SHORT).show();
        });
    }

    private void guardarPartido() {
        String equipo1 = inputEquipo1.getText().toString().trim();
        String equipo2 = inputEquipo2.getText().toString().trim();
        String fecha = inputFecha.getText().toString().trim();


        String torneoSeleccionado = inputTorneo.getText().toString().trim();
        if (equipo1.isEmpty() || equipo2.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (equipo1.equals(equipo2)) {
            Toast.makeText(this, "Los equipos deben ser distintos", Toast.LENGTH_SHORT).show();
            return;
        }

        String idTorneo = mapaNombreIdTorneo.get(torneoSeleccionado);

        if (idTorneo == null) {
            Toast.makeText(this, "Selecciona un torneo válido", Toast.LENGTH_SHORT).show();
            return;
        }
        String idPartido = db.collection("partidos").document().getId();

        Partido partido = new Partido();
        partido.setIdTorneo(idTorneo);
        partido.setIdPartido(idPartido);
        partido.setEquipo1ID(equipo1);
        partido.setEquipo2ID(equipo2);
        partido.setFecha(fecha);
        partido.setResultado("0-0");
        partido.setUrlPartido("https://www.twitch.tv/esporthub1");

        //Guardar partido en la coleccion partidos
        db.collection("partidos")
                .document(idPartido)
                .set(partido)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Partido guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });

        // Guarda el partido en la colección "partidos"
        db.collection("partidos")
                .document(idPartido)
                .set(partido)
                .addOnSuccessListener(unused -> {
                    // Añadir el partido al equipo 1
                    db.collection("equipos")
                            .whereEqualTo("nombre", equipo1)
                            .get()
                            .addOnSuccessListener(snapshot1 -> {
                                for (DocumentSnapshot doc : snapshot1) {
                                    doc.getReference().update("partidos", FieldValue.arrayUnion(partido));
                                }
                            });

                    // Añadir el partido al equipo 2
                    db.collection("equipos")
                            .whereEqualTo("nombre", equipo2)
                            .get()
                            .addOnSuccessListener(snapshot2 -> {
                                for (DocumentSnapshot doc : snapshot2) {
                                    doc.getReference().update("partidos", FieldValue.arrayUnion(partido));
                                }
                            });

                    Toast.makeText(this, "Partido guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });


        // Obtener jugadores de ambos equipos para programar la notificación
        db.collection("equipos")
                .whereIn("nombre", Arrays.asList(equipo1, equipo2))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> uidMiembros = new ArrayList<>();

                    for (DocumentSnapshot equipoDoc : queryDocumentSnapshots) {
                        List<String> miembros = (List<String>) equipoDoc.get("idMiembros"); // estos son UID
                        if (miembros != null) {
                            uidMiembros.addAll(miembros);
                        }
                    }

                    // Calcular la fecha de notificación (un día antes del partido)
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Calendar cal = Calendar.getInstance();
                    try {
                        Date fechaPartido = formatoFecha.parse(fecha);
                        if (fechaPartido != null) {
                            cal.setTime(fechaPartido);
                            cal.add(Calendar.DAY_OF_MONTH, -1); // Un día antes
                            Date fechaNotificacion = cal.getTime();

                            String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaNotificacion);

                            // Ahora buscamos los documentos en "jugadores" por UID
                            db.collection("jugadores")
                                    .whereIn("uid", uidMiembros)
                                    .get()
                                    .addOnSuccessListener(jugadoresSnapshot -> {
                                        for (DocumentSnapshot jugadorDoc : jugadoresSnapshot) {
                                            String idJugador = jugadorDoc.getId(); // ID del documento en "jugadores"

                                            Map<String, Object> notificacion = new HashMap<>();
                                            notificacion.put("idJugador", idJugador);
                                            notificacion.put("titulo", "Partido programado mañana");
                                            notificacion.put("mensaje", "Tienes un partido entre " + equipo1 + " y " + equipo2 + " el día siguiente.");
                                            notificacion.put("fecha", fechaFormateada);
                                            notificacion.put("idPartido", idPartido);

                                            db.collection("notificaciones").add(notificacion);
                                        }
                                    });

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });


    }
}