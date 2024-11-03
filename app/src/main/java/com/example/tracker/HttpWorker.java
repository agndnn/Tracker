package com.example.tracker;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class HttpWorker extends Worker {
    public HttpWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Ваш фоновой код
        String urlString = Params.addPointUrl+"?code="+Params.userCode+"&is_log=1&lat=" + Params.latitude + "&lon=" + Params.longitude;
        Log.debug("urlString ="+urlString );

        try {
            String response = HttpClient.sendGetRequest(urlString);
            Log.debug( "Response: " + response);
            return Result.success(); // Укажите, что работа выполнена успешно
        } catch (Exception e) {
            Log.error( "Error: " + e.getMessage());
            return Result.failure(); // Укажите, что работа не удалась
        }
    }
}
