package com.example.tracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class Old_Coord {
    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    //  LocationCallback locationCallback;
    Context context;
    private LocationListener locationListener;

    public interface LocationListener {
        void onLocationChanged(Location location);
    }

    public void setLocationListener(LocationListener listener) {
        this.locationListener = listener;
    }

    private void notifyLocationChanged(Location location) {
        if (locationListener != null) {
            locationListener.onLocationChanged(location);
        }
    }
    Old_Coord(Context context){
        // Создание слушателя для обработки обновлений местоположения
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // Интервал обновления местоположения в миллисекундах
                .setFastestInterval(2000); // Самый быстрый интервал обновления в миллисекундах
        Log.debug("Запрос координат создан.");
        locationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // if (locationResult == null) {
                // return;
                //}

                for (Location location : locationResult.getLocations()) {
                    Log.debug("location="+location);
                    // Обрабатываем полученное местоположение
                    if (location!=null) {
                        Params.latitude=location.getLatitude();
                        Params.longitude=location.getLongitude();
                        if(context instanceof MainActivity) {
                            TextView textView = ((MainActivity) context).findViewById(R.id.textView);
                            textView.setText("Широта: " + Params.latitude + " Долгота: " +Params.longitude);
                        }
                        sendCoord();
                    }
                }
            }
        };
        Log.debug("locationCallback="+locationCallback);
        startLocationUpdates();
    }
    @SuppressLint("MissingPermission")
    void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                notifyLocationChanged(location);
            }
        }
    };

    public void sendCoord(){
        if (Params.coordRequestTries>0){
            Data inputData = new Data.Builder()
                    .putString("url", Params.getAddPointUrl())
                    .build();
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(Old_HttpWorker.class)
                    .setInputData(inputData)
                    .build();
            WorkManager.getInstance(context).enqueue(workRequest);
            Log.debug("Координаты переданы");
            Params.coordRequestTries--;

        }

    }

    // Добавляем метод, который будет вызываться после получения местоположения
    public void onLocationReceived(Location location) {
        if (location != null) {
            Params.latitude = location.getLatitude();
            Params.longitude = location.getLongitude();
            // Здесь вы можете выполнить дополнительные действия с полученными координатами
            // Например, отправить их в другой сервис или обновить UI
        }
    }
}
