package com.monitoreopucp;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.StructuredQuery;
import com.google.gson.Gson;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;
import com.monitoreopucp.utilities.DtoIncidencias;
import com.monitoreopucp.utilities.FirebaseCallback;
import com.monitoreopucp.utilities.adapters.IncidenciasAdapter;
import com.monitoreopucp.utilities.adapters.InfraIncidenciasHistoryAdapter;
import com.monitoreopucp.utilities.adapters.UserIncidenciasHistoryAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.monitoreopucp.utilities.Util.DISTANCIA_MAXIMA_PARA_FILTROS;
import static com.monitoreopucp.utilities.Util.getDistanceBetweenTwoPoints;
import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class InfraIncidenciasHistoryActivity extends AppCompatActivity {

    private static final int FILTER_REQUEST_CODE = 1;

    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private List<Incidencia> listaIncidencias;
    private RecyclerView recyclerView;
    private Usuario currentUser;
    private LatLng location;
    private Date fromDate, toDate;
    private String keywords;
    private int status=2; //Por defecto se muestran "atendidos" y "por atender" (ambos)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infra_incidencias_history);
        Intent intent1 = getIntent();
        currentUser = (Usuario) intent1.getSerializableExtra("currentUser");
        listaIncidencias = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.recyclerView_InfraHistory);
        refreshView();

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

    /*private void getAllIncidenciasHistory() {

        if (isInternetAvailable(this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, getFullUrl(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    DtoIncidencias dtoIncidencias = gson.fromJson(response, DtoIncidencias.class);
                    final Incidencia[] listaAllIncidencias = dtoIncidencias.getLista();


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
    }*/

    /*private String getFullUrl(){
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
    }*/


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

                    refreshView();
                }

            }

        }
    }

    public void refreshView(){
        getIncidenciasHistory(new FirebaseCallback() {
            @Override
            public void onSuccess() {
                InfraIncidenciasHistoryAdapter InfraIncidenciasHistoryAdapter = new InfraIncidenciasHistoryAdapter(listaIncidencias,
                        InfraIncidenciasHistoryActivity.this, fStorage.getReference());
                recyclerView.setAdapter(InfraIncidenciasHistoryAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(InfraIncidenciasHistoryActivity.this));
                InfraIncidenciasHistoryAdapter.setOnItemClickListener(new InfraIncidenciasHistoryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        Incidencia selectedIncidencia = listaIncidencias.get(position);

                        Intent intent;
                        intent = new Intent(InfraIncidenciasHistoryActivity.this, InfraIncidenciaSeleccionada.class);
                        intent.putExtra("item", selectedIncidencia);
                        intent.putExtra("caso", 2);

                        int requestCode_IncidenciaSeleccionada = 1;
                        startActivityForResult(intent, requestCode_IncidenciaSeleccionada);

                    }
                });
            }
        });
    }

    public void getIncidenciasHistory(final FirebaseCallback callback) {
        if (isInternetAvailable(this)) {
            listaIncidencias.clear();
            CollectionReference collection = fStore.collection("incidencias");
            Task<QuerySnapshot> querySnapshot = null;

            if(fromDate == null){ //No considerar fecha
                switch (status){
                    case 0: //Solo "por atender"
                        querySnapshot = collection.whereEqualTo("estado", "Por atender").get();
                        Log.d("infoApp", "Filtro: Solo por atender");
                        break;
                    case 1: //Solo "atendidos"
                        querySnapshot = collection.whereEqualTo("estado", "Atendido").get();
                        Log.d("infoApp", "Filtro: Solo atendidos");
                        break;
                    case 2: //Ambos
                        querySnapshot = collection.get();
                        Log.d("infoApp", "SIN Filtro");
                        break;
                }
            }
            else{ //Considerar fecha
                switch (status){

                    case 0: //Solo "por atender"
                        querySnapshot = collection.whereGreaterThanOrEqualTo("fechaRegistro", fromDate)
                                .whereLessThanOrEqualTo("fechaRegistro", toDate)
                                .whereEqualTo("estado", "Por atender").get();
                        break;
                    case 1: //Solo "atendidos"
                        querySnapshot = collection.whereGreaterThanOrEqualTo("fechaRegistro", fromDate)
                                .whereLessThanOrEqualTo("fechaRegistro", toDate)
                                .whereEqualTo("estado", "Atendido").get();
                        break;
                    case 2: //Ambos
                        querySnapshot = collection.whereGreaterThanOrEqualTo("fechaRegistro", fromDate)
                                .whereLessThanOrEqualTo("fechaRegistro", toDate)
                                .get();
                        break;
                }
            }

            querySnapshot.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Incidencia incidencia = document.toObject(Incidencia.class);
                            incidencia.setUbicacion((GeoPoint) Objects.requireNonNull(document.get("ubicacion")));
                            incidencia.setId(document.getId());
                            //Filtro por palabras clave
                            if(keywords != null){
                                if(!incidencia.getDescripcion().toLowerCase().contains(keywords.toLowerCase()))
                                    continue;
                            }
                            //Filtro por ubicación
                            if (location != null){
                                Log.d("infoLocation", "Selected: " + location.latitude + ", " + location.longitude);
                                Log.d("infoLocation", "Incidencia: " + incidencia.getLatitud() + ", " + incidencia.getLongitud());
                                Log.d("infoLocation", "distance:" + getDistanceBetweenTwoPoints(location.latitude, location.longitude,
                                        incidencia.getLatitud(), incidencia.getLongitud()));
                                if(getDistanceBetweenTwoPoints(location.latitude, location.longitude,
                                        incidencia.getLatitud(), incidencia.getLongitud()) > DISTANCIA_MAXIMA_PARA_FILTROS)
                                    continue;
                            }
                            listaIncidencias.add(incidencia);
                        }
                        callback.onSuccess();
                    } else {
                        Toast.makeText(InfraIncidenciasHistoryActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }



}


