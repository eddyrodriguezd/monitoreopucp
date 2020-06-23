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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.utilities.DtoIncidencias;
import com.monitoreopucp.utilities.adapters.UserIncidenciasHistoryAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class InfraIncidenciasHistoryActivity extends AppCompatActivity {

    private static final int FILTER_REQUEST_CODE = 1;

    private LatLng location;
    private Date fromDate, toDate;
    private String keywords;
    private int status=2; //Por defecto se muestran "atendidos" y "por atender" (ambos)

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

            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, getFullUrl(), new Response.Listener<String>() {
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

    private String getFullUrl(){
        //FALTA AÑADIR URL
        String url = "...";
        if (location != null){
            url+= "?latitude=" +  location.latitude + "&longitude= " + location.longitude; //Añadir parámetro get
        }
        if (fromDate != null && toDate != null){
            url+= "?startDate=" + fromDate.toString() + "&toDate=" + toDate.toString(); //Añadir parámetro get
        }
        if (keywords != null){
            Log.d("Problem", "keyword!=null");
            if (!keywords.equals(""))
                Log.d("Problem", "keyword!=' '");
            url+= "?keywords=" + keywords; //Añadir parámetro get
        }

        switch (status){
            case 0: //Solo "por atender"
                url+= "?status=" + status + "Solo por atender"; //Añadir parámetro get
                break;
            case 1: //Solo "atendidos"
                url+= "?status=" + status + "Solo atendidos"; //Añadir parámetro get
                break;
            case 2: //Ambos
                url+= "?status=" + status + "Ambos"; //Añadir parámetro get
                break;
        }
        Toast.makeText(InfraIncidenciasHistoryActivity.this, url, Toast.LENGTH_SHORT).show();
        return url;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILTER_REQUEST_CODE){

            if (resultCode == RESULT_OK) {

                if(data!= null){

                    //Location
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    if(latitude!= 0 && longitude!=0){
                        location = new LatLng(latitude, longitude);
                    }

                    //Date
                    DateFormat format = new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es", "PE"));
                    String fromDateString = data.getStringExtra("fromDate");
                    if(fromDateString!= null){
                        try {
                            fromDate = format.parse(fromDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    String toDateString = data.getStringExtra("toDate");
                    if(toDateString!= null){
                        try {
                            toDate = format.parse(toDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    //Content
                    keywords = data.getStringExtra("keywords");

                    //Status
                    status = data.getIntExtra("status", -1);

                    getFullUrl();
                }

            }

        }
    }
}
