package com.monitoreopucp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.monitoreopucp.entities.Anotacion;

public class AgregarAnotacionActivity extends AppCompatActivity {

    private TextView mTextView_TituloIncidencia;
    private EditText mEditText_Anotacion;
    private Button mButton_Aceptar;
    private Button mButton_Cancelar;

    private String tituloIncidencia;
    private Anotacion NuevaAnotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_anotacion);

        mTextView_TituloIncidencia = findViewById(R.id.textViewTituloincidencia_AgregarAnotacion);
        mEditText_Anotacion = findViewById(R.id.inputNuevaAnotacion);
        mButton_Aceptar = findViewById(R.id.buttonAceptar_NuevaAnotacion);
        mButton_Cancelar = findViewById(R.id.buttonCancelar_NuevaAnotacion);

        mTextView_TituloIncidencia.append(receiveData());

        mButton_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mButton_Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cuerpo_anotacion = mEditText_Anotacion.getText().toString();
                NuevaAnotacion = new Anotacion(1231, cuerpo_anotacion);

                Intent intent = new Intent();
                intent.putExtra("nueva anotacion", NuevaAnotacion);
                setResult(RESULT_OK, intent);
                finish();

            }
        });



    }

    public String receiveData() {
        Intent intent = getIntent();
        String tituloIncidencia = "error: incidencia no encontrada";
        if(intent.getStringExtra("titulo incidencia") != null){
            tituloIncidencia = intent.getStringExtra("titulo incidencia");
        }
        return tituloIncidencia;
    }




}
