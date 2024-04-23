package com.apps.doctorkeeper_android.data.login;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class LoginRepository {
    private AppApi appApi;
    private MutableLiveData<LoginResponse> loginResultLiveData;
    RbPreference pref;
    BaseActivity activity;
    public LoginRepository(BaseActivity activity) {
        pref = new RbPreference(activity);
        loginResultLiveData = new MutableLiveData<>();

        this.activity =activity;
    }

    public void loginRepo(String empNo, String empPwd){
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.login(empNo,empPwd)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        activity.progressOFF();
                        if(response.isSuccessful()){
                            Headers headerList = response.headers();

                            List<String> cookieList = headerList.toMultimap().get("Set-Cookie");
                            try {
                                if(cookieList!=null){
                                    for(String cookieValue : cookieList){
                                        if(cookieValue.startsWith("CSID")){
                                            pref.put(RbPreference.CSID , cookieValue);
                                        }
                                        if(cookieValue.startsWith("CSNM")){
                                            pref.put(RbPreference.CSNM , cookieValue);
                                        }
                                        if(cookieValue.startsWith("CSPB")){
                                            pref.put(RbPreference.CSPB , cookieValue);
                                        }
                                        if(cookieValue.startsWith("USERNM")){
                                            pref.put(RbPreference.USERNM , cookieValue);
                                        }
                                    }
                                }

                                loginResultLiveData.postValue(response.body());
                            }catch (Exception e){
                                activity.progressOFF();
                                LoginResponse responses = new LoginResponse();
                                responses.setRESULT("false");
                            }
                        }else{
                            LoginResponse responses = new LoginResponse();
                            responses.setRESULT("false");

                            loginResultLiveData.postValue(responses);
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        activity.progressOFF();

                        LoginResponse response = new LoginResponse();
                        response.setRESULT("false");
                        response.setSUCCESS(t.getMessage());

                        loginResultLiveData.postValue(response);
                    }
                });
    }

    public LiveData<LoginResponse> getLoginResultLiveData() {
        return loginResultLiveData;
    }
}
