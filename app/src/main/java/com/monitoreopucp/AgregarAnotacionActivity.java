package com.monitoreopucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.monitoreopucp.entities.Anotacion;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;

public class AgregarAnotacionActivity extends AppCompatActivity {

    private TextView mTextView_TituloIncidencia;
    private EditText mEditText_Anotacion;
    private Button mButton_Aceptar;
    private Button mButton_Cancelar;
    private Usuario usuario;
    private String tituloIncidencia;
    private Anotacion nuevaAnotacion;
    private String UID;
    private Incidencia incidencia;

    //Firebase

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_anotacion);
        incidencia = (Incidencia) getIntent().getSerializableExtra("incidencia");
        UID = (String) getIntent().getSerializableExtra("userUID");

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
                nuevaAnotacion = new Anotacion("UID", cuerpo_anotacion);
                if (!nuevaAnotacion.getContenido().isEmpty()) {

                    upload(nuevaAnotacion, incidencia);
                    Intent intent = new Intent();
                    intent.putExtra("nueva anotacion", nuevaAnotacion);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(AgregarAnotacionActivity.this, "Por favor llene la anotacion", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(AgregarAnotacionActivity.this, "Nueva anotacion creada", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String receiveData() {
        Intent intent = getIntent();
        String tituloIncidencia = "error: incidencia no encontrada";
        if (intent.getStringExtra("titulo incidencia") != null) {
            tituloIncidencia = intent.getStringExtra("titulo incidencia");
        }
        return tituloIncidencia;
    }


    public void upload(Anotacion anotacion, Incidencia incidencia) {
        db.collection("incidencias").document(incidencia.getId()).collection("anotaciones")
                .add(anotacion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AgregarAnotacionActivity.this, "Se agrego la anotacion", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarAnotacionActivity.this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
