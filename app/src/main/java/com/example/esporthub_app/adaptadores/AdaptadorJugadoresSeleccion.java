package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Jugador;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorJugadoresSeleccion extends RecyclerView.Adapter<AdaptadorJugadoresSeleccion.ViewHolder> {

    private List<Jugador> listaJugadores;
    private List<Jugador> jugadoresSeleccionados = new ArrayList<>();
    private int maxSeleccion = 5;
    private OnJugadorSeleccionChangeListener listener;

    public AdaptadorJugadoresSeleccion(List<Jugador> listaJugadores, OnJugadorSeleccionChangeListener listener) {
        this.listaJugadores = (listaJugadores != null) ? listaJugadores : new ArrayList<>();
        this.listener = listener;
    }

    public List<Jugador> getSeleccionados() {
        return jugadoresSeleccionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jugador_seleccionable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Jugador jugador = listaJugadores.get(position);

        // Asignar nombre y rol
        holder.nombreTextView.setText(jugador.getNombre());
        holder.rolTextView.setText("Rol: " + jugador.getRolJuego());

        // Evitar bugs de reciclaje
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(jugadoresSeleccionados.contains(jugador));


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (jugadoresSeleccionados.size() < maxSeleccion) {
                    if (!jugadoresSeleccionados.contains(jugador)) {
                        jugadoresSeleccionados.add(jugador);
                    }
                } else {
                    buttonView.setChecked(false);
                    Snackbar.make(buttonView, "Máximo 5 jugadores", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                jugadoresSeleccionados.remove(jugador);
            }

            if (listener != null) {
                listener.onJugadorSeleccionChange(jugadoresSeleccionados.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaJugadores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, rolTextView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textNombreJugador);
            rolTextView = itemView.findViewById(R.id.textRolJugador);
            checkBox = itemView.findViewById(R.id.checkBoxJugador);
        }
    }

    public interface OnJugadorSeleccionChangeListener {
        void onJugadorSeleccionChange(int cantidadSeleccionados);
    }
}



