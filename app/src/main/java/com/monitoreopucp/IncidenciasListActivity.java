package com.monitoreopucp;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.monitoreopucp.entities.Incidencia;
import com.monitoreopucp.entities.Usuario;
import com.monitoreopucp.utilities.DataListener;
import com.monitoreopucp.utilities.adapters.IncidenciasAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.monitoreopucp.utilities.Util.DISTANCIA_MAXIMA_PARA_FILTROS;
import static com.monitoreopucp.utilities.Util.getDistanceBetweenTwoPoints;

public class IncidenciasListActivity extends AppCompatActivity implements DataListener {

    private Usuario currentUser;

    //Variables
    private RecyclerView mRecyclerView;
    private IncidenciasAdapter mAdapter;
    private Incidencia[] mLista;
    private Location currentLocation;
    static private double MAX_DISTANCE = 300.0;


    //Login
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private String userUID;
    //FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentSnapshot documentIncidencia;
    private Map<String, Object> dataIncidencia;
    private ArrayList<Incidencia> collectionIncidencias = new ArrayList<Incidencia>();
    //Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Bitmap> collectionImages = new ArrayList<Bitmap>();
    private Bitmap[] mImages;
    private Bitmap currentImage;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nueva_incidencia, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuItem_NuevaIncidencia:
                Intent intent2;
                intent2 = new Intent(IncidenciasListActivity.this, IncidenciaFormulario.class);
                intent2.putExtra("currentUser", currentUser);
                intent2.putExtra("caso", 1);
                int requestCode_CrearIncidencia = 1;
                startActivityForResult(intent2, requestCode_CrearIncidencia);
                return true;
            default:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias_list);
        setTitle("Incidencias cercanas");
        currentLocation = new Location("gps");

        Intent intent = getIntent();
        currentUser = (Usuario) intent.getSerializableExtra("currentUser");
        if (currentUser != null) {
            userUID = currentUser.getId();
        }

        bootActionBar();
        //anonymousLogin();
        readIncidenciasDB();

    }

    public void bootActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerViewIncidenciasList);
        mRecyclerView.setLayoutManager(/*new LinearLayoutManager(IncidenciasListActivity.this)*/new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new IncidenciasAdapter(mLista, mImages, IncidenciasListActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new IncidenciasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Incidencia selectedIncidencia = mLista[position];

                Intent intent;
                intent = new Intent(IncidenciasListActivity.this, IncidenciaSeleccionada.class);
                intent.putExtra("item", selectedIncidencia);
                intent.putExtra("caso", 2);

                int requestCode_IncidenciaSeleccionada = 1;
                startActivityForResult(intent, requestCode_IncidenciaSeleccionada);

            }
        });
    }

    private void readIncidenciasDB() {
        getLocation();
    }

    //ASK FOR LOCATION

    private boolean checkLocationPermission() {
        return (ContextCompat.checkSelfPermission(IncidenciasListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(IncidenciasListActivity.this
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(IncidenciasListActivity.this);

        if (checkLocationPermission()){
            fusedLocationClient.getLastLocation().addOnSuccessListener(IncidenciasListActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation.setLatitude(location.getLatitude());
                        currentLocation.setLongitude(location.getLongitude());

                        db.collection("incidencias")
                                .whereEqualTo("estado", "Por atender")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    setCollectionIncidencias(task.getResult());
                                }
                            }
                        });
                    }
                }

            });
        }
        else{
            askLocationPermission();
        }

    }

    //CALCULAR DISTANCIA
    private boolean isNearLocation(GeoPoint geoPoint) {

        return(getDistanceBetweenTwoPoints(currentLocation.getLatitude(), currentLocation.getLongitude(),
                geoPoint.getLatitude(), geoPoint.getLongitude()) < DISTANCIA_MAXIMA_PARA_FILTROS);

        //double lat1 = currentLocation.getLatitude();
       //double lon1 = currentLocation.getLongitude();
        /*double lat1 = -12.4708646;
        double lon1 = -77.4790065;
        double lat2 = geoPoint.getLatitude();
        double lon2 = geoPoint.getLongitude();
        double theta = lon1 - lon2;
        double distance = Math.sin(deg2rad(lat1))*Math.sin(deg2rad(lat2))+Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance*60*1.1515*1.60934;
        return (distance < MAX_DISTANCE);*/
    }

    /*private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad *180.0 / Math.PI);
    }*/

    //DOWNLOAD IMAGES
    private void downloadImage(String imageName){

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(imageName + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(25*ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                currentImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
        addImage(currentImage);
    }

    private void addImage(Bitmap bitmap){
        collectionImages.add(bitmap);
    }

    //LOGIN
    private void anonymousLogin(){
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }

    //LISTENER

    public void setCollectionIncidencias(QuerySnapshot result) {
        Incidencia aux;
        GeoPoint aux2;
        for (QueryDocumentSnapshot document : result) {
            aux = document.toObject(Incidencia.class);
            aux2 = (GeoPoint) document.get("ubicacion");
            aux.setId(document.getId());
            if (isNearLocation(aux2)){
                aux.setUbicacion(aux2);
                collectionIncidencias.add(aux);
            }
        }
        mLista = collectionIncidencias.toArray(new Incidencia[collectionIncidencias.size()]);
        OnDataReadingFinishedListener();
    }

    @Override
    public void OnDataReadingFinishedListener() {
        //setmImagenes(mLista);
        buildRecyclerView();
    }

    public void setmImagenes(Incidencia[] mIncidencias) {
        for(Incidencia aux : mIncidencias){
            downloadImage(aux.getIdFoto());
        }
        mImages = collectionImages.toArray(new Bitmap[collectionImages.size()]);
        OnImageStorageReading();
    }

    @Override
    public void OnImageStorageReading() {
        buildRecyclerView();
    }
}
