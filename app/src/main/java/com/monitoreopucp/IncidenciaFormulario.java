package com.monitoreopucp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class IncidenciaFormulario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_formulario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}