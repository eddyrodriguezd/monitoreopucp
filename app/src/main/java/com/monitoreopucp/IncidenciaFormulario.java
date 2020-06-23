package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.monitoreopucp.entities.Incidencia;

public class IncidenciaFormulario extends AppCompatActivity {

    private TextView mTextView_Titulo;
    private TextView mTextview_Cuerpo;
    private ImageView mImageView;
    private ImageButton mButton_Camara;
    private ImageButton mButton_Galeria;
    private CheckBox mCheckBox;
    private Button mButton_Location;
    private Button mButton_Aceptar;

    private Incidencia mItem;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_REQUEST_CODE = 2;
    private Bitmap imageBitmap;
    private Uri selectedImage;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lugar = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_incidencia_formulario, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_formulario);

        if (checkLocationPermission() == false){
            askLocationPermission();
        }

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
        int caso = intent.getIntExtra("caso",1);


        if (caso == 1){
            tituloActv = "Crear Incidencia";
        }
        else {
            tituloActv  = "Editar Incidencia";
            Incidencia incidencia = (Incidencia) intent.getSerializableExtra("item");
            fillFields(incidencia);
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

    }

    public void fillFields (Incidencia item) {

        mTextView_Titulo.setText(item.getTitulo());
        mTextview_Cuerpo.setText(item.getDescripcion());
        // NO SE COMO VENDRA LA IMAGEN
        mCheckBox.setText(String.valueOf(item.getLatitud()) + String.valueOf(item.getLongitud()));
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
        else {
            return false;
        }

    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(IncidenciaFormulario.this
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    private void getLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(IncidenciaFormulario.this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(IncidenciaFormulario.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                String mensaje = "";
                if (location != null) {
                    // Logic to handle location object
                    lugar = location;
                    mensaje = "Se obtuvo ubicacion actual";
                    mCheckBox.setText(mensaje);
                }
                else {
                    mensaje = "No se obtuvo la ubicacion";
                }

                Toast toast = Toast.makeText(IncidenciaFormulario.this, mensaje, Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    public void onCheckboxClicked(View view) {
        getLocation();
    }

}
