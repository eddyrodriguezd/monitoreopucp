package com.monitoreopucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.monitoreopucp.entities.Usuario;
import com.monitoreopucp.utilities.FirebaseCallback;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fStore;

    /*private EditText editTextMail;
    private EditText editTextPassword;*/

    private TextInputLayout textInputLayoutMail;
    private TextInputLayout textInputLayoutPassword;

    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_usuario);

        /*editTextMail = findViewById(R.id.editTextMail);
        editTextPassword = findViewById(R.id.editTextPassword);*/

        textInputLayoutMail = findViewById(R.id.editTextMail);
        textInputLayoutPassword = findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        if(user != null){
            if(user.isEmailVerified())
                signIn();
        }

        findViewById(R.id.buttonAcceder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextMail = textInputLayoutMail.getEditText().getText().toString();
                String editTextPassword = textInputLayoutPassword.getEditText().getText().toString();

                if (editTextMail.equals("") || editTextPassword.equals("")) {
                    Toast.makeText(LoginActivity.this, "Por favor rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.signInWithEmailAndPassword(editTextMail, editTextPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                user= mAuth.getCurrentUser();
                                if (user.isEmailVerified())
                                    signIn();
                                else
                                    Toast.makeText(LoginActivity.this, "Por favor verifique su correo", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Credenciales erróneas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterUser.class));
            }
        });

    }

    public void signIn(){
        getUserData(new FirebaseCallback() {
            @Override
            public void onSuccess() {

                if(currentUser.isInfra()){
                    Intent intent = new Intent(getApplicationContext(), InfraIncidenciasHistoryActivity.class);
                    intent.putExtra("currentUser", currentUser);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), UserIncidenciasReportadasActivity.class);
                    intent.putExtra("currentUser", currentUser);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    public void getUserData(final FirebaseCallback callback){
        try{
            DocumentReference documentReference = fStore.collection("usuarios").document(mAuth.getCurrentUser().getUid());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot!= null){
                        currentUser = documentSnapshot.toObject(Usuario.class);
                        currentUser.setId(mAuth.getCurrentUser().getUid());
                        callback.onSuccess();
                    }

                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, "No se encuentra el usuario", Toast.LENGTH_SHORT).show();
        }

    }
}
