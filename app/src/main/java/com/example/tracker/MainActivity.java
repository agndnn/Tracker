package com.example.tracker;

import static com.example.tracker.Params.ReceiverIntent;
import static com.example.tracker.Params.usersOut;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
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

@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
public class MainActivity extends AppCompatActivity {

    Context context;
    private static boolean isMapKitInitialized = false;
    DatabaseHelper databaseHelper;
    private TextView textView;

    String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getParams();
        context = this;

        if (!isMapKitInitialized) {
            MapKitFactory.setApiKey(Params.getApiKey());
            MapKitFactory.initialize(this);
            Params.mapkit = MapKitFactory.getInstance();
            isMapKitInitialized = true;
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissions, 200);
        }


        Intent serviceLocationIntent = new Intent(this, LocationService.class);
        startService(serviceLocationIntent);
        Log.debug("Запущен сервис геолокации.");

        ReceiverIntent = new Intent(this, CallReceiver.class);
        startService(ReceiverIntent);

        //Intent BootIntent = new Intent(this, BootReceiver.class);
        //startActivity(BootIntent);

        Log.debug("Запущен листsенер звонка.");

        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button1);
        textView = findViewById(R.id.textView);
        Button buttonTest = findViewById(R.id.buttonTest);
        Button buttonCallOut = findViewById(R.id.buttonCallOut);
        Button buttonParams = findViewById(R.id.buttonParams);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });


        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Идет определение координат...");
                LocationHelper locationHelper = new LocationHelper(context);
                locationHelper.getLocation(new LocationHelper.OnLocationReceivedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        Log.debug("Received Location: Latitude: " + latitude + ", Longitude: " + longitude);
                        textView.setText("Широта: " + Params.latitude + " Долгота: " + Params.longitude);

                        // Здесь вы можете использовать полученные координаты
                        locationHelper.stopLocationUpdates();
                    }
                });
            }
        });

        buttonCallOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCallOutActivity();
            }
        });

        buttonParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openParamsActivity();
            }
        });
    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
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
        if (!(requestCode == 200 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(MainActivity.this, "Не все разрешения предоставлены.", Toast.LENGTH_SHORT).show();
            Log.debug("разрешения не предоставлены");
            finishAffinity();
        }
    }
}
