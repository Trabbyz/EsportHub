package com.example.esporthub_app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.esporthub_app.R;
import com.example.esporthub_app.modelos.Notificacion;

import java.util.List;

public class AdaptadorNotificaciones extends RecyclerView.Adapter<AdaptadorNotificaciones.ViewHolder> {
    private List<Notificacion> notificaciones;

    public AdaptadorNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, mensaje, fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloNoti);
            mensaje = itemView.findViewById(R.id.textMensajeNoti);
            fecha = itemView.findViewById(R.id.textFechaNoti);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notificacion noti = notificaciones.get(position);
        holder.titulo.setText(noti.getTitulo());
        holder.mensaje.setText(noti.getMensaje());
        holder.fecha.setText(noti.getFecha());
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }
}

