package com.monitoreopucp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class UserIncidenciasReportadasActivity extends AppCompatActivity {

    private Button ButtonVerHistorico, ButtonRefresh,ButtonAddIncidencia;
    private static final int History_Request_Code = 1;
    private static final int IncidenciasListActivityRequestCode = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tus_incidencias_reportadas);

        ButtonVerHistorico = findViewById(R.id.verHistorico);
        ButtonRefresh = findViewById(R.id.buttonRefresh);
        ButtonAddIncidencia = findViewById(R.id.buttonAdd);




        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);
        Log.i("sebastian","ID DEL USUARIO LOGEADO: "+ userId );
        if(userId != -1){
            getUserIncidenciasSinResolver(userId);
        }
        else{
            Toast.makeText(UserIncidenciasReportadasActivity.this, "Usuario no detectado", Toast.LENGTH_SHORT).show();
        }

        //Ver Historial
        ButtonVerHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserIncidenciasReportadasActivity.this, UserIncidenciasHistoryActivity.class);
                startActivityForResult(intent, History_Request_Code);
            }
        });
        //Anadir Incidencia
        ButtonAddIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserIncidenciasReportadasActivity.this, IncidenciasListActivity.class);
                startActivityForResult(intent, IncidenciasListActivityRequestCode);
            }
        });





    }

    private void getUserIncidenciasSinResolver(int userId){

        if (isInternetAvailable(this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //FALTA AÑADIR URL
            String url = "" + "?id=" + userId + "&estado=" + R.string.NoAtendido;
            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    DtoIncidencias dtoIncidencias = gson.fromJson(response, DtoIncidencias.class);
                    final Incidencia[] listaIncidencias = dtoIncidencias.getLista();
                    UserIncidenciasHistoryAdapter listaIncidenciasAdapter = new UserIncidenciasHistoryAdapter(listaIncidencias, UserIncidenciasReportadasActivity.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerView_UserIncidenciasSinResolver);
                    recyclerView.setAdapter(listaIncidenciasAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(UserIncidenciasReportadasActivity.this));
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
}
