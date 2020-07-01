package com.monitoreopucp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;
import com.monitoreopucp.utilities.DtoIncidencias;
import com.monitoreopucp.utilities.FirebaseCallback;
import com.monitoreopucp.utilities.adapters.InfraIncidenciasHistoryAdapter;
import com.monitoreopucp.utilities.adapters.UserIncidenciasHistoryAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.monitoreopucp.utilities.Util.DISTANCIA_MAXIMA_PARA_FILTROS;
import static com.monitoreopucp.utilities.Util.getDistanceBetweenTwoPoints;
import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class UserIncidenciasReportadasActivity extends AppCompatActivity {

    private Button ButtonVerHistorico, ButtonRefresh,ButtonAddIncidencia;
    private static final int History_Request_Code = 1;
    private static final int IncidenciasListActivityRequestCode = 2;

    private Usuario currentUser;
    private List<Incidencia> listaIncidenciasSinResolver;
    private RecyclerView recyclerView;

    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tus_incidencias_reportadas);

        listaIncidenciasSinResolver = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        ButtonVerHistorico = findViewById(R.id.verHistorico);
        ButtonRefresh = findViewById(R.id.buttonRefresh);
        ButtonAddIncidencia = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerView_UserIncidenciasSinResolver);

        Intent intent = getIntent();
        currentUser = (Usuario) intent.getSerializableExtra("currentUser");

        if (currentUser != null){
            String userId = currentUser.getId();

            if(!userId.equals("")){
                refreshView(userId);
            }
            else{
                Toast.makeText(UserIncidenciasReportadasActivity.this, "Usuario no detectado", Toast.LENGTH_SHORT).show();
            }
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

    public void refreshView(String userId){
        getUserIncidenciasSinResolver(userId, new FirebaseCallback() {
            @Override
            public void onSuccess() {
                UserIncidenciasHistoryAdapter listaIncidenciasAdapter = new UserIncidenciasHistoryAdapter(listaIncidenciasSinResolver,
                        UserIncidenciasReportadasActivity.this, fStorage.getReference());
                recyclerView.setAdapter(listaIncidenciasAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(UserIncidenciasReportadasActivity.this));
            }
        });
    }

    public void getUserIncidenciasSinResolver(String userId, final FirebaseCallback callback) {
        listaIncidenciasSinResolver.clear();

        fStore.collection("incidencias")
                .whereEqualTo("usuario.id", userId)
                .whereEqualTo("estado", "Por atender")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Incidencia incidencia = document.toObject(Incidencia.class);
                                incidencia.setUbicacion((GeoPoint) Objects.requireNonNull(document.get("ubicacion")));
                                listaIncidenciasSinResolver.add(incidencia);
                            }
                            callback.onSuccess();
                        } else {
                            Toast.makeText(UserIncidenciasReportadasActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
