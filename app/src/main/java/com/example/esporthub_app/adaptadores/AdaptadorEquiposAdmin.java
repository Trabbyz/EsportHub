package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Equipo;

import java.util.List;
import java.util.function.Consumer;

public class AdaptadorEquiposAdmin extends RecyclerView.Adapter<AdaptadorEquiposAdmin.EquipoViewHolder> {
    private List<Equipo> lista;
    private Consumer<Equipo> onEditar;
    private Consumer<Equipo> onEliminar;

    public AdaptadorEquiposAdmin(List<Equipo> lista, Consumer<Equipo> onEditar, Consumer<Equipo> onEliminar) {
        this.lista = lista;
        this.onEditar = onEditar;
        this.onEliminar = onEliminar;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipo_admin, parent, false);
        return new EquipoViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = lista.get(position);
        holder.txtNombre.setText(equipo.getNombre());
        holder.btnEditar.setOnClickListener(v -> onEditar.accept(equipo));
        holder.btnEliminar.setOnClickListener(v -> onEliminar.accept(equipo));
        holder.itemView.setOnLongClickListener(v -> {
            onEliminar.accept(equipo);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class EquipoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        ImageButton btnEditar, btnEliminar;

        EquipoViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreEquipo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

}

