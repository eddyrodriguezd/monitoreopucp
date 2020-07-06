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
    public void onBindViewHolder(final AnotacionViewHolder holder, int position) {
        final Anotacion[] anotacion = {data[position]};
        final String[] titulo = new String[1];
        String cuerpo;

        db.collection("usuarios").document(anotacion[0].getIdUsuario()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    titulo[0] = (String) document. getData().get("nombre");
                    writeOnHolder(holder,titulo[0],anotacion[0]);
                }
                else {
                    titulo[0] = anotacion[0].getIdUsuario();
                    writeOnHolder(holder,titulo[0],anotacion[0]);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public void writeOnHolder(AnotacionViewHolder holder, String title, Anotacion anotacion){
        holder.textTitulo.setText(title);
        holder.textCuerpo.setText(anotacion.getContenido());
    }

}
