package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Torneo;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdaptadorTorneos extends RecyclerView.Adapter<AdaptadorTorneos.TorneoViewHolder> {

    private List<Torneo> torneoList;
    private OnAbandonarTorneoListener listener;


    public AdaptadorTorneos(List<Torneo> torneoList, OnAbandonarTorneoListener listener) {
        this.torneoList = torneoList;
        this.listener = listener;
    }


    public interface OnAbandonarTorneoListener {
        void onVerDetalles(Torneo torneo);
        void onAbandonar(Torneo torneo, String idDocTorneo);
    }

    @NonNull
    @Override
    public TorneoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_torneo, parent, false);
        return new TorneoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TorneoViewHolder holder, int position) {
        Torneo torneo = torneoList.get(position);
        holder.tvNombre.setText(torneo.getNombre());
        holder.tvDescripcion.setText(torneo.getDescripcion());


        holder.btnVerDetalles.setOnClickListener(view -> {
            if (listener != null) {
                listener.onVerDetalles(torneo);
            }
        });


        holder.btnAbandonarTorneo.setOnClickListener(v -> {
            if (listener != null) {
                String idDocTorneo = torneo.getIdTorneo();
                listener.onAbandonar(torneo, idDocTorneo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return torneoList.size();
    }

    public static class TorneoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvDescripcion;
        MaterialButton btnVerDetalles, btnAbandonarTorneo;

        public TorneoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.textTorneoNombre);
            tvDescripcion = itemView.findViewById(R.id.textTorneoDescripcion);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnAbandonarTorneo = itemView.findViewById(R.id.btnAbandonarTorneo);
        }
    }
}


