package com.monitoreopucp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
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
    private static final int EditarIncidencia_RequestCode = 3;

    private Usuario currentUser;
    private String userId = "";
    private List<Incidencia> listaIncidenciasSinResolver;
    private RecyclerView recyclerView;

    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tus_incidencias_reportadas);
        setTitle("Mis Incidencias Reportadas");

        listaIncidenciasSinResolver = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.recyclerView_UserIncidenciasSinResolver);

        Intent intent = getIntent();
        currentUser = (Usuario) intent.getSerializableExtra("currentUser");

        if (currentUser != null){
            userId = currentUser.getId();

            if(!userId.equals("")){
                refreshView(userId);
            }
            else{
                Toast.makeText(UserIncidenciasReportadasActivity.this, "Usuario no detectado", Toast.LENGTH_SHORT).show();
            }
        }

        //Ver Historial
        findViewById(R.id.verHistorico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserIncidenciasReportadasActivity.this, UserIncidenciasHistoryActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivityForResult(intent, History_Request_Code);
            }
        });
        //Anadir Incidencia
        findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserIncidenciasReportadasActivity.this, IncidenciasListActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivityForResult(intent, IncidenciasListActivityRequestCode);
            }
        });
        //Actualizar RV
        findViewById(R.id.buttonRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userId.equals("")){
                    refreshView(userId);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_infra_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuItem_InfraMain) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent2);
            finish();
        }
        return true;
    }



        public void refreshView(final String userId){
        getUserIncidenciasSinResolver(userId, new FirebaseCallback() {
            @Override
            public void onSuccess() {
                UserIncidenciasHistoryAdapter listaIncidenciasAdapter = new UserIncidenciasHistoryAdapter(listaIncidenciasSinResolver,
                        UserIncidenciasReportadasActivity.this, fStorage.getReference());
                listaIncidenciasAdapter.setOnItemClickListener(new UserIncidenciasHistoryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Incidencia selectedIncidencia = listaIncidenciasSinResolver.get(position);
                        Intent intent;
                        intent = new Intent(UserIncidenciasReportadasActivity.this, IncidenciaSeleccionada.class);
                        intent.putExtra("item", selectedIncidencia);
                        intent.putExtra("userUID", userId);
                        intent.putExtra("currentUser", currentUser);
                        intent.putExtra("caso", 2);


                        startActivityForResult(intent, EditarIncidencia_RequestCode);
                    }
                });
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
                                incidencia.setId(document.getId());
                                incidencia.setUbicacion((GeoPoint) Objects.requireNonNull(document.get("ubicacion")));
                                listaIncidenciasSinResolver.add(incidencia);
                            }
                            callback.onSuccess();
                        } else {
                            Toast.makeText(UserIncidenciasReportadasActivity.this, "Ha ocurrido un problema", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
