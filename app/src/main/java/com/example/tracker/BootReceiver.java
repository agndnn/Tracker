package com.example.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.getParams();
            if (Params.IsAutoRun.equals("Y")) {
                // Запускаем LocationService после перезагрузки
                Intent serviceIntent = new Intent(context, TrackerService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}