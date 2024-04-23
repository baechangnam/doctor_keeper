package com.apps.doctorkeeper_android.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {

    protected MutableLiveData<String> _serverError =  new MutableLiveData<>();
    protected MutableLiveData<String> _networkError =  new MutableLiveData<>();
}
