package com.example.tracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class CallInterceptorService extends Service {
    private static final Notification CHANNEL_ID = null;
    private TelephonyManager telephonyManager;
    //private MyPhoneStateListener phoneStateListener;
  //  double latitude;
  //  double longitude;
    @Override
    public void onCreate() {
        super.onCreate();
      //  Toast.makeText(getApplicationContext(), "OnCreate=", Toast.LENGTH_SHORT).show();
        //telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //phoneStateListener = new MyPhoneStateListener();

    }
/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Params.latitude = intent.getDoubleExtra(MainActivity.par_latitude,0);
       // Params.longitude = intent.getDoubleExtra(MainActivity.par_longitude,0);
   //     Toast.makeText(getApplicationContext(), "latitude11="+Params.latitude, Toast.LENGTH_SHORT).show();
  //      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
  //          startForegroundService(intent);
  //      } else {
            startService(intent);
  //      }
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }
*/
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "onStartCommandstart", Toast.LENGTH_SHORT).show();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener callStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state,incomingNumber);
                Context context = getApplicationContext();
                Toast.makeText(context, incomingNumber+ " TelephonyManager.CALL_STATE_RINGING: "+state, Toast.LENGTH_LONG).show();
                if (state==TelephonyManager.CALL_STATE_RINGING) {
   //                 String mDirName = mDBConnector.getDirNameByPhone(incomingNumber);
   //                 String msg = incomingNumber+'-'+mDirName;
    //                int duration = Toast.LENGTH_LONG;
                //    for (int i = 1; i <= 3; i++) {
                        Toast.makeText(context, incomingNumber+ ": "+Params.getCoordTxt(), Toast.LENGTH_LONG).show();

               // if (incomingNumber.length()>0)
                        handleIncomingCall(incomingNumber);
                }

            }
        };


        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);//.LISTEN_CALL_STATE
/*
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My_Loc_Service")
                .setContentText("My Location Service is running in foreground")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        // Установка службы в качестве foreground service
        startForeground(1, notification);
*/
/*
        //повышение приоритета
        if (Params.IsForeground.equals("Y")) {
            int NOTIFICATION_ID = 250572;
            Intent intent1 = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 1, intent1, PendingIntent.FLAG_IMMUTABLE);

            //@SuppressWarnings("deprecation")
            Notification notification = new Notification(R.drawable.ic_launcher_background,
                    "Running in the Foreground", System.currentTimeMillis());
          //  notification.setLatestEventInfo(this, getString(R.string.app_name), "", pi);
            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
            startForeground(NOTIFICATION_ID, notification);
        }
        else {
            this.stopForeground(false);
        }
        //-------------------------
*/
        return Service.START_STICKY;
    }

    /*
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
          //  handleIncomingCall(incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // Звонок поступил
                    handleIncomingCall(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Звонок принят или находится в процессе
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // Звонок завершен
                    break;
            }
        }
    }
*/

    private void handleIncomingCall(String incomingNumber) {
        // Вызываем свой метод при поступлении звонка
        // Например, можно запустить уведомление или выполнить какие-то действия
        new Thread(() -> {
            try {
                //String jsonBody = "{\"code\":\"ag234678\",\"is_log\":\"1\",\"lat\":\""
                //         +latitude+"\",\"lon\":\""+longitude+"\"}";
  //              Toast.makeText(getApplicationContext(), "lat5="+Params.latitude, Toast.LENGTH_SHORT).show();

                String urlString = "https://site-www.ru/maptrack/add_point.php?code=ag234678&is_log=1&lat="+Params.latitude+"&lon="+Params.longitude;
                // textView.setText(urlString);
                String response = HttpClient.sendGetRequest(urlString);
                //runOnUiThread(() -> {
               //     textView.setText("response="+response );
                    // Обновите UI с полученными данными
                    Toast.makeText(getApplicationContext(), "response="+response+" "+Params.getCoordTxt(), Toast.LENGTH_SHORT).show();
              //  });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

