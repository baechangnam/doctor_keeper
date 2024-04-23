package com.apps.doctorkeeper_android.data.consult;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.accounts.NetworkErrorException;

public  class ConsultDocRepository {
    private AppApi appApi;
    private MutableLiveData<ConsultDocResponse> consultDocResponseMutableLiveData;
    private MutableLiveData<CommonCodeResponse> commonCodeResponseMutableLiveData;
    RbPreference pref;
    BaseActivity activity;
    public ConsultDocRepository(BaseActivity activity) {
        pref = new RbPreference(activity);
        consultDocResponseMutableLiveData = new MutableLiveData<>();
        commonCodeResponseMutableLiveData = new MutableLiveData<>();

        this.activity =activity;
    }

    public void getDocList(String cnsltClssCd) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getCounseling(cnsltClssCd)
                .enqueue(new Callback<ConsultDocResponse>() {
                    @Override
                    public void onResponse(Call<ConsultDocResponse> call, Response<ConsultDocResponse> response) {
                        activity.progressOFF();
                        if(response.isSuccessful()){
                            consultDocResponseMutableLiveData.setValue(response.body());
                        }


                    }

                    @Override
                    public void onFailure(Call<ConsultDocResponse> call, Throwable t) {
                        activity.progressOFF();

                    }
                });

    }

    public void getCommonCode(String cdGrpId) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getCommonCode(cdGrpId)
                .enqueue(new Callback<CommonCodeResponse>() {
                    @Override
                    public void onResponse(Call<CommonCodeResponse> call, Response<CommonCodeResponse> response) {
                        activity.progressOFF();
                        commonCodeResponseMutableLiveData.setValue(response.body());

                    }

                    @Override
                    public void onFailure(Call<CommonCodeResponse> call, Throwable t) {
                        activity.progressOFF();

                    }
                });

    }

    public MutableLiveData<ConsultDocResponse> getConsultDocResultLiveData() {
        return consultDocResponseMutableLiveData;
    }


    public MutableLiveData<CommonCodeResponse> getCommonCodeResultLiveData() {
        return commonCodeResponseMutableLiveData;
    }
}
