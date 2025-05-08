package com.example.esporthub_app.adaptadores;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Partido;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdaptadorPartidos extends RecyclerView.Adapter<AdaptadorPartidos.PartidoViewHolder> {

    private List<Partido> partidos;
    private OnPartidoClickListener listener;

    public AdaptadorPartidos(List<Partido> partidos, OnPartidoClickListener listener) {
        this.partidos = partidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eliminar_partido, parent, false);
        return new PartidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        Partido partido = partidos.get(position);
        holder.txtPartido.setText(partido.getEquipo1ID() + " vs " + partido.getEquipo2ID());

        // Eliminar partido al hacer click en el botón
        holder.btnEliminarPartido.setOnClickListener(v -> listener.onEliminarClick(partido));
    }

    @Override
    public int getItemCount() {
        return partidos.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updatePartidos(List<Partido> nuevosPartidos) {
        partidos.clear();
        partidos.addAll(nuevosPartidos);
        notifyDataSetChanged();
    }

    public interface OnPartidoClickListener {
        void onEliminarClick(Partido partido);
    }

    public static class PartidoViewHolder extends RecyclerView.ViewHolder {
        TextView txtPartido;
        MaterialButton btnEliminarPartido;

        public PartidoViewHolder(View itemView) {
            super(itemView);
            txtPartido = itemView.findViewById(R.id.txtPartido);
            btnEliminarPartido = itemView.findViewById(R.id.btnEliminarPartido);
        }
    }
}


