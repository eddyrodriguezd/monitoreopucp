package com.monitoreopucp.utilities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.monitoreopucp.R;
import com.monitoreopucp.entities.Anotacion;
import com.monitoreopucp.entities.Usuario;

public class AnotacionAdapter extends RecyclerView.Adapter<AnotacionAdapter.AnotacionViewHolder> {

    private Anotacion[] data;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        final Anotacion[] anotacion = {data[position]};
        final String[] titulo = new String[1];
        String cuerpo;

        db.collection("usuarios").document(data[position].getIdUsuario()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Usuario usuario = document.toObject(Usuario.class);
                    titulo[0] = usuario.getNombre() + ", " + usuario.getApellido();
                }
                else {
                    titulo[0] = String.valueOf(anotacion[0].getIdUsuario());
                }
            }
        });
        cuerpo = String.valueOf(anotacion[0].getContenido());

        holder.textTitulo.setText(titulo[0]);
        holder.textCuerpo.setText(cuerpo);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

}
