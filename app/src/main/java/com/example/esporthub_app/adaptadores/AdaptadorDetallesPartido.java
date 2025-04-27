package com.example.esporthub_app.adaptadores;

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

public class AdaptadorDetallesPartido extends RecyclerView.Adapter<AdaptadorDetallesPartido.PartidoViewHolder> {

    private List<Partido> partidosList;
    private OnVerPartidoListener listener;

    // Constructor con el listener
    public AdaptadorDetallesPartido(List<Partido> partidosList, OnVerPartidoListener listener) {
        this.partidosList = partidosList;
        this.listener = listener;
    }

    public interface OnVerPartidoListener {
        void onVerPartido(Partido partido);
    }


    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partido, parent, false);
        return new PartidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        Partido partido = partidosList.get(position);
        holder.txtEquipos.setText(partido.getEquipo1ID() + " vs " + partido.getEquipo2ID());
        holder.txtFechaPartido.setText(partido.getFecha());
        holder.txtResultado.setText(partido.getResultado());

        // Configurar el listener para el botón que abrirá la URL
        holder.btnVerPartido.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerPartido(partido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return partidosList.size();
    }

    public static class PartidoViewHolder extends RecyclerView.ViewHolder {
        TextView txtEquipos, txtFechaPartido, txtResultado;
        MaterialButton btnVerPartido;

        public PartidoViewHolder(View itemView) {
            super(itemView);
            txtEquipos = itemView.findViewById(R.id.txtEquipos);
            txtFechaPartido = itemView.findViewById(R.id.txtFechaPartido);
            txtResultado = itemView.findViewById(R.id.txtResultado);
            btnVerPartido = itemView.findViewById(R.id.btnVerPartido);
        }
    }
}
