package com.monitoreopucp.utilities.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.monitoreopucp.R;
import com.monitoreopucp.entities.Incidencia;

import java.util.ArrayList;

public class IncidenciasAdapter extends RecyclerView.Adapter<IncidenciasAdapter.IncidenciaViewHolder> {

    private Incidencia[] data;
    private Bitmap[] fotos;
    private Context context;
    private OnItemClickListener mListener;
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }



    public IncidenciasAdapter(Incidencia[] data, Bitmap[] fotos, Context context) {
        this.data = data;
        this.fotos = fotos;
        this.context = context;
    }

    @Override
    public IncidenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_incidencia,parent,false);
        IncidenciaViewHolder incidenciaViewHolder = new IncidenciaViewHolder(itemView, mListener);

        return incidenciaViewHolder;
    }

    @Override
    public void onBindViewHolder(final IncidenciaViewHolder holder, int position) {

        String Titulo = data[position].getTitulo();
        //Bitmap Foto = fotos[position];
        // Create a storage reference from our app

        StorageReference storageRef = storage.getReference();
        final StorageReference spaceRef = storageRef.child(data[position].getIdFoto()+".jpg");
        spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imageView);
            }
        });

        holder.textView.setText(Titulo);
        //holder.imageView.setImageBitmap(Foto);

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
