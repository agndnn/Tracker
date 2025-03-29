package com.example.tracker;

public class Log {
    private final static String LOG_CODE ="hh42";

    public static void debug(String s){
        android.util.Log.d(LOG_CODE,s);
    }
    public static void error(String s){
        android.util.Log.e(LOG_CODE,s);
    }
}
