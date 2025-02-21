package com.example.tracker;
import android.os.Handler;
import android.os.Looper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpHelper {
    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler(Looper.getMainLooper()); // Используем главный поток для задержки
    private int currentRetry;

    public void executeRequest(final String url,  final Callback callback) {
        executeRequestInternal(url, 0, callback);
    }

    private void executeRequestInternal(final String url, final int currentRetry, final Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Params.httpRetries = currentRetry;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if ( Params.httpRetries <= Params.httpMaxRetries) {
                    // Если не достигнуто максимальное количество попыток - повторяем запрос через заданный интервал
                    handler.postDelayed(() -> executeRequestInternal(url,  Params.httpRetries + 1, callback), Params.httpDelayInSeconds * 1000L);
                } else {
                    // Обработка окончательной ошибки
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if ( Params.httpRetries <= Params.httpMaxRetries) {
                        // Если ответ не успешен, повторяем запрос через заданный интервал
                        handler.postDelayed(() -> executeRequestInternal(url,  Params.httpRetries + 1, callback), Params.httpDelayInSeconds * 1000L);
                    } else {
                        // Обработка окончательной ошибки
                        callback.onFailure(call, new IOException("Unexpected code " + response));
                    }
                } else {
                    // Обработка успешного ответа
                    callback.onResponse(call, response);
                }
            }
        });
    }
}