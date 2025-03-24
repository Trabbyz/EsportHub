package com.example.esporthub_app.vistas.bienvenida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporthub_app.R;
import com.example.esporthub_app.vistas.auth.Pantalla_InicioSesion;
import com.example.esporthub_app.vistas.auth.Pantalla_Registro;

public class Pantalla_Bienvenida extends AppCompatActivity {
    Button botonIniciarSesion;
    Button botonRegistrarse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaBienvenida), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        botonIniciarSesion = findViewById(R.id.btnLogin);
        botonRegistrarse = findViewById(R.id.btnRegister);

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pantalla_Bienvenida.this, Pantalla_InicioSesion.class);
                startActivity(intent);
            }
        });

        botonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pantalla_Bienvenida.this, Pantalla_Registro.class);
                startActivity(intent);
            }
        });
    }
}