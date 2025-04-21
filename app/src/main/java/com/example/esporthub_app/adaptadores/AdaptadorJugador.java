package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Usuario;

import java.util.List;

public class AdaptadorJugador extends RecyclerView.Adapter<AdaptadorJugador.ViewHolder> {

    public interface AccionJugadorListener {
        void verPerfil(Usuario jugador);
        void eliminarJugador(Usuario jugador);
    }

    private List<Usuario> lista;
    private AccionJugadorListener listener;

    public AdaptadorJugador(List<Usuario> lista, AccionJugadorListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtEmail;
        Button btnVerPerfil, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreJugador);
            txtEmail = itemView.findViewById(R.id.txtEmailJugador);
            btnVerPerfil = itemView.findViewById(R.id.btnVerPerfil);
            btnEliminar = itemView.findViewById(R.id.btnEliminarJugador);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jugador, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario jugador = lista.get(position);
        holder.txtNombre.setText(jugador.getNombre());
        holder.txtEmail.setText(jugador.getEmail());

        holder.btnVerPerfil.setOnClickListener(v -> listener.verPerfil(jugador));
        holder.btnEliminar.setOnClickListener(v -> listener.eliminarJugador(jugador));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}

