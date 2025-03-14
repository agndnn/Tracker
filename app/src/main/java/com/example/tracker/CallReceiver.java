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
    private static final int[] DELAYS = {20000, 30000, 40000, 60000, 12000, 24000}; // 20с, 30с, 60с, 2мин, 4 мин - повторы отправки при неудаче
    private int currentAttempt = 0;
    private boolean flg = true;
    private Handler handler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".

                if (flg) {
                    flg = false;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flg = true;
                        }
                    }, 2000); // 2 сек

                    Log.debug("Call recieved");
                    sendCoord(context);
                }
            }
        }
    }

    public void sendCoord(Context context){

        LocationHelper locationHelper = new LocationHelper(context);
        locationHelper.getLocation(new LocationHelper.OnLocationReceivedListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {

                locationHelper.stopLocationUpdates();

                HttpHelper httpHelper = new HttpHelper();
                String url = Params.getAddPointUrl();
                Log.debug("Params.getAddPointUrl()="+Params.getAddPointUrl());

                httpHelper.executeRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Обработать ошибку
                        if (currentAttempt < DELAYS.length) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    currentAttempt++;
                                    Log.debug( "Не удалось отправить координаты. Повторная попытка отправки № "+currentAttempt);
                                    sendCoord(context);
                                }
                            }, DELAYS[currentAttempt]);
                        } else {
                            Log.debug( "Не удалось отправить запрос после нескольких попыток ("+currentAttempt+")");
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Обработать успешный ответ
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            android.util.Log.d("HTTP_RESPONSE", "Координаты успешно отправлены. responseData="+responseData);
                        }
                    }
                });
            }
        });
    }
}

