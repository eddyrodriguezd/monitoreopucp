package com.monitoreopucp.utilities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.monitoreopucp.R;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.monitoreopucp.utilities.Util.formatDate;
import static com.monitoreopucp.utilities.Util.get00Time;
import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class InfraIncidenciasHistoryAdapter extends RecyclerView.Adapter<InfraIncidenciasHistoryAdapter.InfraIncidenciaHistoryViewHolder> {
    private List<Incidencia> listaIncidencias;
    private Context context;
    private StorageReference storageReference;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public InfraIncidenciasHistoryAdapter(List<Incidencia> listaIncidencias, Context c, StorageReference storageReference){
        this.listaIncidencias = listaIncidencias;
        this.context = c;
        this.storageReference = storageReference;
    }

    public static class InfraIncidenciaHistoryViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout singleInfraIncidencia_ConstraintLayout;
        private TextView textViewSingleInfraIncidencia_TitleValue;
        private TextView textViewSingleInfraIncidencia_UserValue;
        private TextView textViewSingleInfraIncidencia_RegisterDateValue;
        private TextView textViewSingleInfraIncidencia_StatusValue;
        private TextView textViewSingleInfraIncidencia_CheckDate;
        private TextView textViewSingleInfraIncidencia_CheckDateValue;
        private TextView textViewSingleInfraIncidencia_AnotacionesValue;
        private TextView textViewSingleInfraIncidencia_Anotaciones;
        private ImageView imageViewInfraIncidencia;

        public InfraIncidenciaHistoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            singleInfraIncidencia_ConstraintLayout = itemView.findViewById(R.id.singleInfraIncidencia_ConstraintLayout);
            textViewSingleInfraIncidencia_TitleValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_TitleValue);
            textViewSingleInfraIncidencia_UserValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_UserValue);
            textViewSingleInfraIncidencia_RegisterDateValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_RegisterDateValue);
            textViewSingleInfraIncidencia_StatusValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_StatusValue);
            textViewSingleInfraIncidencia_CheckDate = itemView.findViewById(R.id.textViewSingleInfraIncidencia_CheckDate);
            textViewSingleInfraIncidencia_CheckDateValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_CheckDateValue);
            textViewSingleInfraIncidencia_AnotacionesValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_AnotacionesValue);
            textViewSingleInfraIncidencia_Anotaciones = itemView.findViewById(R.id.textViewSingleInfraIncidencia_Anotaciones);
            imageViewInfraIncidencia = itemView.findViewById(R.id.imageViewInfraIncidencia);

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
    public InfraIncidenciaHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.single_incidencia_infra_history, parent, false);
        return new InfraIncidenciasHistoryAdapter.InfraIncidenciaHistoryViewHolder(itemView,mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InfraIncidenciaHistoryViewHolder holder, int position) {
        Incidencia incidencia = listaIncidencias.get(position);
        holder.textViewSingleInfraIncidencia_TitleValue.setText(incidencia.getTitulo());

        holder.textViewSingleInfraIncidencia_UserValue.setText(incidencia.getUsuario().get("nombre"));

        Date fechaRegistro = incidencia.getFechaRegistro();
        holder.textViewSingleInfraIncidencia_RegisterDateValue.setText(formatDate(fechaRegistro));

        Date initialDateTime = get00Time(Calendar.getInstance().getTime(),0);
        Date endDateTime = get00Time(Calendar.getInstance().getTime(),1);
        Log.d("InfoFecha", "inicio: " + initialDateTime.toString());
        Log.d("InfoFecha", "fin: " + endDateTime.toString());
        Log.d("InfoFecha", "fechaRegistro: " + fechaRegistro.toString());

        if( initialDateTime.equals(fechaRegistro) ||
                (initialDateTime.before(fechaRegistro) && endDateTime.after(fechaRegistro)) ){
            Log.d("InfoFecha", "ENTRÓ Titulo:" + incidencia.getTitulo());
        }
        else{
        }

        holder.textViewSingleInfraIncidencia_StatusValue.setText(incidencia.getEstado());

        getIncidenciaImage(incidencia.getIdFoto() + ".jpg", holder);

        if(incidencia.getFechaRevision()!= null){
            holder.textViewSingleInfraIncidencia_CheckDateValue.setText(formatDate(incidencia.getFechaRevision()));
        }
        else{
            holder.textViewSingleInfraIncidencia_CheckDate.setVisibility(View.GONE);
            holder.textViewSingleInfraIncidencia_CheckDateValue.setVisibility(View.GONE);
        }

        if(!incidencia.getAnotaciones().isEmpty()){
            holder.textViewSingleInfraIncidencia_AnotacionesValue.setText(incidencia.getAnotaciones().size());
        }
        else{
            holder.textViewSingleInfraIncidencia_Anotaciones.setVisibility(View.GONE);
            holder.textViewSingleInfraIncidencia_AnotacionesValue.setVisibility(View.GONE);
        }

    }

    public void getIncidenciaImage(final String photoName, final InfraIncidenciaHistoryViewHolder holder){
        storageReference.child(photoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.imageViewInfraIncidencia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaIncidencias.size();
    }
}
