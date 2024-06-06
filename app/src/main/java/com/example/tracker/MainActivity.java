package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button buttonSave;
    private TextView textView;
    private FusedLocationProviderClient fusedLocationClient;
    //private MapView mapView;
    protected static final String par_latitude = "latitude";
    protected static final String par_longitude = "longitude";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

//    private double latitude;
//    private double longitude;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String[] permissions = {
                           Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.READ_PHONE_STATE,
                          Manifest.permission.CALL_PHONE,
                           Manifest.permission.RECEIVE_BOOT_COMPLETED
    }
            ;
    int requestCode = 123; // Любое число, которое вы выберете для
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("a4304081-4a62-4707-9204-65de6edc6562");
        MapKitFactory.initialize(this);
// Запуск обновлений местоположения

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 123);
        }

        /*
        //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        //   getCurrentLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 4);
        }

 */

        //       MapKitFactory.setApiKey("a4304081-4a62-4707-9204-65de6edc6562");
 //       MapKitFactory.initialize(this);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // Интервал обновления местоположения в миллисекундах
                .setFastestInterval(2000); // Самый быстрый интервал обновления в миллисекундах

       // mapView = findViewById(R.id.mapView);
        button = findViewById(R.id.button1);
        textView = findViewById(R.id.textView);
        buttonSave=findViewById(R.id.buttonSave);

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
                        //String jsonBody = "{\"code\":\"ag234678\",\"is_log\":\"1\",\"lat\":\""
                       //         +latitude+"\",\"lon\":\""+longitude+"\"}";
                        String urlString = "https://site-www.ru/maptrack/add_point.php?code=ag234678&is_log=1&lat="+ Params.latitude +"&lon="+Params.latitude;
                       // textView.setText(urlString);
                        String response = HttpClient.sendGetRequest(urlString);
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

// Создание слушателя для обработки обновлений местоположения
           locationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                 // if (locationResult == null) {
                   // return;
                //}

                for (Location location : locationResult.getLocations()) {
                    // Обрабатываем полученное местоположение
                    if (location!=null) {
                        Params.latitude=location.getLatitude();
                        Params.longitude=location.getLongitude();

                       textView.setText("Широта: " + Params.latitude + " Долгота: " +Params.longitude);
                    }
                }
            }
        };


        startLocationUpdates();

        Intent serviceIntent = new Intent(this, CallReceiver.class);
        startService(serviceIntent);

        /*
//        serviceIntent.putExtra(par_latitude, latitude); // Здесь передавайте значение первого параметра
 //       serviceIntent.putExtra(par_longitude, longitude); // Здесь передавайте значение второго параметра
        startService(serviceIntent);

*/
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
 //       intent.putExtra(par_latitude, latitude); // Здесь передавайте значение первого параметра
 //       intent.putExtra(par_longitude, longitude); // Здесь передавайте значение второго параметра
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
            startLocationUpdates();
        //}
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
}
