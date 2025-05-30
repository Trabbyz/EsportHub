package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Equipo;

import java.util.List;


public class AdaptadorEquipos extends RecyclerView.Adapter<AdaptadorEquipos.EquipoViewHolder> {

    private List<Equipo> listaEquipos;
    private List<String> listaIdsDocumentos; // nueva lista para IDs
    private OnEquipoClickListener listener;

    public void setOnEquipoClickListener(OnEquipoClickListener listener) {
        this.listener = listener;
    }

    public AdaptadorEquipos(List<Equipo> listaEquipos, List<String> listaIdsDocumentos) {
        this.listaEquipos = listaEquipos;
        this.listaIdsDocumentos = listaIdsDocumentos;
    }

    public interface OnEquipoClickListener {
        void onVerDetalles(Equipo equipo);
        void onAbandonar(Equipo equipo, String idDocEquipo);
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipoactual, parent, false);
        return new EquipoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = listaEquipos.get(position);
        holder.tvNombre.setText(equipo.getNombre());
        holder.tvDescripcion.setText(equipo.getDescripcion());
        String idDocEquipo = equipo.getId();
        holder.btnVerDetalles.setOnClickListener(v -> listener.onVerDetalles(equipo));
        holder.btnAbandonar.setOnClickListener(v -> listener.onAbandonar(equipo, idDocEquipo));


    }

    @Override
    public int getItemCount() {
        return listaEquipos.size();
    }

    static class EquipoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion;
        Button btnVerDetalles, btnAbandonar;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.textEquipoNombre);
            tvDescripcion = itemView.findViewById(R.id.textEquipoDescripcion);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnAbandonar = itemView.findViewById(R.id.btnAbandonarEquipo);
        }
    }
}

