package com.apps.doctorkeeper_android.ui.consult;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.data.consult.CommonCodeResponse;
import com.apps.doctorkeeper_android.data.consult.ConsultDocRepository;
import com.apps.doctorkeeper_android.data.consult.ConsultDocResponse;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConsultDocViewModel extends AndroidViewModel {
    private ConsultDocRepository repository;
    private MutableLiveData<ConsultDocResponse> consultDocLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonCodeResponse> commodeLiveData;
    BaseActivity baseActivity;

    public ConsultDocViewModel(@NonNull Application application, Activity activity) {
        super(application);
        baseActivity = (BaseActivity)activity;
        repository = new ConsultDocRepository(baseActivity);

    }

    public void getDocList(String cnsltClssCd) {
        repository.getDocList(cnsltClssCd);
    }
    public MutableLiveData<ConsultDocResponse> getConsultLiveData() {
        consultDocLiveData = repository.getConsultDocResultLiveData();

        return consultDocLiveData;
    }

    public void getCommonCode(String cnsltClssCd) {
        repository.getCommonCode(cnsltClssCd);
    }

    public LiveData<CommonCodeResponse> getCommcodeResultLiveData() {
        if (commodeLiveData == null) {
            commodeLiveData = repository.getCommonCodeResultLiveData();
        }

        return commodeLiveData;
    }
}
