package com.monitoreopucp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFilterActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng location;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_filter);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.buttonConfirmLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent  = new Intent();
                returnIntent.putExtra("latitude", location.latitude);
                returnIntent.putExtra("longitude", location.longitude);
                setResult(MapFilterActivity.RESULT_OK, returnIntent);
                finish();
            }
        });

        findViewById(R.id.buttonBackFromMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        location = new LatLng(-12.069512,-77.0815479);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f));

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                location = MapFilterActivity.this.googleMap.getCameraPosition().target;
            }
        });
    }
}
