package com.example.esporthub_app.vistas.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Torneo;
import com.example.esporthub_app.modelos.Jugador;

import com.example.esporthub_app.vistas.auth.Pantalla_InicioSesion;
import com.example.esporthub_app.vistas.equipos.Pantalla_EquiposDisponibles;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerMisEquipos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerMisTorneos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerPerfil;
import com.example.esporthub_app.vistas.notificaciones.Pantalla_Notificaciones_Jugador;
import com.example.esporthub_app.vistas.torneos.Pantalla_TorneosDisponibles;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_InicioJugadores extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private TextView textTorneos, textEquipos,textSaludo;
    private List<Equipo> equiposJugador = new ArrayList<>();
    private List<Torneo> listaTorneos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_inicio_jugadores);

        // Establecer márgenes según los márgenes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaInicioJugadores), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener referencias a los elementos del layout
        drawerLayout = findViewById(R.id.pantallaInicioJugadores);
        toolbar = findViewById(R.id.toolbarUsuario);
        navigationView = findViewById(R.id.nav_view);
        textTorneos = findViewById(R.id.textTorneosActivos);
        textEquipos = findViewById(R.id.textEquipos);
        textSaludo = findViewById(R.id.textSaludo);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        // Configurar Toolbar
        setSupportActionBar(toolbar);

        // Configurar DrawerLayout y ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        obtenerTorneosDelJugador();
        obtenerEquiposDelJugador();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            db.collection("usuarios")
                    .whereEqualTo("email",email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            // Cargar los datos del perfil
                            String nombre = document.getString("nombre");
                            textSaludo.setText("Hola, "+nombre+" !!");
                        } else {
                            mostrarMensaje("No se encontró el nombre del usuario.");
                        }
                    })
                    .addOnFailureListener(e -> mostrarMensaje("Error al cargar el nombre: " + e.getMessage()));
        }
        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_InicioJugadores.class);
                startActivity(intent);
            } else if (id == R.id.nav_perfil) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_VerPerfil.class);
                startActivity(intent);
            }else if (id == R.id.nav_mis_torneos) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_VerMisTorneos.class);
                startActivity(intent);
            }else if (id == R.id.nav_mis_equipos) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_VerMisEquipos.class);
                startActivity(intent);
            }else if (id == R.id.nav_explorar_torneos) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_TorneosDisponibles.class);
                startActivity(intent);
            }else if (id == R.id.nav_explorar_equipos) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_EquiposDisponibles.class);
                startActivity(intent);
            }else if (id == R.id.nav_notificaciones) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_Notificaciones_Jugador.class);
                startActivity(intent);
            }else if (id == R.id.nav_cerrar_sesion) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_InicioSesion.class);
                startActivity(intent);
                finish();
            }
            // Cerrar el Drawer cuando se selecciona un ítem
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void obtenerTorneosDelJugador() {
        String uidJugador = currentUser.getUid(); // Obtener el UID del jugador actual

        // Realizar la consulta en la colección "torneos"
        db.collection("torneos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTorneos.clear(); // Limpiar la lista antes de llenarla

                    // Recorremos todos los torneos obtenidos
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Torneo torneo = doc.toObject(Torneo.class);
                        torneo.setIdTorneo(doc.getId()); // Si necesitas luego el ID del torneo

                        boolean estaEnTorneo = false; // Variable para verificar si el jugador está en el torneo

                        // Verificamos los equipos participantes
                        if (torneo.getParticipantes() != null) {
                            for (Equipo equipo : torneo.getParticipantes()) {
                                // Verificamos si el equipo tiene miembros
                                if (equipo.getMiembros() != null) {
                                    // Recorremos los miembros del equipo
                                    for (Jugador jugador : equipo.getMiembros()) {
                                        // Verificamos si el UID del jugador coincide
                                        if (jugador.getUid().equals(uidJugador)) {
                                            estaEnTorneo = true; // El jugador está en este torneo
                                            break; // No necesitamos seguir buscando en este equipo
                                        }
                                    }
                                }

                                if (estaEnTorneo) break; // Si encontramos al jugador, dejamos de buscar más
                            }
                        }

                        // Si el jugador está en el torneo, lo agregamos a la lista
                        if (estaEnTorneo) {
                            listaTorneos.add(torneo);
                        }
                    }

                    // Actualiza la UI con la lista de torneos (puedes hacerlo como desees)
                    actualizarInterfazConTorneos();
                })
                .addOnFailureListener(e -> {
                    // Manejo de errores
                    // Podrías mostrar un mensaje en la UI
                    Log.e("Pantalla_InicioJugadores", "Error al obtener los torneos", e);
                });
    }

    private void actualizarInterfazConTorneos() {
        // Aquí puedes actualizar la interfaz, como mostrar la cantidad de torneos o los detalles

        textTorneos.setText("Torneos Activos: " + listaTorneos.size());
    }

    private void obtenerEquiposDelJugador() {
        if (currentUser != null) {
            // Consulta los equipos donde el jugador actual es miembro
            db.collection("equipos")
                    .whereArrayContains("idMiembros", currentUser.getUid()) // Compara el UID del jugador con la lista de miembros
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                equiposJugador.clear();
                                for (DocumentSnapshot document : querySnapshot) {
                                    Equipo equipo = document.toObject(Equipo.class);
                                    equiposJugador.add(equipo);
                                }

                                // Actualizar la interfaz con el número de equipos
                                textEquipos.setText("Equipos: " + equiposJugador.size());
                            } else {
                                textEquipos.setText("Equipos: 0");
                            }
                        } else {
                            textEquipos.setText("Error al obtener equipos.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        textEquipos.setText("Error al obtener equipos.");
                    });
        }
    }

    private void mostrarMensaje(String mensaje) {
        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_SHORT).show();
    }
}
