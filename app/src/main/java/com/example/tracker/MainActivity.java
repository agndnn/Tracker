package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.MapKitFactory;


import android.content.pm.PackageManager;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private Button button;
    private Button buttonSave;
    private Button buttonCallOut;
    private Button buttonParams;
    private Coord coord;
    private TextView textView;
    //private MapView mapView;
   // protected static final String par_latitude = "latitude";
    //protected static final String par_longitude = "longitude";
    //private static final int REQUEST_LOCATION_PERMISSION = 1;

//    private double latitude;
//    private double longitude;
    String[] permissions = {
                           Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.READ_PHONE_STATE,
                           Manifest.permission.CALL_PHONE,
                           Manifest.permission.RECEIVE_BOOT_COMPLETED,
                           Manifest.permission.READ_PHONE_NUMBERS,
                           Manifest.permission.READ_CALL_LOG,
                           Manifest.permission.READ_CONTACTS
    };
    int requestCode = 123; // Любое число, которое вы выберете для
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getParams();
        MapKitFactory.setApiKey(Params.getApiKey());
        MapKitFactory.initialize(this);

// Запуск обновлений местоположения

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissions, 123);
        }

         coord =new Coord(this);

        setContentView(R.layout.activity_main);
       // mapView = findViewById(R.id.mapView);
        button = findViewById(R.id.button1);
        textView = findViewById(R.id.textView);
        buttonSave=findViewById(R.id.buttonSave);
        buttonCallOut=findViewById(R.id.buttonCallOut);
        buttonParams=findViewById(R.id.buttonParams);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   textView.setText("добрый");
                //getCurrentLocation();
                openMapActivity();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(() -> {
                    try {
                        String response = HttpClient.sendGetRequest(Params.getAddPointUrl());
                        runOnUiThread(() -> {
                            textView.setText("response="+response );
                            // Обновите UI с полученными данными
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        buttonCallOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   textView.setText("добрый");
                //getCurrentLocation();
                openCallOutActivity();
            }
        });

        buttonParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openParamsActivity();
            }
        });

        Intent serviceIntent = new Intent(this, CallReceiver.class);
        startService(serviceIntent);


    //    startForegroundService(serviceIntent);
    }
/*
    @SuppressLint({"SetTextI18n", "MissingPermission"})
    private void getCurrentLocation() {
    //    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Местоположение найдено, обрабатываем
                            textView.setText("Широта1: " + location.getLatitude()+ " Долгота1: " + location.getLongitude());
                        }
                    });

  //      } else {
     //       requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
     //   }

    }
*/
    private void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("key_latitude", Params.latitude);
        intent.putExtra("key_longitude", Params.longitude);
        startActivity(intent);
    }

    private void openCallOutActivity() {
        CallLogsManager callLogsManager = new CallLogsManager(this);
        List<CallLogsManager.CallLogEntry> callLogs = callLogsManager.getOutgoingCalls(Params.usersOut);
        Intent intent = new Intent(this, CallOutActivity.class);
        intent.putExtra(CallOutActivity.EXTRA_CALL_LOGS, (Serializable) callLogs);
        startActivity(intent);
    }

    private void openParamsActivity() {
        Log.debug("openParamsActivity started");
        Intent intent = new Intent(this, ParamsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Разрешение предоставлено, выполняем действия с местоположением
                    //handleLocationPermissionGranted();
                } else {
                    // Разрешение не предоставлено
                    //handleLocationPermissionDenied();
                    Toast.makeText(MainActivity.this, "Не все разрешения предоставлены.", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //if (requestingLocationUpdates) {
            coord.startLocationUpdates();
        //}
    }

}
