package com.example.esporthub_app.adaptadores;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Equipo;
import com.example.esporthub_app.modelos.Jugador;
import com.example.esporthub_app.vistas.equipos.Pantalla_VerDetallesEquipo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorEquipos extends RecyclerView.Adapter<AdaptadorEquipos.EquipoViewHolder> {

    private List<Equipo> listaEquipos;
    private List<String> listaIdsDocumentos; // nueva lista para IDs

    public AdaptadorEquipos(List<Equipo> listaEquipos, List<String> listaIdsDocumentos) {
        this.listaEquipos = listaEquipos;
        this.listaIdsDocumentos = listaIdsDocumentos;
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

        holder.btnVerDetalles.setOnClickListener(v -> {
            // Obtén el equipo en la posición actual
            int pos = holder.getAdapterPosition();
            String idDoc = listaIdsDocumentos.get(pos);  // Este es el ID del documento del equipo

            // Crear la intención para pasar a la pantalla de detalles
            Intent intent = new Intent(holder.itemView.getContext(), Pantalla_VerDetallesEquipo.class);

            // Pasar el ID del documento del equipo
            intent.putExtra("idEquipo", idDoc);

            // Iniciar la actividad
            holder.itemView.getContext().startActivity(intent);
        });

        holder.btnAbandonar.setOnClickListener(v -> {
            String uidActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
            int pos = holder.getAdapterPosition();

            String idDoc = listaIdsDocumentos.get(pos);

            // Eliminar del array de IDs
            equipo.getIdMiembros().remove(uidActual);

            // Eliminar del array de objetos Jugador
            List<Jugador> nuevosMiembros = new ArrayList<>();
            for (Jugador j : equipo.getMiembros()) {
                if (!j.getUid().equals(uidActual)) {
                    nuevosMiembros.add(j);
                }
            }
            equipo.setMiembros(nuevosMiembros);

            // Actualizar el equipo en Firestore
            FirebaseFirestore.getInstance()
                    .collection("equipos")
                    .document(idDoc)
                    .update(
                            "idMiembros", equipo.getIdMiembros(),
                            "miembros", equipo.getMiembros()
                    )
                    .addOnSuccessListener(unused -> {
                        // También actualizar el campo "equipoActual" del jugador
                        FirebaseFirestore.getInstance()
                                .collection("jugadores")
                                .document(uidActual)
                                .update("equipoActual", "")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(holder.itemView.getContext(), "Has abandonado el equipo", Toast.LENGTH_SHORT).show();
                                    listaEquipos.remove(pos);
                                    listaIdsDocumentos.remove(pos);
                                    notifyItemRemoved(pos);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al actualizar equipoActual del jugador", e);
                                    Toast.makeText(holder.itemView.getContext(), "Error al actualizar el jugador", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al abandonar el equipo", e);
                        Toast.makeText(holder.itemView.getContext(), "Error al abandonar el equipo", Toast.LENGTH_SHORT).show();
                    });
        });


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

