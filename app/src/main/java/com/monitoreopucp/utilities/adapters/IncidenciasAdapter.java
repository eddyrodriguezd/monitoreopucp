package com.monitoreopucp.utilities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monitoreopucp.R;
import com.monitoreopucp.entities.Incidencia;

public class IncidenciasAdapter extends RecyclerView.Adapter<IncidenciasAdapter.IncidenciaViewHolder> {

    private Incidencia[] data;
    private Context context;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }



    public IncidenciasAdapter(Incidencia[] data, Context context) {
        this.data = data;
        this.context = context;
    }


    @Override
    public IncidenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_incidencia,parent,false);
        IncidenciaViewHolder incidenciaViewHolder = new IncidenciaViewHolder(itemView, mListener);

        return incidenciaViewHolder;
    }

    @Override
    public void onBindViewHolder(IncidenciaViewHolder holder, int position) {

        String Titulo = data[position].getTitulo();
        int IdFoto = data[position].getIdFoto();

        //Obtener idRsc a partir del IdFoto;
        int idRsc = 0;

        holder.textView.setText(Titulo);
        holder.imageView.setImageResource(idRsc);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class IncidenciaViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ImageView imageView;

        public IncidenciaViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.item_rv_TituloIncidencia);
            this.imageView = itemView.findViewById(R.id.item_rv_ImagenIncidencia);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }



}
