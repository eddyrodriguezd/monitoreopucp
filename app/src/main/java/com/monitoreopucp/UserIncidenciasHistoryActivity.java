package com.monitoreopucp;

import androidx.annotation.NonNull;
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
import com.monitoreopucp.utilities.adapters.UserIncidenciasHistoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.monitoreopucp.utilities.Util.isInternetAvailable;

public class UserIncidenciasHistoryActivity extends AppCompatActivity {

    private Usuario currentUser;
    private List<Incidencia> listaIncidenciasResueltas;
    private RecyclerView recyclerView;

    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_incidencias_history);

        listaIncidenciasResueltas = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        recyclerView = findViewById(R.id.recyclerView_UserHistory);

        Intent intent = getIntent();
        currentUser = (Usuario) intent.getSerializableExtra("currentUser");

        if (currentUser != null){
            String userId = currentUser.getId();

            if(!userId.equals("")){
                refreshView(userId);
            }
            else{
                Toast.makeText(UserIncidenciasHistoryActivity.this, "Usuario no detectado", Toast.LENGTH_SHORT).show();
            }
        }

        findViewById(R.id.buttonBack_UserHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void refreshView(String userId){
        getUserIncidenciasHistory(userId, new FirebaseCallback() {
            @Override
            public void onSuccess() {
                UserIncidenciasHistoryAdapter listaIncidenciasAdapter = new UserIncidenciasHistoryAdapter(listaIncidenciasResueltas,
                        UserIncidenciasHistoryActivity.this, fStorage.getReference());
                recyclerView.setAdapter(listaIncidenciasAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(UserIncidenciasHistoryActivity.this));
            }

        });
    }

    public void getUserIncidenciasHistory(String userId, final FirebaseCallback callback) {
        listaIncidenciasResueltas.clear();

        fStore.collection("incidencias")
                .whereEqualTo("usuario.id", userId)
                .whereEqualTo("estado", "Atendido")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Incidencia incidencia = document.toObject(Incidencia.class);
                                incidencia.setUbicacion((GeoPoint) Objects.requireNonNull(document.get("ubicacion")));
                                listaIncidenciasResueltas.add(incidencia);
                            }
                            callback.onSuccess();
                        } else {
                            Toast.makeText(UserIncidenciasHistoryActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
