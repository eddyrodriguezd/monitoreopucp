package com.monitoreopucp.utilities.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.monitoreopucp.R;
import com.monitoreopucp.entities.Incidencia;

import java.util.Date;
import java.util.List;

import static com.monitoreopucp.utilities.Util.formatDate;

public class UserIncidenciasHistoryAdapter extends RecyclerView.Adapter<UserIncidenciasHistoryAdapter.UserIncidenciaHistoryViewHolder> {

    private List<Incidencia> listaIncidencias;
    private Context context;
    private StorageReference storageReference;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(UserIncidenciasHistoryAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }


    public UserIncidenciasHistoryAdapter(List<Incidencia> listaIncidencias, Context c, StorageReference storageReference){
        this.listaIncidencias = listaIncidencias;
        this.context = c;
        this.storageReference = storageReference;
    }

   public static class UserIncidenciaHistoryViewHolder extends RecyclerView.ViewHolder{

       private TextView textViewSingleUserIncidencia_TitleValue;
       private TextView textViewSingleUserIncidencia_RegisterDateValue;
       private TextView textViewSingleUserIncidencia_StatusValue;
       private TextView textViewSingleUserIncidenciaCheckDate;
       private TextView textViewSingleUserIncidencia_CheckDateValue;
       private ImageView imageViewSingleUserIncidencia;

       public UserIncidenciaHistoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
           super(itemView);
           textViewSingleUserIncidencia_TitleValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_TitleValue);
           textViewSingleUserIncidencia_RegisterDateValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_RegisterDateValue);
           textViewSingleUserIncidencia_StatusValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_StatusValue);
           textViewSingleUserIncidenciaCheckDate = itemView.findViewById(R.id.textViewSingleUserIncidenciaCheckDate);
           textViewSingleUserIncidencia_CheckDateValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_CheckDateValue);
           imageViewSingleUserIncidencia = itemView.findViewById(R.id.imageViewSingleUserIncidencia);

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

    @NonNull
    @Override
    public UserIncidenciaHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.single_incidencia_user_history, parent, false);
        return new UserIncidenciaHistoryViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserIncidenciaHistoryViewHolder holder, int position) {
        Log.i("Sebastian", listaIncidencias.toString());
        Incidencia incidencia = listaIncidencias.get(position);
        holder.textViewSingleUserIncidencia_TitleValue.setText(incidencia.getTitulo());
        holder.textViewSingleUserIncidencia_RegisterDateValue.setText(formatDate(incidencia.getFechaRegistro()));
        holder.textViewSingleUserIncidencia_StatusValue.setText(incidencia.getEstado());

        getIncidenciaImage(incidencia.getIdFoto() + ".jpg", holder);

        if(incidencia.getEstado().equals("Atendido")){
            holder.textViewSingleUserIncidencia_CheckDateValue.setText(formatDate(incidencia.getFechaRevision()));
        }
        else{
            holder.textViewSingleUserIncidenciaCheckDate.setVisibility(View.GONE);
            holder.textViewSingleUserIncidencia_CheckDateValue.setVisibility(View.GONE);
        }
    }

    public void getIncidenciaImage(final String photoName, final UserIncidenciaHistoryViewHolder holder){
        storageReference.child(photoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.imageViewSingleUserIncidencia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaIncidencias.size();
    }
}
