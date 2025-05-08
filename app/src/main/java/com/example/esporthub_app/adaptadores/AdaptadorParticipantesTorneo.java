package com.example.esporthub_app.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;

import java.util.List;

public class AdaptadorParticipantesTorneo extends RecyclerView.Adapter<AdaptadorParticipantesTorneo.ViewHolder> {

    private final List<Equipo> listaParticipantes;
    private final Context context;

    public AdaptadorParticipantesTorneo(List<Equipo> listaParticipantes, Context context) {
        this.listaParticipantes = listaParticipantes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_participante_torneo, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipo equipo = listaParticipantes.get(position);
        holder.nombreEquipo.setText(equipo.getNombre());
        holder.cantidadJugadores.setText("Jugadores: " + equipo.getIdMiembros().size());


        if (equipo.getMiembros() != null && !equipo.getMiembros().isEmpty()) {
            StringBuilder miembros = new StringBuilder();
            for (Jugador jugador : equipo.getMiembros()) {
                miembros.append("- ").append(jugador.getNombre()).append("\n");
            }
            holder.listaMiembros.setText(miembros.toString());
        } else {
            holder.listaMiembros.setText("Miembros no disponibles");
        }
    }

    @Override
    public int getItemCount() {
        return listaParticipantes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreEquipo, cantidadJugadores, listaMiembros;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreEquipo = itemView.findViewById(R.id.txtNombreEquipo);
            cantidadJugadores = itemView.findViewById(R.id.txtCantidadJugadores);
            listaMiembros = itemView.findViewById(R.id.txtListaMiembros);
        }
    }
}

