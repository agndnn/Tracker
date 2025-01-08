package com.example.tracker;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class HttpWorker extends Worker {
    public HttpWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
//        this.urlString = urlString;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Ваш фоновой код
        String url = getInputData().getString("url");
        //String urlString = Params.getAddPointUrl();
        Log.debug("urlString ="+url); //Params.getAddPointUrl());

        try {
            String response = HttpClient.sendGetRequest(url);
            Log.debug( "Response: " + response);
            return Result.success(); // Укажите, что работа выполнена успешно
        } catch (Exception e) {
            Log.error( "Error: " + e.getMessage());
            return Result.failure(); // Укажите, что работа не удалась
        }
    }
}
