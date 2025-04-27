package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.modelos.Equipo;

import java.util.List;

public class AdaptadorEquiposFavoritos extends RecyclerView.Adapter<AdaptadorEquiposFavoritos.EquipoViewHolder> {

    private List<String> equiposFavoritos;

    public AdaptadorEquiposFavoritos(List<String> equiposFavoritos) {
        this.equiposFavoritos = equiposFavoritos;
    }

    @Override
    public EquipoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EquipoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EquipoViewHolder holder, int position) {
        String equipo = equiposFavoritos.get(position);
        holder.equipoNombre.setText(equipo);
    }

    @Override
    public int getItemCount() {
        return equiposFavoritos.size();
    }

    public class EquipoViewHolder extends RecyclerView.ViewHolder {

        public TextView equipoNombre;

        public EquipoViewHolder(View itemView) {
            super(itemView);
            equipoNombre = itemView.findViewById(android.R.id.text1);
        }
    }
}

