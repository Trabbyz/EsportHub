package com.example.esporthub_app.vistas.home;

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
import com.google.android.material.navigation.NavigationView;

public class Pantalla_InicioJugadores extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

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
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        // Configurar Toolbar
        setSupportActionBar(toolbar);

        // Configurar DrawerLayout y ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Agregar el listener del toggle al DrawerLayout
        drawerLayout.addDrawerListener(toggle);

        // Sincronizar el estado para mostrar el ícono adecuado
        toggle.syncState();

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                // Acción al presionar "Inicio"
            } else if (id == R.id.nav_configuracion) {
                // Acción al presionar "Configuración"
            }
            // Cerrar el Drawer cuando se selecciona un ítem
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
