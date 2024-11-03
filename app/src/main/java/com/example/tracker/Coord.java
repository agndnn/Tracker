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

public class Coord {
    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    //  LocationCallback locationCallback;

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
    Coord(Context context){
        // Создание слушателя для обработки обновлений местоположения
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // Интервал обновления местоположения в миллисекундах
                .setFastestInterval(2000); // Самый быстрый интервал обновления в миллисекундах
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
