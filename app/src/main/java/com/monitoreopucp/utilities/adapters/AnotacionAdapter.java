package com.monitoreopucp.utilities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monitoreopucp.R;
import com.monitoreopucp.entities.Anotacion;

public class AnotacionAdapter extends RecyclerView.Adapter<AnotacionAdapter.AnotacionViewHolder> {

    private Anotacion[] data;
    private Context context;

    public AnotacionAdapter(Anotacion[] data, Context context) {
        this.data = data;
        this.context = context;
    }

    public static class AnotacionViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitulo;
        public TextView textCuerpo;

        public AnotacionViewHolder(View itemView) {
            super(itemView);
            this.textTitulo = itemView.findViewById(R.id.item_rv_TituloAnotacion);
            this.textCuerpo = itemView.findViewById(R.id.item_rv_CuerpoAnotacion);
        }
    }


    @NonNull
    @Override
    public AnotacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_anotacion, parent,false);
        AnotacionViewHolder anotacionViewHolder = new AnotacionViewHolder(itemView);

        return anotacionViewHolder;
    }

    @Override
    public void onBindViewHolder(AnotacionViewHolder holder, int position) {
        String titulo = String.valueOf(data[position].getIdUsuario());
        String cuerpo = String.valueOf(data[position].getContenido());

        holder.textTitulo.setText(titulo);
        holder.textCuerpo.setText(cuerpo);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

}
