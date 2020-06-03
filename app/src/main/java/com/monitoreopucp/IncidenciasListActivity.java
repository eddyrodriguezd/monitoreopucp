package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.utilities.adapters.IncidenciasAdapter;

public class IncidenciasListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private IncidenciasAdapter mAdapter;
    private Incidencia[] mLista;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nueva_incidencia,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuItem_NuevaIncidencia:
                return true;
            default:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias_list);

        bootActionBar();
        buildRecyclerView();

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

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerViewIncidenciasList);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new IncidenciasAdapter(mLista, IncidenciasListActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new IncidenciasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Incidencia selectedIncidencia = mLista[position];

                Intent intent;
                intent = new Intent(IncidenciasListActivity.this, IncidenciaSeleccionada.class);
                intent.putExtra("item", selectedIncidencia);

                int requestCode_IncidenciaSeleccionada = 1;
                startActivityForResult(intent, requestCode_IncidenciaSeleccionada);

            }
        });
    }


}
