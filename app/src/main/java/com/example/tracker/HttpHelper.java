package com.example.tracker;
import android.os.Handler;
import android.os.Looper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class HttpHelper {
    private static final OkHttpClient client = new OkHttpClient();

    private Handler handler = new Handler(Looper.getMainLooper()); // Используем главный поток для задержки
    //private int currentRetry;

    //public void executeRequest(final String url,  final Callback callback) {
//        executeRequestInternal(url,  callback);
  //  }

    public void executeRequest(final String url, final Callback callback)  {
        Request request = getRequest(url);
        //new Request.Builder()
                //.url(url)
                //.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                //.build();
        //Params.httpRetries = currentRetry;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
  //              if ( Params.httpRetries <= Params.httpMaxRetries) {
                    // Если не достигнуто максимальное количество попыток - повторяем запрос через заданный интервал
//                    handler.postDelayed(() -> executeRequestInternal(url,  Params.httpRetries + 1, callback), Params.httpDelayInSeconds * 1000L);
//                } else {
                    // Обработка окончательной ошибки
                    callback.onFailure(call, e);
  //              }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
//                    if ( Params.httpRetries <= Params.httpMaxRetries) {
                        // Если ответ не успешен, повторяем запрос через заданный интервал
//                        handler.postDelayed(() -> executeRequestInternal(url,  Params.httpRetries + 1, callback), Params.httpDelayInSeconds * 1000L);
//                    } else {
                        // Обработка окончательной ошибки
                        callback.onFailure(call, new IOException("Unexpected code " + response));
//                    }
                } else {
                    // Обработка успешного ответа
                    callback.onResponse(call, response);
                }
            }
        });
    }

    //--------------------------------------------------
    //sendGetRequest
    //------------------------------------------------------------
    public  String sendGetRequest(String url) throws Exception {
        Request request = getRequest(url) ;
                //new Request.Builder()
                //.url(url)
                //.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                //.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    //------------------------------------------------------
    //getRequest
    //----------------------------------------------------
    private  Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
    }
}