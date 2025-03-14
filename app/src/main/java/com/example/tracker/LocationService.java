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

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        Log.d("LocationService", "LocationService srated.");
        super.onCreate();

        createNotificationChannel(); // Создание канала уведомлений для API 26+
        startForeground(1, getNotification()); // Запуск сервиса в фоновом режиме
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Перезапуск сервиса при низком уровне памяти
    }


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
