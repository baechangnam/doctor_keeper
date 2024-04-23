package com.apps.doctorkeeper_android.data.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginResponse {
    String SUCCESS="";
    String RESULT="";

    public String getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(String SUCCESS) {
        this.SUCCESS = SUCCESS;
    }

    public String getRESULT() {
        return RESULT;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }



}
