package com.example.tracker;

import java.util.ArrayList;

public final class Params {
    static double latitude;
    static double longitude;
    private  static  String homeUrlDefault = "http://f99600w0.beget.tech/tracker";
    static  String userCode;
    static  String userPhone;
    static  String userName;
    private static  String apiKeyDefault = "a4304081-4a62-4707-9204-65de6edc6562";

    static ArrayList<User> usersOut = new ArrayList<>();

    static String getApiKey(){
        return apiKeyDefault;
    }
    static  String getAddPointUrl(){
        String url = homeUrlDefault;
        url=url+"/add_point.php?code="+Params.userCode+"&phone="+Params.userPhone+"&is_log=1&lat=" + Params.latitude + "&lon=" + Params.longitude;
        return (url);
    }

    static  String getCoordFromUrl(String code, String phone){
        return homeUrlDefault
                 +"/get_point.php?code="+code+"&is_log=1&phone="+phone.substring(phone.length()-10);
    }

    static  String getRegisterUrl(){
        String url = homeUrlDefault;
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
