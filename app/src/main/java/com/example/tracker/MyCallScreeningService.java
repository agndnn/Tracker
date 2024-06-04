package com.example.tracker;

import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.widget.Toast;

public class MyCallScreeningService extends CallScreeningService {
    @Override
    public void onScreenCall(Call.Details callDetails) {
        // Обработка входящего вызова
        String handle = String.valueOf(callDetails.getHandle());
        Toast.makeText(this, "onScreenCall: "+callDetails.getCallerDisplayName(), Toast.LENGTH_SHORT).show();
        //callDetails.getCallerDisplayName()
        // Выполните здесь действия на основе информации о вызове
    }
}
