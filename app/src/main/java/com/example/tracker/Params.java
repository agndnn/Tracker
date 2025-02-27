package com.example.tracker;

import java.util.ArrayList;

public final class Params {
    static double latitude;
    static double longitude;

    static final int coordRequestTriesDefault=3; //запросов на отправку координат

    static int coordRequestTries; //Сколько выполнить запросов на отправку координат

    static  String IsForeground ="Y";
    static  String IsAutoRun ="Y";

    static  String homeUrl;
    private  static  String homeUrlDefault = "https://site-www.ru/maptrack";
//    static  String addPointUrl = homeUrl+"/add_point.php";
    static  String userCode;
    static  String userPhone;
    static  String userName;
    static  String apiKey;
    private static  String apiKeyDefault = "a4304081-4a62-4707-9204-65de6edc6562";

    //static int httpMaxRetries = 1; // Максимальное количество повторных попыток отправки координат через HTTP
    //static int httpRetries; // Максимальное количество повторных попыток отправки координат через HTTP
    //static int httpDelayInSeconds = 1; // Задержка в секундах между попытками отправки координат через HTTP

    //отслеживаемые номера (исходящие)
    //static  String[] phonesOut= {"79585487061", "79081642616"};
    static ArrayList<User> usersOut = new ArrayList<>();//= {new User("ag234678","79585487061"), new User("sv23457","79081642616")};
    //отлеживающие номера (входящие)
   // static  String[] phonesIn= {"79585487061", "79081642616"};

    static String getCoordTxt(){
        return ("Coord: ("+latitude +","+ longitude+")");
    }
    static String getApiKey(){
        return (apiKey==null||apiKey.equals("")?apiKeyDefault:apiKey);
    }
    static  String getAddPointUrl(){
        String url = (homeUrl==null||homeUrl.equals(""))?homeUrlDefault:homeUrl;
        url=url+"/add_point.php?code="+Params.userCode+"&phone="+Params.userPhone+"&is_log=1&lat=" + Params.latitude + "&lon=" + Params.longitude;
        return (url);
    }
    static  String getCoordFromUrl(String code, String phone){
        return ((homeUrl==null||homeUrl.equals("")?homeUrlDefault:homeUrl)
                 +"/get_point.php?code="+code+"&is_log=1&phone="+phone.substring(phone.length()-10));
    }
    static  String getRegisterUrl(){
        String url = (homeUrl==null||homeUrl.equals(""))?homeUrlDefault:homeUrl;
        url=url+"/register.php?code="+Params.userCode+"&is_log=1&phone=" + Params.userPhone + "&name=" + Params.userName;
        return (url);
    }

    static  String getUserCodeByPhone(String phone){
        String code=null;
        for (User user : usersOut) {
            if (user.getPhone().substring(user.getPhone().length()-10).equals((phone.substring(phone.length()-10)))) {
                code = user.getCode().trim();
                break;
            }

        }
        return code;
    }

    static String getCanonicalPhone(String phone){
        String canonicalPhone;
        if (phone.length()>10)
            canonicalPhone=phone.substring(phone.length()-10);
        else
            canonicalPhone=phone;
        return canonicalPhone;
    }

}
