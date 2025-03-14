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
    private final OkHttpClient client = new OkHttpClient();

    public void executeRequest(final String url, final Callback callback) {

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                        // Обработка окончательной ошибки
                    callback.onFailure(call, new IOException("Unexpected code " + response));
                } else {
                    // Обработка успешного ответа
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public String sendGetRequest(String url) throws Exception {
        Request request = getRequest(url) ;

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    private Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
    }

}