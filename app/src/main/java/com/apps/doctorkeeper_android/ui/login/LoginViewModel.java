package com.apps.doctorkeeper_android.ui.login;


import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.data.login.LoginRepository;
import com.apps.doctorkeeper_android.data.login.LoginResponse;


public class LoginViewModel extends AndroidViewModel {

    private LoginRepository repository;
    private LiveData<LoginResponse> _liveData;
    BaseActivity baseActivity;
    public LoginViewModel(@NonNull Application application, Activity activity) {
        super(application);
        baseActivity = (BaseActivity)activity;
    }

    public void login(String userID, String password) {
        repository.loginRepo(userID, password);
    }

    public LiveData<LoginResponse> getLoginResultLiveData() {
        if (_liveData == null) {
            repository = new LoginRepository(baseActivity);
            _liveData = repository.getLoginResultLiveData();
        }

        return _liveData;
    }





}
