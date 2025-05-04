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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

        // Validar si ambos equipos pertenecen al torneo y no comparten jugadores
        db.collection("equipos")
                .whereIn("nombre", Arrays.asList(equipo1, equipo2))
                .get()
                .addOnSuccessListener(snapshot -> {
                    int equiposEnTorneo = 0;
                    List<List<String>> miembrosEquipos = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot) {
                        String torneoEquipo = doc.getString("idTorneo");
                        if (idTorneo.equals(torneoEquipo)) {
                            equiposEnTorneo++;

                            List<String> miembros = (List<String>) doc.get("idMiembros");
                            if (miembros != null) {
                                miembrosEquipos.add(miembros);
                            } else {
                                miembrosEquipos.add(new ArrayList<>());
                            }
                        }
                    }

                    if (equiposEnTorneo < 2) {
                        Toast.makeText(this, "Ambos equipos deben pertenecer al torneo seleccionado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Verificar si hay jugadores en común
                    Set<String> miembrosEquipo1 = new HashSet<>(miembrosEquipos.get(0));
                    Set<String> miembrosEquipo2 = new HashSet<>(miembrosEquipos.get(1));

                    miembrosEquipo1.retainAll(miembrosEquipo2); // intersección

                    if (!miembrosEquipo1.isEmpty()) {
                        Toast.makeText(this, "No se puede crear el partido: hay jugadores que pertenecen a ambos equipos", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Se puede Crear el partido
                    crearPartido(equipo1, equipo2, fecha, idTorneo);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al validar los equipos", Toast.LENGTH_SHORT).show();
                });
    }


    private void crearPartido(String equipo1, String equipo2, String fecha, String idTorneo) {
        String idPartido = db.collection("partidos").document().getId();

        Partido partido = new Partido();
        partido.setIdTorneo(idTorneo);
        partido.setIdPartido(idPartido);
        partido.setEquipo1ID(equipo1);
        partido.setEquipo2ID(equipo2);
        partido.setFecha(fecha);
        partido.setResultado("0-0");
        partido.setUrlPartido("https://www.twitch.tv/esporthub1");

        db.collection("partidos").document(idPartido).set(partido)
                .addOnSuccessListener(unused -> {
                    // Añadir el partido a ambos equipos
                    for (String nombreEquipo : Arrays.asList(equipo1, equipo2)) {
                        db.collection("equipos")
                                .whereEqualTo("nombre", nombreEquipo)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    for (DocumentSnapshot doc : snapshot) {
                                        doc.getReference().update("partidos", FieldValue.arrayUnion(partido));
                                    }
                                });
                    }

                    Toast.makeText(this, "Partido guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());

        // Notificaciones
        db.collection("equipos")
                .whereIn("nombre", Arrays.asList(equipo1, equipo2))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> uidMiembros = new ArrayList<>();

                    for (DocumentSnapshot equipoDoc : queryDocumentSnapshots) {
                        List<String> miembros = (List<String>) equipoDoc.get("idMiembros");
                        if (miembros != null) {
                            uidMiembros.addAll(miembros);
                        }
                    }

                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Calendar cal = Calendar.getInstance();
                    try {
                        Date fechaPartido = formatoFecha.parse(fecha);
                        if (fechaPartido != null) {
                            cal.setTime(fechaPartido);
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                            Date fechaNotificacion = cal.getTime();
                            String fechaFormateada = formatoFecha.format(fechaNotificacion);

                            db.collection("jugadores")
                                    .whereIn("uid", uidMiembros)
                                    .get()
                                    .addOnSuccessListener(jugadoresSnapshot -> {
                                        for (DocumentSnapshot jugadorDoc : jugadoresSnapshot) {
                                            String idJugador = jugadorDoc.getId();

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
