package com.example.esporthub_app.adaptadores;

import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.modelos.Torneo;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.esporthub_app.R;
import com.google.android.material.button.MaterialButton;

public class AdaptadorEliminarTorneo extends RecyclerView.Adapter<AdaptadorEliminarTorneo.ViewHolder> {

    private final List<Torneo> listaTorneos;
    private final OnEliminarClickListener eliminarClickListener;

    public interface OnEliminarClickListener {
        void onEliminarClick(Torneo torneo);
    }

    public AdaptadorEliminarTorneo(List<Torneo> listaTorneos, OnEliminarClickListener listener) {
        this.listaTorneos = listaTorneos;
        this.eliminarClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_eliminar_torneo, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Torneo torneo = listaTorneos.get(position);
        holder.txtNombreTorneo.setText(torneo.getNombre());
        holder.txtFechaTorneo.setText("Fecha: " + torneo.getFechaInicio());
        holder.txtMaxParticipantes.setText("Máx participantes: " + torneo.getMaxParticipantes());

        holder.btnEliminar.setOnClickListener(v -> eliminarClickListener.onEliminarClick(torneo));
    }

    @Override
    public int getItemCount() {
        return listaTorneos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreTorneo, txtFechaTorneo, txtMaxParticipantes;
        MaterialButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreTorneo = itemView.findViewById(R.id.txtNombreTorneo);
            txtFechaTorneo = itemView.findViewById(R.id.txtFechaTorneo);
            txtMaxParticipantes = itemView.findViewById(R.id.txtMaxParticipantes);
            btnEliminar = itemView.findViewById(R.id.btnEliminarTorneo);
        }
    }
}


