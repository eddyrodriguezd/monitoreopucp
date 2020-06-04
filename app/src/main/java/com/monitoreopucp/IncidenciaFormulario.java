package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_incidencia_formulario, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_formulario);



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

    public void setForm(int caso) {

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

        if (caso == 1){
            tituloActv = "Crear Incidencia";
        }
        else {
            tituloActv  = "Editar Incidencia";
            Incidencia incidencia = (Incidencia) intent.getSerializableExtra("item");
            fillFields(incidencia);
        }

        this.setTitle(tituloActv);
    }

    public void fillFields (Incidencia item) {

        mTextView_Titulo.setText(item.getTitulo());
        mTextview_Cuerpo.setText(item.getDescripcion());
        // NO SE COMO PONER LA IMAGEN, NI COMO VENDRA;
        mButton_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mButton_Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
