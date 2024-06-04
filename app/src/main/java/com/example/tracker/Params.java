package com.example.tracker;

public final class Params {
    static double latitude;
    static double longitude;

    static  String IsForeground ="Y";
    static  String IsAutoRun ="Y";

    static String getCoordTxt(){
        return ("Coord: ("+latitude +","+ longitude+")");
    }
}
