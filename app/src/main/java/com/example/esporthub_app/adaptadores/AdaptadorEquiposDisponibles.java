package com.example.esporthub_app.adaptadores;

import android.content.Context;
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

public class AdaptadorEquiposDisponibles extends RecyclerView.Adapter<AdaptadorEquiposDisponibles.EquipoViewHolder> {
    private List<Equipo> listaEquipos;
    private List<String> listaIdsDocumentos;  // Lista para almacenar los IDs de los documentos
    private Context context;
    private OnEquipoClickListener listener;

    public interface OnEquipoClickListener {
        void onVerDetalles(Equipo equipo);
        void onUnirme(Equipo equipo, String idDocEquipo);  // Pasar el idDocEquipo aquí
    }

    public AdaptadorEquiposDisponibles(List<Equipo> listaEquipos, List<String> listaIdsDocumentos, Context context, OnEquipoClickListener listener) {
        this.listaEquipos = listaEquipos;
        this.listaIdsDocumentos = listaIdsDocumentos;  // Inicializar la lista de IDs de documentos
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_equipodisponible, parent, false);
        return new EquipoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = listaEquipos.get(position);
        holder.textNombre.setText(equipo.getNombre());
        int miembrosActuales = (equipo.getMiembros() != null) ? equipo.getMiembros().size() : 0;
        holder.textMiembros.setText("Miembros: " + miembrosActuales + "/" + equipo.getMaxJugadores());

        // Obtener el ID del documento del equipo
        String idDocEquipo = listaIdsDocumentos.get(position);

        holder.btnVerDetalles.setOnClickListener(v -> listener.onVerDetalles(equipo));
        holder.btnUnirme.setOnClickListener(v -> listener.onUnirme(equipo, idDocEquipo));  // Pasar el idDocEquipo
    }

    @Override
    public int getItemCount() {
        return listaEquipos.size();
    }

    public static class EquipoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textMiembros;
        Button btnVerDetalles, btnUnirme;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombreEquipo);
            textMiembros = itemView.findViewById(R.id.textMiembros);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnUnirme = itemView.findViewById(R.id.btnUnirme);
        }
    }
}
