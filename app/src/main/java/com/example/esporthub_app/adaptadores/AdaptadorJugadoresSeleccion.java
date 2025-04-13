package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Jugador;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdaptadorJugadoresSeleccion extends RecyclerView.Adapter<AdaptadorJugadoresSeleccion.ViewHolder> {

    private List<Jugador> listaJugadores;
    private Set<Jugador> jugadoresSeleccionados = new HashSet<>();
    private int maxSeleccion = 5;
    private OnJugadorSeleccionChangeListener listener;

    public AdaptadorJugadoresSeleccion(List<Jugador> listaJugadores, OnJugadorSeleccionChangeListener listener) {
        this.listaJugadores = (listaJugadores != null) ? listaJugadores : new ArrayList<>();
        this.listener = listener;
    }

    public Set<Jugador> getSeleccionados() {
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
        holder.checkBox.setText(jugador.getNombre());
        holder.checkBox.setOnCheckedChangeListener(null); // previene bugs de reciclaje
        holder.checkBox.setChecked(jugadoresSeleccionados.contains(jugador));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (jugadoresSeleccionados.size() < maxSeleccion) {
                    jugadoresSeleccionados.add(jugador);
                } else {
                    buttonView.setChecked(false);
                    Snackbar.make(buttonView, "Máximo 5 jugadores", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                jugadoresSeleccionados.remove(jugador);
            }

            // Notificar al listener
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
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxJugador);
        }
    }

    // INTERFAZ INTERNA (opción 2)
    public interface OnJugadorSeleccionChangeListener {
        void onJugadorSeleccionChange(int cantidadSeleccionados);
    }
}

