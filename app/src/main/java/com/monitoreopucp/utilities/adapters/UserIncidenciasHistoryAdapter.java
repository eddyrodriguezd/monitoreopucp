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

public class UserIncidenciasHistoryAdapter extends RecyclerView.Adapter<UserIncidenciasHistoryAdapter.UserIncidenciaHistoryViewHolder> {

    private Incidencia[] listaIncidencias;
    private Context context;

    public UserIncidenciasHistoryAdapter(Incidencia[] listaIncidencias, Context c){
        this.listaIncidencias = listaIncidencias;
        this.context = c;
    }

   public static class UserIncidenciaHistoryViewHolder extends RecyclerView.ViewHolder{

       private TextView textViewSingleUserIncidencia_TitleValue;
       private TextView textViewSingleUserIncidencia_RegisterDateValue;
       private TextView textViewSingleUserIncidencia_StatusValue;
       private TextView textViewSingleUserIncidenciaCheckDate;
       private TextView textViewSingleUserIncidencia_CheckDateValue;
       private ImageView imageViewSingleUserIncidencia;

       public UserIncidenciaHistoryViewHolder(@NonNull View itemView) {
           super(itemView);
           textViewSingleUserIncidencia_TitleValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_TitleValue);
           textViewSingleUserIncidencia_RegisterDateValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_RegisterDateValue);
           textViewSingleUserIncidencia_StatusValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_StatusValue);
           textViewSingleUserIncidenciaCheckDate = itemView.findViewById(R.id.textViewSingleUserIncidenciaCheckDate);
           textViewSingleUserIncidencia_CheckDateValue = itemView.findViewById(R.id.textViewSingleUserIncidencia_CheckDateValue);
           imageViewSingleUserIncidencia = itemView.findViewById(R.id.imageViewSingleUserIncidencia);
       }
   }

    @NonNull
    @Override
    public UserIncidenciaHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.single_incidencia_user_history, parent, false);
        return new UserIncidenciaHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserIncidenciasHistoryAdapter.UserIncidenciaHistoryViewHolder holder, int position) {
        Incidencia incidencia = listaIncidencias[position];
        holder.textViewSingleUserIncidencia_TitleValue.setText(incidencia.getTitulo());
        holder.textViewSingleUserIncidencia_RegisterDateValue.setText(incidencia.getFechaRegistro().toString());
        holder.textViewSingleUserIncidencia_StatusValue.setText(incidencia.getEstado());

        if(incidencia.getEstado().equals(R.string.Atendido)){
            holder.textViewSingleUserIncidencia_CheckDateValue.setText(incidencia.getFechaRevision().toString());
        }
        else{
            holder.textViewSingleUserIncidenciaCheckDate.setVisibility(View.GONE);
            holder.textViewSingleUserIncidencia_CheckDateValue.setVisibility(View.GONE);
        }

        //FALTA LO DE LA IMAGEN
    }

    @Override
    public int getItemCount() {
        return listaIncidencias.length;
    }
}
