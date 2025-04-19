package com.example.esporthub_app.vistas.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

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
import com.example.esporthub_app.vistas.jugador.Pantalla_VerMisEquipos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerMisTorneos;
import com.example.esporthub_app.vistas.jugador.Pantalla_VerPerfil;
import com.example.esporthub_app.vistas.notificaciones.Pantalla_Notificaciones_Jugador;
import com.example.esporthub_app.vistas.settings.Pantalla_AjustesGeneral;
import com.example.esporthub_app.vistas.torneos.Pantalla_TorneosDisponibles;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Pantalla_InicioJugadores extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imgPerfil;

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
        //imgPerfil = findViewById(R.id.imgPerfil);
        // Configurar Toolbar
        setSupportActionBar(toolbar);

        // Configurar DrawerLayout y ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    // Cargar imagen de perfil (usar un Intent para abrir galería)
       // imgPerfil.setOnClickListener(v -> {
       //     Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //    startActivityForResult(intent, 100);
       // });

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
            }else if (id == R.id.nav_configuracion) {
                Intent intent = new Intent(Pantalla_InicioJugadores.this, Pantalla_AjustesGeneral.class);
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

}
