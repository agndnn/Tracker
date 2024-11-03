package com.example.tracker;

public final class Params {
    static double latitude;
    static double longitude;

    static  String IsForeground ="Y";
    static  String IsAutoRun ="Y";

    static  String addPointUrl = "https://site-www.ru/maptrack/add_point.php";
    static  String userCode = "ag234678";

    static String getCoordTxt(){
        return ("Coord: ("+latitude +","+ longitude+")");
    }
}
