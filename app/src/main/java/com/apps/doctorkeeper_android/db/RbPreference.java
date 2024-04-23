package com.apps.doctorkeeper_android.db;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class RbPreference {

    private final String PREF_NAME = "com.apps.doctorkeeper_android";

    static Context mContext;
    public static String AUTH_TOKEN = "AUTH_TOKEN";
    public static String MEM_ID = "MEM_ID";
    public static String REG_ID = "REG_ID";
    public static String IS_LOGIN = "IS_LOGIN";
    public static String AUTO_LOGIN = "AUTO_LOGIN";
    public static String IS_X_RAY = "IS_X_RAY";
    public static String CSID = "CSID";
    public static String CSNM = "CSNM";
    public static String CSPB = "CSPB";
    public static String USERNM = "USERNM";

    public static String API_URL = "API_URL";
    public static String IMG_URL = "IMG_URL";

    public static String BASE_URL = "BASE_URL";

    public static String IP_01 = "IP_01";
    public static String IP_02 = "IP_02";
    public static String IP_03 = "IP_03";
    public static String IP_04 = "IP_04";


    public RbPreference(Context c) {
        mContext = c;
    }

    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}

