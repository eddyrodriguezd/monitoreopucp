package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IncidenciaFormulario extends AppCompatActivity {

    private EditText mTextView_Titulo;
    private EditText mTextview_Cuerpo;
    private ImageView mImageView;
    private ImageButton mButton_Camara;
    private ImageButton mButton_Galeria;
    private CheckBox mCheckBox;
    private Button mButton_Location;
    private Button mButton_Aceptar;

    private Usuario currentUser;

    private Incidencia mItem;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_REQUEST_CODE = 2;
    static final int MAP_FILTER_REQUEST_CODE = 3;
    private Bitmap imageBitmap;
    private Uri selectedImage;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lugar = null;
    private GeoPoint geoPoint;
    private boolean locationFromMap = false;
    private int CASE = 1;
    private String incidenciaUID;

    //Firebase
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private StorageReference mountainsRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idFoto;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_incidencia_formulario, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_formulario);

        if (!checkLocationPermission()){
            askLocationPermission();
        }

        Intent intent = getIntent();
        currentUser = (Usuario) intent.getSerializableExtra("currentUser");

        bootActionBar();
        setForm();

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

    public void setForm() {

        mTextView_Titulo = findViewById(R.id.textViewTituloIncidenica_FormularioIncidencia);
        mTextview_Cuerpo = findViewById(R.id.textViewContenidoIncidencia_FormularioIncidencia);
        mImageView = findViewById(R.id.imageFotoIncidencia_FormularioIncidencia);
        mButton_Camara = findViewById(R.id.buttonTomarFoto_FormularioIncidencia);
        mButton_Galeria = findViewById(R.id.buttonGaleria_FornularioIncidencia);
        mCheckBox = findViewById(R.id.checkBox_FormularioIncidencia);
        mButton_Location = findViewById(R.id.buttonIngresarMapa_FormularioIncidencia);
        mButton_Aceptar = findViewById(R.id.buttonAceptar_FormularioIncidencia);

        // "caso" es el "requestCode", para saber si venimos a crear o editar incidencias
        // 1 -> crear
        // 2 -> editar

        String tituloActv;
        Intent intent = getIntent();
        CASE = intent.getIntExtra("caso",1);

        if (CASE == 1){
            tituloActv = "Crear Incidencia";
        }
        else {
            tituloActv  = "Editar Incidencia";
            mItem = (Incidencia) intent.getSerializableExtra("item");
            currentUser = (Usuario) intent.getSerializableExtra("currentUser");
            fillFields(mItem);
        }

        this.setTitle(tituloActv);

        mButton_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mButton_Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        mButton_Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInfo();
            }
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    getLocation();
                    locationFromMap = false;
                }
            }
        });
        mButton_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncidenciaFormulario.this, MapFilterActivity.class);
                startActivityForResult(intent, MAP_FILTER_REQUEST_CODE);
            }
        });

    }

    public void fillFields (Incidencia item) {

        downloadImage(item.getIdFoto());
        mTextView_Titulo.setText(item.getTitulo());
        mTextview_Cuerpo.setText(item.getDescripcion());
        String infoLocation = String.valueOf(item.getLatitud()) + " " + String.valueOf(item.getLongitud());
        mCheckBox.setText(infoLocation);
        mCheckBox.setChecked(false);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(imageBitmap);
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    mImageView.setImageURI(selectedImage);
                case MAP_FILTER_REQUEST_CODE:
                    if (data != null) {
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        geoPoint = new GeoPoint(latitude, longitude);
                        locationFromMap = true;
                    }
            }
        }
    }

    private void pickFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    private boolean checkLocationPermission(){

        if (ContextCompat.checkSelfPermission(IncidenciaFormulario.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(IncidenciaFormulario.this
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    private void getLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(IncidenciaFormulario.this);

        if (checkLocationPermission()){
            fusedLocationClient.getLastLocation().addOnSuccessListener(IncidenciaFormulario.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String mensaje = "";
                    if (location != null) {
                        mensaje = "Ubicación capturada";
                        lugar = location;
                    }
                    else {
                        mensaje = "No se pudo obtener la ubicación";
                    }
                    Toast.makeText(IncidenciaFormulario.this, mensaje,Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void onCheckboxClicked(View view) {
        getLocation();
    }

    //Firebase Actions
    private void uploadImage(final String idFoto){
        storageRef = storage.getReference();
        mountainsRef = storageRef.child(idFoto + ".jpg");
        // Get the data from an ImageView as bytes
        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(IncidenciaFormulario.this,"Fail",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Map<String,Object> infoFoto = new HashMap<>();
                infoFoto.put("idFoto", idFoto);
                db.collection("incidencias").document(idFoto)
                        .update(infoFoto)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(IncidenciaFormulario.this,"Success",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        });
            }
        });
    }

    private void uploadInfo(){

        Map<String, Object> infoUsuario = new HashMap<>();
        infoUsuario.put("id", currentUser.getId());
        infoUsuario.put("codigo", currentUser.getCodigo());
        infoUsuario.put("nombre", currentUser.getNombre() + " " + currentUser.getApellido());

        Map<String, Object> incidencia = new HashMap<>();
        incidencia.put("descripcion", mTextview_Cuerpo.getText().toString());
        incidencia.put("estado", "Por atender");
        //incidencia.put("idFoto", idFoto);
        incidencia.put("titulo",mTextView_Titulo.getText().toString());
        incidencia.put("usuario", infoUsuario);

        if (CASE == 1){
            Date currentTime = Calendar.getInstance().getTime();
            incidencia.put("fechaRegistro",new Timestamp(currentTime));
            GeoPoint geo;
            if (locationFromMap){
                geo = geoPoint;
            }
            else {
                geo = new GeoPoint(lugar.getLatitude(),lugar.getLongitude());
            }

            incidencia.put("ubicacion", geo);

            db.collection("incidencias")
                    .add(incidencia)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            idFoto = documentReference.getId();
                            uploadImage(idFoto);
                        }
                    });
        }
        else {
            db.collection("incidencias").document(String.valueOf(mItem.getId()))
                    .set(incidencia)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            idFoto = mItem.getId();
                            uploadImage(idFoto);
                        }
                    });
        }

    }

    private void downloadImage(String idFoto){
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child(idFoto + ".jpg");
        spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(IncidenciaFormulario.this).load(uri).into(mImageView);
            }
        });
    }

}
