package com.example.esporthub_app.vistas.jugador;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporthub_app.R;
public class Pantalla_VerPerfil extends AppCompatActivity {

    private Toolbar toolbarPerfil;
    private Button btnEditarPerfil, btnGuardar, btnCancelar;
    private EditText editNombre, editEmail, editRol, editEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ver_detalles_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaVerPerfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar
        toolbarPerfil = findViewById(R.id.toolbarPerfil);
        setSupportActionBar(toolbarPerfil);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarPerfil.setNavigationOnClickListener(v -> finish());

        // EditTexts
        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        editRol = findViewById(R.id.editRol);
        editEquipo = findViewById(R.id.editEquipo);

        // Botones
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Estado inicial
        setEditable(false);
        btnGuardar.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);

        // Acción al pulsar "Editar Perfil"
        btnEditarPerfil.setOnClickListener(v -> {
            setEditable(true);
            btnEditarPerfil.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);
        });

        // Acción al pulsar "Cancelar"
        btnCancelar.setOnClickListener(v -> {
            setEditable(false);
            btnEditarPerfil.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
        });

        // Acción al pulsar "Guardar Cambios"
        btnGuardar.setOnClickListener(v -> {
            // Aquí puedes guardar en base de datos o lo que necesites

            setEditable(false);
            btnEditarPerfil.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
        });
    }

    // Método para activar o desactivar edición
    private void setEditable(boolean editable) {
        EditText[] campos = {editNombre, editEmail, editRol, editEquipo};
        for (EditText campo : campos) {
            campo.setFocusable(editable);
            campo.setFocusableInTouchMode(editable);
            campo.setClickable(editable);
            campo.setCursorVisible(editable);
        }
    }
}
