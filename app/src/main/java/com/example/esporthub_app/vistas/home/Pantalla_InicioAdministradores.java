package com.example.esporthub_app.vistas.home;

import android.content.Intent;
import android.os.Bundle;

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
import com.example.esporthub_app.vistas.auth.Pantalla_InicioSesion;
import com.example.esporthub_app.vistas.equipos.Pantalla_EquiposDisponibles;
import com.example.esporthub_app.vistas.equipos.Pantalla_GestionarEquipos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerJugadores;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerMisEquipos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerMisTorneos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerPerfil;
import com.example.esporthub_app.vistas.notificaciones.Pantalla_Notificaciones_Jugador;
import com.example.esporthub_app.vistas.partidos.Pantalla_CrearPartido;
import com.example.esporthub_app.vistas.partidos.Pantalla_EliminarPartido;
import com.example.esporthub_app.vistas.settings.Pantalla_AjustesGeneral;
import com.example.esporthub_app.vistas.torneos.Pantalla_CrearTorneo;
import com.example.esporthub_app.vistas.torneos.Pantalla_EliminarTorneo;
import com.example.esporthub_app.vistas.torneos.Pantalla_TorneosDisponibles;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Pantalla_InicioAdministradores extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbarAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_inicio_administradores);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaInicioAdministrador), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout = findViewById(R.id.pantallaInicioAdministrador);
        navigationView = findViewById(R.id.nav_view_admin);
        toolbarAdmin = findViewById(R.id.toolbarAdministrador);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbarAdmin,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_crear_torneo) {
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_CrearTorneo.class);
                startActivity(intent);
            } else if (id == R.id.nav_eliminar_torneo) {
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_EliminarTorneo.class);
                startActivity(intent);
            }else if (id == R.id.nav_crear_partido) {
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_CrearPartido.class);
                startActivity(intent);
            }else if (id == R.id.nav_eliminar_partido) {
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_EliminarPartido.class);
                startActivity(intent);
            }else if (id == R.id.nav_ver_jugadores) {
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_VerJugadores.class);
                startActivity(intent);
            }else if (id == R.id.nav_gestionar_equipos) {
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_GestionarEquipos.class);
                startActivity(intent);
            }else if (id == R.id.nav_cerrar_sesion) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Pantalla_InicioAdministradores.this, Pantalla_InicioSesion.class);
                startActivity(intent);
                finish();
            }
            // Cerrar el Drawer cuando se selecciona un ítem
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}