package com.example.tracker;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.location.Location;
import android.os.Handler;
import android.telephony.TelephonyManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
//import android.widget.Toast;


public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;
    private static final Notification CHANNEL_ID = null;
    private TelephonyManager telephonyManager;
    //private MyPhoneStateListener phoneStateListener;
  //  double latitude;
  //  double longitude;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "ON_Receive_Start", Toast.LENGTH_LONG).show();
        Log.debug("onReceive = "+intent.getAction());
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Трубка не поднята, телефон звонит

                //new Thread(() -> {
                    //try {
                        String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        incomingCall = true;
                        Log.debug("phoneNumber1 = "+phoneNumber);
                        if (phoneNumber!=null)
                            handleIncomingCall(context,phoneNumber);

                   // } catch (Exception e) {
                     //   e.printStackTrace();
                   // }
              //  }).start();



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
                    incomingCall = false;
                }
                sendCoord(context);

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
        //Toast.makeText(context, incomingNumber+ ": "+Params.getCoordTxt(), Toast.LENGTH_LONG).show();
        final Handler h = new Handler();
        //LocationTask locationTask = new LocationTask(context);
        //locationTask.doInBackground();
        //locationTask.execute();
        /*
        Data inputData = new Data.Builder()
                .putString("url", Params.getAddPointUrl())
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(HttpWorker.class)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);
         */
    }

    public void sendCoord(Context context){
        Params.coordRequestTries = Params.coordRequestTriesDefault; //нужно отправить координаты
        Log.debug("Установлен флаг передачи координат Params.coordRequestTries="+Params.coordRequestTries );

//      if (Params.coordRequestTries>0){
            // Получаем гео-координаты
        LocationHelper locationHelper = new LocationHelper(context);
        locationHelper.getLocation(new LocationHelper.OnLocationReceivedListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                Log.debug( "Received Location: Latitude: " + latitude + ", Longitude: " + longitude);
                // Здесь вы можете использовать полученные координаты
                locationHelper.stopLocationUpdates();

                HttpHelper httpHelper = new HttpHelper();
                String url = Params.getAddPointUrl();

                httpHelper.executeRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Обработать ошибку
                        android.util.Log.e("HTTP_ERROR", "Failed to fetch data", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Обработать успешный ответ
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            android.util.Log.d("HTTP_RESPONSE", responseData);
//                        Params.coordRequestTries--;
                            // stopSelf();
                        }
                    }
                });

            }
        });


        //}

    }
}

