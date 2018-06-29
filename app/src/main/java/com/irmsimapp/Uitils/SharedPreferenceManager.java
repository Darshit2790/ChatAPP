package com.irmsimapp.Uitils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by darshit on 25/5/17.
 */
public class SharedPreferenceManager {


    private Context context;
    private SharedPreferences sharedPreferences;


    private  final String KEY_USERNAME = "username";
    private  final String KEY_LOGINNAME = "loginname";
    private  final String KEY_PASSWORD = "password";
    private  final String KEY_USERTYPE = "usertype";
    private  final String KEY_PROFILEPICTURE = "profile_picture";

    public  String getKEY_PROFILEPICTURE() {
        return  sharedPreferences.getString(KEY_PROFILEPICTURE, "");
    }
    public void setKEY_PROFILEPICTURE(String profilepicture) {
        sharedPreferences.edit().putString(KEY_PROFILEPICTURE, profilepicture).apply();
    }




    public  String getKEY_LOGINNAME() {
        return  sharedPreferences.getString(KEY_LOGINNAME, "");
    }

    public void setKEY_LOGINNAME(String loginname) {
        sharedPreferences.edit().putString(KEY_LOGINNAME, loginname).apply();
    }

    public  String getKeyUsertype() {
        return  sharedPreferences.getString(KEY_USERTYPE, "");
    }
    public void setKeyUsertype(String usertype) {
        sharedPreferences.edit().putString(KEY_USERTYPE, usertype).apply();
    }


    public SharedPreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("imapp_preferences", Context.MODE_PRIVATE);
    }

    public  String getKeyUsername() {
        return  sharedPreferences.getString(KEY_USERNAME, "");
    }

    public void setKeyUsername(String username) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }


    public  String getKeyPassword() {
        return  sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public void setKeyPassword(String password) {
        sharedPreferences.edit().putString(KEY_PASSWORD, password).apply();
    }

    public void clearData() {
        sharedPreferences.edit().clear().commit();
    }
}
