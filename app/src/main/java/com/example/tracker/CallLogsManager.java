package com.example.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.CallLog;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CallLogsManager {

    private Context context;

    public CallLogsManager(Context context) {
        this.context = context;
    }

    @SuppressLint("Range")
    public List<CallLogEntry> getOutgoingCalls(ArrayList<User> users) {
        List<CallLogEntry> callLogEntries = new ArrayList<>();

        // Создаем мапу для быстрого поиска имен контактов по номерам
        HashMap<String, String> numberToNameMap = new HashMap<>();
        fetchContacts(numberToNameMap, users);

        // Чтение журнала исходящих вызовов
        ContentResolver cr = context.getContentResolver();
        //        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE + "=?",
        //        new String[]{String.valueOf(CallLog.Calls.OUTGOING_TYPE)}, null);
        // Получаем текущее время
        long currentTime = System.currentTimeMillis();
        // Получаем время 24 часа назад
        long yesterdayTime = currentTime -3*(24 * 60 * 60 * 1000); // 3*24 часа в миллисекундах

        // Фильтрация по времени: только вызовы за последние 24 часа
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.TYPE + "=? AND " + CallLog.Calls.DATE + ">=?",
                new String[]{String.valueOf(CallLog.Calls.OUTGOING_TYPE), String.valueOf(yesterdayTime)},
                CallLog.Calls.DATE + " DESC"); // Сортируем по дате в обратном порядке

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                //String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                long dateInMillis = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); // Получаем дата в миллисекундах
                // Форматируем дату
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date(dateInMillis));
                String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                Log.debug("number="+number);

                // Проверяем, попадает ли номер в целевые номера
                for (User user : users) {
//                    if (number.replace("+", "").equals(user.getPhone().replace("+", ""))) {
                    if (number.length()>10 && (number.substring(number.length() - 10).equals (user.getPhone().substring(user.getPhone().length() - 10)))) {
                        String name = numberToNameMap.getOrDefault(number.substring(number.length() - 10), "Unknown");
                        boolean isExist = false;
                        for (CallLogEntry callLogEntry:callLogEntries){
                            if (callLogEntry.number.substring(callLogEntry.number.length()-10).equals(number.substring(number.length()-10))) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist)
                            callLogEntries.add(new CallLogEntry(name, number, date, duration));
                    }
                }
            }
            cursor.close();
        }

        return callLogEntries;
    }

    @SuppressLint("Range")
    private void fetchContacts(HashMap<String, String> numberToNameMap, ArrayList<User> users) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);


        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","").replace("-","");
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    if( number.length() >= 10 ) {
                        Log.debug("номер=" + number);
                        for (User user : users) {
//                    if (number.replace("+", "").equals(user.getPhone().replace("+", ""))) {
                            if (number.substring(number.length() - 10).equals (user.getPhone().substring(user.getPhone().length() - 10))) {
                                numberToNameMap.put(number.substring(number.length() - 10), name);
                            }
                        }
                    }
            }
            cursor.close();
        }
    }

    public static class CallLogEntry implements Serializable {
        private String name;
        private String number;
        private String date;
        private String duration;
        private String latitude;
        private String longitude;
        private int errCode;
        private  String errMsg;


        public CallLogEntry(String name, String number, String date, String duration) {
            this.name = name;
            this.number = number;
            this.date = date;
            this.duration = duration;
            this.errCode = 0;
        }
        @Override
        public String toString() {
            String ret;
            ret = name + "\n" + number + "\n"+date+", " + duration + " сек.";
            if (errMsg!=null){
                ret = ret + "\n"+"Ошибка "+errCode+":"+errMsg;
            }
            else if (longitude!=null){
                ret = ret + "\n"+"Коорд:"+latitude+"/"+longitude;
            }
            return ret;
        }

        public  CallLogEntry  addCoord(String latitude, String longitude){
            this.latitude = latitude;
            this.longitude = longitude;
            return this;
        }
        public  CallLogEntry  addErr(int errCode, String errMsg){
            this.errCode = errCode;
            this.errMsg = errMsg;
            return this;
        }

        public String getNumber() {
            return this.number;
        }
        public String getErrMsg() {
            return this.errMsg;
        }
        public int getErrCode() {
            return this.errCode;
        }

        public String getLatitude() {
            return this.latitude;
        }
        public String getLongitude() {
            return this.longitude;
        }

        // Getters and optional toString() method can be added here.
    }
}
