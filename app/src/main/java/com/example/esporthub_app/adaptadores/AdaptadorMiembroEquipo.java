package com.example.esporthub_app.adaptadores;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.modelos.Jugador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import java.util.List;


import com.example.esporthub_app.R;



public class AdaptadorMiembroEquipo extends RecyclerView.Adapter<AdaptadorMiembroEquipo.MiembroViewHolder> {

    private List<Jugador> listaMiembros;

    public AdaptadorMiembroEquipo(List<Jugador> listaMiembros) {
        this.listaMiembros = listaMiembros;
    }

    @NonNull
    @Override
    public MiembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_miembro_equipo, parent, false);
        return new MiembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiembroViewHolder holder, int position) {
        Jugador jugador = listaMiembros.get(position);
        holder.tvNombre.setText(jugador.getNombre());
        holder.tvRol.setText(jugador.getRolJuego());
    }

    @Override
    public int getItemCount() {
        return listaMiembros.size();
    }

    public static class MiembroViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvRol;

        public MiembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMiembro);
            tvRol = itemView.findViewById(R.id.tvRolMiembro);
        }
    }
}


