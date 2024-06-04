package com.example.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
//       Intent serviceLauncher = new Intent(context, CodeDirService.class);
//       context.startService(serviceLauncher);
            if (Params.IsAutoRun.equals("Y")) {
                context.startService(new Intent(context, CallInterceptorService.class));
            }
        }
    }
}