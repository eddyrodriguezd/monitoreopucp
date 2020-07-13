package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.monitoreopucp.entities.Anotacion;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.utilities.adapters.AnotacionAdapter;

import java.util.ArrayList;

public class InfraIncidenciaSeleccionada extends AppCompatActivity {


    private TextView mTextView_Titulo;
    private TextView mTextView_Cuerpo;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;

    private Incidencia itemSelected;
    private AnotacionAdapter mAdapter;
    private ArrayList<Anotacion> anotaciones = new ArrayList<Anotacion>();
    private Anotacion[] mAnotaciones;

    //Firebase
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private String userUID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemMenu_AgregarAnotacion:

                Intent intent;
                intent = new Intent(InfraIncidenciaSeleccionada.this, AgregarAnotacionActivity.class);
                intent.putExtra("titulo incidencia", itemSelected.getTitulo());
                intent.putExtra("incidencia", itemSelected);
                intent.putExtra("userUID", userUID);

                int requestCode_AgregarAnotacion = 2;
                startActivityForResult(intent, requestCode_AgregarAnotacion);

                return true;
            default:
                Intent intent2 = new Intent();
                setResult(RESULT_OK,intent2);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infra_incidencia_seleccionada);

        receiveItem();
        fillFields();
        loadAnotaciones();

        final Button botonMarcarResuelto = (Button) findViewById(R.id.buttonMarcarResuelto);
        if(itemSelected.getEstado().equalsIgnoreCase("Atendido")) {
            botonMarcarResuelto.setText("Marcar como no resuelto");
        }

        findViewById(R.id.ButtonBack_InfraIncidencia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        botonMarcarResuelto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(!itemSelected.getEstado().equalsIgnoreCase("Atendido")) {
                    db.collection("incidencias").document(itemSelected.getId()).update("estado", "Atendido")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(InfraIncidenciaSeleccionada.this, "Se ha marcado como atendido", Toast.LENGTH_SHORT).show();
                                    itemSelected.setEstado("Atendido");
                                    botonMarcarResuelto.setText("Marcar como no resuelto");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InfraIncidenciaSeleccionada.this, "Ocurrio un error, no se marco.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    db.collection("incidencias").document(itemSelected.getId()).update("estado", "Por Atender")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(InfraIncidenciaSeleccionada.this, "Se ha marcado como no atendido", Toast.LENGTH_SHORT).show();
                                    itemSelected.setEstado("Por atender");
                                    botonMarcarResuelto.setText("Marcar como resuelto");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InfraIncidenciaSeleccionada.this, "Ocurrio un error.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }


    public void receiveItem() {
        Intent intent = getIntent();
        itemSelected = (Incidencia) intent.getSerializableExtra("item");
        userUID = intent.getStringExtra("userUID");
    }

    public void loadAnotaciones() {

        db.collection("incidencias").document(itemSelected.getId())
                .collection("anotaciones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    setAnotaciones(task.getResult());
                }
            }
        });

    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerViewAnotaciones_IncidenciaSeleccionada);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(InfraIncidenciaSeleccionada.this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AnotacionAdapter(mAnotaciones, InfraIncidenciaSeleccionada.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void fillFields() {
        mTextView_Titulo = findViewById(R.id.textViewTituloIncidencia_IncidenciaSeleccionada);
        mTextView_Cuerpo = findViewById(R.id.textViewContenidoIncidencia_IncidenciaSeleccionada);
        mImageView = findViewById(R.id.imageViewFotoIncidencia_IncidenciaSeleccionada);

        mTextView_Titulo.setText(itemSelected.getTitulo());
        mTextView_Cuerpo.setText(itemSelected.getDescripcion());
        loadImage();
    }

    public void loadImage(){
        StorageReference storageRef = storage.getReference();
        final StorageReference spaceRef = storageRef.child(itemSelected.getIdFoto()+".jpg");
        spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(InfraIncidenciaSeleccionada.this).load(uri).into(mImageView);
            }
        });
    }


    //Listener
    public void setAnotaciones(QuerySnapshot result) {
        Anotacion aux;
        for(QueryDocumentSnapshot documentSnapshot : result){
            aux = documentSnapshot.toObject(Anotacion.class);
            if (documentSnapshot != null){
                anotaciones.add(aux);
            }
        }
        mAnotaciones = anotaciones.toArray(new Anotacion[anotaciones.size()]);
        buildRecyclerView();
    }
}