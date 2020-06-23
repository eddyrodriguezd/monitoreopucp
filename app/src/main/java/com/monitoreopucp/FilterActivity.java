package com.monitoreopucp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    private static final int MAP_FILTER_REQUEST_CODE = 1;

    private LatLng location;
    private TextView textViewFilterDateFrom, textViewFilterDateTo, editTextFilterKeywords;
    private CheckBox checkBoxAtendidoFilter, checkBoxPorAtenderFilter;
    private DatePickerDialog datePicker;
    private boolean datePickerNro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        textViewFilterDateFrom = findViewById(R.id.textViewFilterDateFrom);
        textViewFilterDateTo = findViewById(R.id.textViewFilterDateTo);
        editTextFilterKeywords = findViewById(R.id.editTextFilterKeywords);
        checkBoxAtendidoFilter = findViewById(R.id.checkBoxAtendidoFilter);
        checkBoxPorAtenderFilter = findViewById(R.id.checkBoxPorAtenderFilter);

        final Calendar newCalendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth);
                if(datePickerNro){
                    textViewFilterDateFrom.setText(new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es", "PE")).format(c.getTime()));
                }
                else{
                    textViewFilterDateTo.setText(new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es", "PE")).format(c.getTime()));
                }

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //Location (map) Filter
        findViewById(R.id.buttonFilterMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, MapFilterActivity.class);
                startActivityForResult(intent, MAP_FILTER_REQUEST_CODE);
            }
        });

        //Date Filter
        findViewById(R.id.imageButtonFromCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerNro = true;
                datePicker.show();
            }
        });
        findViewById(R.id.imageButtonToCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerNro = false;
                datePicker.show();
            }
        });
        findViewById(R.id.buttonToToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Send to previous activity (confirm)
        findViewById(R.id.buttonConfirmAllFilters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkBoxAtendidoFilter.isChecked() && !checkBoxPorAtenderFilter.isChecked()) {
                    Toast.makeText(FilterActivity.this, "Debe seleccionar al menos uno de los estados",
                            Toast.LENGTH_SHORT).show();
                } else {

                    if(!textViewFilterDateFrom.getText().toString().equals(getResources().getString(R.string.From)) &&
                            !textViewFilterDateTo.getText().toString().equals(getResources().getString(R.string.To)) ){

                        Date fromDate=null, toDate=null;

                        DateFormat format = new SimpleDateFormat("EEEE dd-MM-yyyy", new Locale("es", "PE"));
                        try {
                            fromDate = format.parse(textViewFilterDateFrom.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            toDate = format.parse(textViewFilterDateTo.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(toDate!= null && fromDate != null){
                            Log.d("Problem", "To:" + toDate.toString());
                            Log.d("Problem", "From:" + fromDate.toString());
                            if (toDate.compareTo(fromDate) >= 0){
                                SendDataAndFinishActivity();
                            }
                            else{
                                Toast.makeText(FilterActivity.this, "Por favor seleccione un rango de fechas válido",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else{
                        SendDataAndFinishActivity();
                    }
                }


            }
        });

        findViewById(R.id.buttonBackFromFilterMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void SendDataAndFinishActivity(){
        Intent returnIntent = new Intent();

        //Location (map) Filter
        if (location != null) {
            returnIntent.putExtra("latitude", location.latitude);
            returnIntent.putExtra("longitude", location.longitude);
        }

        //Date Filter
        if(!textViewFilterDateFrom.getText().toString().equals(getResources().getString(R.string.From)) &&
                !textViewFilterDateTo.getText().toString().equals(getResources().getString(R.string.To)) ){
            returnIntent.putExtra("fromDate", textViewFilterDateFrom.getText().toString());
            returnIntent.putExtra("toDate", textViewFilterDateTo.getText().toString());
        }

        //Content Filter
        if (!editTextFilterKeywords.getText().toString().equals("")) {
            returnIntent.putExtra("keywords", editTextFilterKeywords.getText().toString());
        }

        //Status Filter
        int status = (!checkBoxAtendidoFilter.isChecked()) ? 0 //Solo por atender
                : (!checkBoxPorAtenderFilter.isChecked()) ? 1 //Solo atendidas
                : 2; //Por atender y atendidas
        returnIntent.putExtra("status", status);

        setResult(FilterActivity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_FILTER_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                if (data != null) {
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);

                    if (latitude != 0 && longitude != 0)
                        location = new LatLng(latitude, longitude);
                }

            }

        }
    }

}
