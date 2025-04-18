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
import com.example.esporthub_app.modelos.Torneo;

import java.util.List;

public class AdaptadorTorneosDisponibles extends RecyclerView.Adapter<AdaptadorTorneosDisponibles.TorneoViewHolder> {
    private List<Torneo> listaTorneos;
    private Context context;
    private OnTorneoClickListener listener;

    public interface OnTorneoClickListener {
        void onVerDetalles(Torneo torneo);
        void onUnirme(Torneo torneo);
    }

    public AdaptadorTorneosDisponibles(List<Torneo> listaTorneos, Context context, OnTorneoClickListener listener) {
        this.listaTorneos = listaTorneos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TorneoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_torneodisponible, parent, false);
        return new TorneoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TorneoViewHolder holder, int position) {
        Torneo torneo = listaTorneos.get(position);
        holder.textNombre.setText(torneo.getNombre());

        int numParticipantes = (torneo.getParticipantes() != null) ? torneo.getParticipantes().size() : 0;
        holder.textParticipantes.setText("Participantes: " + numParticipantes);

        holder.btnVerDetalles.setOnClickListener(v -> listener.onVerDetalles(torneo));
        holder.btnUnirme.setOnClickListener(v -> listener.onUnirme(torneo));
    }

    @Override
    public int getItemCount() {
        return listaTorneos.size();
    }

    public static class TorneoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textParticipantes;
        Button btnVerDetalles, btnUnirme;

        public TorneoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombreTorneo);
            textParticipantes = itemView.findViewById(R.id.textParticipantes);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetallesTorneo);
            btnUnirme = itemView.findViewById(R.id.btnUnirmeTorneo);
        }
    }
}



