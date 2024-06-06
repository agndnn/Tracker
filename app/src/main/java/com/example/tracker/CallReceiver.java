package com.example.tracker;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;
    private static final Notification CHANNEL_ID = null;
    private TelephonyManager telephonyManager;
    //private MyPhoneStateListener phoneStateListener;
  //  double latitude;
  //  double longitude;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Трубка не поднята, телефон звонит
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                Log.debug("Show window: " + phoneNumber);
                Toast.makeText(context, phoneNumber+ ": "+Params.getCoordTxt(), Toast.LENGTH_LONG).show();
                handleIncomingCall(context,phoneNumber);
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                if (incomingCall) {
                    Log.debug("Close window.");
                    incomingCall = false;
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".
                if (incomingCall) {
                    Log.debug("Close window.");
                    incomingCall = false;
                }
            }
        }
    }
    /*
    @Override
    public void onCreate() {
        super.onCreate();
      //  Toast.makeText(getApplicationContext(), "OnCreate=", Toast.LENGTH_SHORT).show();
        //telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //phoneStateListener = new MyPhoneStateListener();

    }

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

        return Service.START_STICKY;
    }
*/

    private void handleIncomingCall(Context context, String incomingNumber) {
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
                    Toast.makeText(context, "response="+response+" "+Params.getCoordTxt(), Toast.LENGTH_SHORT).show();
              //  });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}

