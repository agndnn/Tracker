package com.example.tracker;

public class User {
    String code;
    String phone;

    public User(String code, String phone){
        this.phone = phone;
        this.code = code;
    }
    public String getCode(){
        return code;
    }
    public String getPhone(){
        return phone;
    }
    public void setCode(String code){
        this.code = code;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
}
