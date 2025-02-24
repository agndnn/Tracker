package com.example.tracker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {
    private static final String CHANNEL_ID = "LocationServiceChannel";
    //private FusedLocationProviderClient fusedLocationClient;
   // private LocationRequest locationRequest;
   // private LocationCallback locationCallback;
    //private Context context;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        Log.d("LocationService", "LocationService srated.");
        super.onCreate();
        createNotificationChannel(); // Создание канала уведомлений для API 26+
        startForeground(1, getNotification()); // Запуск сервиса в фоновом режиме
        //context = getApplicationContext();

        /*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // Интервал обновления местоположения в миллисекундах
                .setFastestInterval(1000); // Самый быстрый интервал обновления в миллисекундах

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("LocationService", "Location: " + location);
                        Params.latitude = location.getLatitude();
                        Params.longitude = location.getLongitude();
                        if(context instanceof MainActivity) {
                            TextView textView = ((MainActivity) context).findViewById(R.id.textView);
                            textView.setText("Широта: " + Params.latitude + " Долгота: " +Params.longitude);
                        }
  //                      sendCoord();

                    }
                }
            }
        };
        startLocationUpdates();

         */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Перезапуск сервиса при низком уровне памяти
    }

    /*
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

*/
    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracker")
                .setContentText("Tracker is active")
                .setSmallIcon(R.drawable.ic_launcher_background) // Убедитесь, что у вас есть подходящий иконка
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
     //   fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Мы не используем биндинг
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Tracker Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
