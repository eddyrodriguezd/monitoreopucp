package com.monitoreopucp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.utilities.DtoIncidencias;
import com.monitoreopucp.utilities.adapters.UserIncidenciasHistoryAdapter;

import java.util.Map;

import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class InfraIncidenciasHistoryActivity extends AppCompatActivity {

    private static final int FILTER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infra_incidencias_history);

        getAllIncidenciasHistory();

        findViewById(R.id.buttonBack_InfraHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.button_FilterHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfraIncidenciasHistoryActivity.this, FilterActivity.class);
                startActivityForResult(intent, FILTER_REQUEST_CODE);
            }
        });
    }

    private void getAllIncidenciasHistory() {

        if (isInternetAvailable(this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //FALTA AÑADIR URL
            String url = "";
            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    DtoIncidencias dtoIncidencias = gson.fromJson(response, DtoIncidencias.class);
                    final Incidencia[] listaAllIncidencias = dtoIncidencias.getLista();

                    UserIncidenciasHistoryAdapter listaAllIncidenciasAdapter = new UserIncidenciasHistoryAdapter(listaAllIncidencias, InfraIncidenciasHistoryActivity.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerView_InfraHistory);
                    recyclerView.setAdapter(listaAllIncidenciasAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(InfraIncidenciasHistoryActivity.this));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILTER_REQUEST_CODE){

            if (resultCode == RESULT_OK) {

                //Refrescar la lista con los filtros seleccionados
            }

        }
    }
}
