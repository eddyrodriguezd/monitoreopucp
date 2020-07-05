package com.monitoreopucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.proto.TargetOrBuilder;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private EditText editNombre, editApellido, editCorreo, editCodigo, editPassword;
    private Button buttonRegistrar, buttonCancelar;

    private String nombre;
    private String apellido;
    private String correo;
    private String codigo;
    private String password;

    final private String[] domainsList = {"@pucp.pe", "@pucp.edu.pe"};
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;
    private String userUID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        setForm();
    }

    private void setForm(){

        editNombre = findViewById(R.id.editText15);
        editApellido = findViewById(R.id.editText16);
        editCorreo = findViewById(R.id.editText17);
        editCodigo = findViewById(R.id.editText18);
        editPassword = findViewById(R.id.editText19);
        buttonRegistrar = findViewById(R.id.button7);
        buttonCancelar = findViewById(R.id.button8);

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataValid()){
                    createUser();
                }
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    private boolean isDataValid(){

        boolean emptyForm;
        nombre = editNombre.getText().toString();
        apellido = editApellido.getText().toString();
        correo = editCorreo.getText().toString();
        codigo = editCodigo.getText().toString();
        password = editPassword.getText().toString();

        emptyForm = nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || codigo.isEmpty() || password.isEmpty();

        if (emptyForm){
            Toast.makeText(this,"Rellene todos los campos", Toast.LENGTH_LONG).show();
        }
        else {
            if (isMailValid()){
                return true;
            }
            else{
                Toast.makeText(this,"Dirección de correo inválido", Toast.LENGTH_LONG).show();
            }
        }

        return false;
    }

    private boolean isMailValid(){
        boolean validUser = false;

        for (String domain : domainsList){
            if (correo.length() > domain.length()) {
                if (correo.indexOf('@') == (correo.length()-domain.length())){
                    validUser = true;
                    break;
                }
            }
        }

        return validUser;
    }

    private void createUser(){
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            userUID = mUser.getUid();
                            createUserDocument();
                        }
                        else {
                            Toast.makeText(RegisterUser.this,
                                    "Error: no se pudo crear el usuario", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationMail() {
        mUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(RegisterUser.this,"Por favor revise la bandeja de entrada" +
                                    "de su correo: " + correo, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    }
                });
    }

    private void createUserDocument() {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("apellido", apellido);
        usuario.put("codigo", codigo);
        usuario.put("correo", correo);
        usuario.put("infra", false);
        usuario.put("nombre", nombre);

        db.collection("usuarios").document(userUID)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendVerificationMail();
                    }
                });
    }

}