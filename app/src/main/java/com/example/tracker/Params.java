package com.example.tracker;

public final class Params {
    static double latitude;
    static double longitude;

    static  String IsForeground ="Y";
    static  String IsAutoRun ="Y";

    static  String homeUrl = "https://site-www.ru/maptrack";
//    static  String addPointUrl = homeUrl+"/add_point.php";
    static  String userCode = "03538d0c758f7fa1";
    static  String apiKey = "a4304081-4a62-4707-9204-65de6edc6562";

    //отслеживаемые номера (исходящие)
    //static  String[] phonesOut= {"79585487061", "79081642616"};
    static  User[] usersOut= {new User("ag234678","79585487061"), new User("sv23457","79081642616")};
    //отлеживающие номера (входящие)
   // static  String[] phonesIn= {"79585487061", "79081642616"};

    static String getCoordTxt(){
        return ("Coord: ("+latitude +","+ longitude+")");
    }
    static  String getAddPointUrl(){
        return (Params.homeUrl+"/add_point.php?code="+Params.userCode+"&is_log=1&lat=" + Params.latitude + "&lon=" + Params.longitude);
    }
    static  String getCoordFromUrl(String code, String phone){
        return (Params.homeUrl+"/get_point.php?code="+code+"&is_log=1&phone="+phone.substring(phone.length()-10));
    }

    static  String getUserCodeByPhone(String phone){
        String code=null;
        for (User user : usersOut) {
            if (user.getPhone().substring(user.getPhone().length()-10).equals((phone.substring(phone.length()-10)))) {
                code = user.getCode();
                break;
            }

        }
        return code;
    }
}
