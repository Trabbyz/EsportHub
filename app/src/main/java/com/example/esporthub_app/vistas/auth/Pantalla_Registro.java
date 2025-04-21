package com.example.esporthub_app.vistas.auth;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Pantalla_Registro extends AppCompatActivity {
    private EditText nombreUsuario, apellidos, correoElectronico, contrasena, repetirContrasena;
    private Button crearCuenta, cancelar;
    private Spinner spinnerRoles;
    private Spinner spinnerRolJuego;
    private TextView textRolJuego;
    private String rolJuegoSeleccionado;


    private String rolSeleccionado;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantallaRegistro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombreUsuario     = findViewById(R.id.editTextNombre);
        apellidos       = findViewById(R.id.editTextApellido);
        correoElectronico = findViewById(R.id.editTextEmail);
        contrasena        = findViewById(R.id.editTextPassword);
        crearCuenta       = findViewById(R.id.btnRegistrarse);

        spinnerRoles      = findViewById(R.id.spinnerRol);
        spinnerRolJuego = findViewById(R.id.spinnerRolJuego);
        textRolJuego = findViewById(R.id.textRolJuego);

        // Cargar roles en el Spinner
        String[] roles = {"Selecciona tu rol", "Jugador", "Administrador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(adapter);


        String[] rolesJuego = {"Top", "Jungla", "Mid", "ADC", "Support"};
        ArrayAdapter<String> adapterRolJuego = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, rolesJuego);
        adapterRolJuego.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRolJuego.setAdapter(adapterRolJuego);

// Ocultarlo por defecto
        spinnerRolJuego.setVisibility(View.GONE);
        textRolJuego.setVisibility(View.GONE);

        // Manejar selección de rol
        spinnerRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rolSeleccionado = parent.getItemAtPosition(position).toString();

                if (rolSeleccionado.equals("Jugador")) {
                    spinnerRolJuego.setVisibility(View.VISIBLE);
                    textRolJuego.setVisibility(View.VISIBLE);
                } else {
                    spinnerRolJuego.setVisibility(View.GONE);
                    textRolJuego.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rolSeleccionado = null; // Ningún rol seleccionado
            }
        });

        // Instanciar FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Botón para crear cuenta
        crearCuenta.setOnClickListener(view -> {
            guardarUsuario(view);
        });

    }

    private void guardarUsuario(View view) {
        String usuarioIntroducido    = correoElectronico.getText().toString().trim();
        String contrasenaIntroducida = contrasena.getText().toString().trim();


        // Validar campos
        if (usuarioIntroducido.isEmpty()) {
            Snackbar.make(view, "Por favor, ingrese un correo electrónico", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (contrasenaIntroducida.isEmpty()) {
            Snackbar.make(view, "Por favor, ingrese una contraseña", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (contrasenaIntroducida.length() < 6) {
            Snackbar.make(view, "La contraseña debe tener al menos 6 caracteres", Snackbar.LENGTH_LONG).show();
            return;
        }



        // Crear usuario en Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(usuarioIntroducido, contrasenaIntroducida)
                .addOnCompleteListener(Pantalla_Registro.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Usuario creado con éxito en Firebase Auth
                            Log.d("REGISTRO", "createUserWithEmail:success");

                            // Ahora registrar en Firestore (y luego en SQLite)
                            registrarUsuario();
                        } else {
                            // Error en el registro
                            Log.e("REGISTRO", "createUserWithEmail:failure", task.getException());
                            Snackbar.make(view, "Error en el registro: " +
                                            task.getException().getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                            recargarInterfaz(); // Resetear campos si ocurre un error
                        }
                    }
                });
    }

    private void registrarUsuario() {
        // Obtener datos de los campos
        String nombreUs     = nombreUsuario.getText().toString();
        String apellidosTexto = apellidos.getText().toString();
        String correo       = correoElectronico.getText().toString();
        String password     = contrasena.getText().toString();
        String rol          = spinnerRoles.getSelectedItem().toString();
        String rolJuegoSeleccionado = rol.equals("Jugador") ? spinnerRolJuego.getSelectedItem().toString() : null;
        Log.d("Firestore","Rol Juego : "+rolJuegoSeleccionado);

        // Validar campos
        if (nombreUs.isEmpty() || apellidosTexto.isEmpty() ||
                correo.isEmpty() || password.isEmpty() ||
                rol.isEmpty() || "Selecciona tu rol".equals(rol)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Por favor, rellena todos los campos y selecciona un rol",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Crear objeto Usuario (modelo)
        Usuario nuevoUsuario = new Usuario(nombreUs, apellidosTexto, correo, password, rol, rolJuegoSeleccionado);
        String uidJugador = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Crear objeto Jugador
        Jugador nuevoJugador = new Jugador(uidJugador, nombreUs, "", rolJuegoSeleccionado, null);

        // Guardar en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Guardar en Firestore (colección "usuarios")
        db.collection("usuarios")
                .add(nuevoUsuario)
                .addOnSuccessListener(documentReference -> {
                    // Obtener el ID del documento creado
                    String idUsuario = documentReference.getId();
                    Log.d("Firestore", "ID de usuario guardado: " + idUsuario);



                    // Si el rol es "Jugador", también agregamos este jugador a la colección "jugadores"
                    if (rol.equals("Jugador")) {
                        nuevoJugador.setIdUsuario(idUsuario);  // Asignamos el ID a la clase Jugador

                        // Guardar el jugador en la colección "jugadores"
                        db.collection("jugadores")
                                .add(nuevoJugador)
                                .addOnSuccessListener(empleadoRef -> {
                                    Log.d("Firestore", "Jugador añadido con éxito a la colección 'jugadores'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al guardar jugador en 'jugadores'", e);
                                });
                    }

                    // Mostrar mensaje
                    Snackbar.make(findViewById(android.R.id.content),
                            "Usuario creado con éxito",
                            Snackbar.LENGTH_LONG).show();

                    // Redirigir a pantalla de login
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error al crear el usuario en Firestore",
                            Snackbar.LENGTH_LONG).show();
                    Log.e("Firestore", "Error al guardar usuario", e);
                });
    }


    private void updateUI() {
        Intent intent = new Intent(this, Pantalla_InicioSesion.class);
        startActivity(intent);
        finish();
    }

    private void recargarInterfaz() {
        correoElectronico.setText("");
        contrasena.setText("");
        repetirContrasena.setText("");
    }


}