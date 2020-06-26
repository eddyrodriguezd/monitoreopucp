package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.monitoreopucp.entities.Anotacion;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.utilities.adapters.AnotacionAdapter;

public class IncidenciaSeleccionada extends AppCompatActivity {

    private TextView mTextView_Titulo;
    private TextView mTextView_Cuerpo;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;

    private Incidencia itemSelected;
    private Anotacion[] listaAnotaciones;
    private AnotacionAdapter mAdapter;

    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_incidencia_seleccionada, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemMenu_AgregarAnotacion:

                Intent intent;
                intent = new Intent(IncidenciaSeleccionada.this, AgregarAnotacionActivity.class);
                intent.putExtra("titulo incidencia", itemSelected.getTitulo());

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
        setContentView(R.layout.activity_incidencia_seleccionada);

        bootActionBar();
        receiveItem();
        loadAnotaciones();
        fillFields();

    }

    public void bootActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        };
    }

    public void receiveItem() {
        Intent intent = getIntent();
        itemSelected = (Incidencia) intent.getSerializableExtra("item");
    }

    public void loadAnotaciones() {

        //db.collection("incidencias").document(itemSelected.get)


        // OBTENER LA LISTA DE INCIDENCIAS QUE COINCIDEN CON "idIncidencia"
        // listaAnotaciones = ????

    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerViewAnotaciones_IncidenciaSeleccionada);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AnotacionAdapter(listaAnotaciones, IncidenciaSeleccionada.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void fillFields() {
        mTextView_Titulo = findViewById(R.id.textViewTituloIncidencia_IncidenciaSeleccionada);
        mTextView_Cuerpo = findViewById(R.id.textViewContenidoIncidencia_IncidenciaSeleccionada);
        mImageView = findViewById(R.id.imageViewFotoIncidencia_IncidenciaSeleccionada);

        mTextView_Titulo.setText(itemSelected.getTitulo());
        mTextView_Cuerpo.setText(itemSelected.getDescripcion());
        loadImage();
        buildRecyclerView();
    }

    public void loadImage(){
        StorageReference storageRef = storage.getReference();
        final StorageReference spaceRef = storageRef.child(itemSelected.getIdFoto()+".jpg");
        spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(IncidenciaSeleccionada.this).load(uri).into(mImageView);
            }
        });
    }

}
