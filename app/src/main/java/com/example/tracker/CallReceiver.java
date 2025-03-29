package com.example.tracker;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.location.Location;
import android.os.Handler;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallReceiver extends BroadcastReceiver {
    private static final int[] DELAYS = {20000, 30000, 40000, 60000, 12000, 24000}; // 20с, 30с, 60с, 2мин, 4 мин - повторы отправки при неудаче
    private int currentAttempt = 0;
    private Handler handler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.debug("onReceive = "+intent.getAction());
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.debug("phoneNumber="+phoneNumber);
                if (phoneNumber!=null && Params.isTargetNumber(phoneNumber.substring(phoneNumber.length() - 10))) {
                    sendCoord(context);
                }
            }

        }
    }
    public void sendCoord(Context context){
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


        //}

    }
}

