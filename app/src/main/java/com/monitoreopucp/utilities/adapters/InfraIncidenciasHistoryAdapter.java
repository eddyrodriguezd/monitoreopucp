package com.monitoreopucp.utilities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.monitoreopucp.R;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;

import java.util.Map;

import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class InfraIncidenciasHistoryAdapter extends RecyclerView.Adapter<InfraIncidenciasHistoryAdapter.InfraIncidenciaHistoryViewHolder> {
    Incidencia[] listaIncidencias;
    Context context;

    public InfraIncidenciasHistoryAdapter(Incidencia[] listaIncidencias, Context c){
        this.listaIncidencias = listaIncidencias;
        this.context = c;
    }

    public static class InfraIncidenciaHistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewSingleInfraIncidencia_TitleValue;
        private TextView textViewSingleInfraIncidencia_UserValue;
        private TextView textViewSingleInfraIncidencia_RegisterDateValue;
        private TextView textViewSingleInfraIncidencia_StatusValue;
        private TextView textViewSingleInfraIncidencia_CheckDateValue;
        private TextView textViewSingleInfraIncidencia_AnotacionesValue;
        private TextView textViewSingleInfraIncidencia_Anotaciones;
        private ImageView imageViewInfraIncidencia;

        public InfraIncidenciaHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSingleInfraIncidencia_TitleValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_TitleValue);
            textViewSingleInfraIncidencia_UserValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_UserValue);
            textViewSingleInfraIncidencia_RegisterDateValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_RegisterDateValue);
            textViewSingleInfraIncidencia_StatusValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_StatusValue);
            textViewSingleInfraIncidencia_CheckDateValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_CheckDateValue);
            textViewSingleInfraIncidencia_AnotacionesValue = itemView.findViewById(R.id.textViewSingleInfraIncidencia_AnotacionesValue);
            textViewSingleInfraIncidencia_Anotaciones = itemView.findViewById(R.id.textViewSingleInfraIncidencia_Anotaciones);
            imageViewInfraIncidencia = itemView.findViewById(R.id.imageViewInfraIncidencia);
        }
    }

    @NonNull
    @Override
    public InfraIncidenciaHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.single_incidencia_infra_history, parent, false);
        return new InfraIncidenciasHistoryAdapter.InfraIncidenciaHistoryViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InfraIncidenciasHistoryAdapter.InfraIncidenciaHistoryViewHolder holder, int position) {
        Incidencia incidencia = listaIncidencias[position];
        holder.textViewSingleInfraIncidencia_TitleValue.setText(incidencia.getTitulo());

        Usuario usuario = getUsuarioById(incidencia.getIdUsuario());
        if(usuario!= null){
            holder.textViewSingleInfraIncidencia_UserValue.setText(usuario.getNombre() + " " + usuario.getApellido());
        }

        holder.textViewSingleInfraIncidencia_RegisterDateValue.setText(incidencia.getFechaRegistro().toString());
        holder.textViewSingleInfraIncidencia_StatusValue.setText(incidencia.getEstado());
        holder.textViewSingleInfraIncidencia_CheckDateValue.setText(incidencia.getFechaRevision().toString());

        if(incidencia.getAnotaciones().size()>0){
            holder.textViewSingleInfraIncidencia_AnotacionesValue.setText(incidencia.getAnotaciones().size());
        }
        else{
            holder.textViewSingleInfraIncidencia_Anotaciones.setVisibility(View.GONE);
            holder.textViewSingleInfraIncidencia_AnotacionesValue.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public Usuario getUsuarioById(int userId){
        final Usuario[] usuario = {null};

        if (isInternetAvailable(context)) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            //FALTA AÑADIR URL
            String url = "" + "?id=" + userId;
            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    usuario[0] = gson.fromJson(response, Usuario.class);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    //DE SER NECESARIO (Como el Api-key en el lab)
                    return null;
                }
            };
            requestQueue.add(stringRequest);
        }

        return usuario[0];
    }


}
