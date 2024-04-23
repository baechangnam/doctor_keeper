package com.apps.doctorkeeper_android.data.customer;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.util.SingleLiveEvent;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class CustomerRepository {
    private AppApi appApi;
    private MutableLiveData<CustomerResponse> customerModelResponseMutableLiveData;
    private MutableLiveData<CustomerImageResponse> customerImageModelResponseMutableLiveData;
    private MutableLiveData<CustomerXrayResponse> xrayResponseMutableLiveData;
    private MutableLiveData<CustomerAgreementTemplateResponse> agreementTemplateResponseMutableLiveData;


    private MutableLiveData<CustomerAgreeResponse> customerAgreeLiveData;

    RbPreference pref;
    BaseActivity activity;
    public CustomerRepository(BaseActivity activity) {
        pref = new RbPreference(activity);
        customerModelResponseMutableLiveData = new MutableLiveData<>();
        customerImageModelResponseMutableLiveData  = new MutableLiveData<>();
        xrayResponseMutableLiveData = new MutableLiveData<>();

        agreementTemplateResponseMutableLiveData = new MutableLiveData<>();
        customerAgreeLiveData = new MutableLiveData<>();
        this.activity =activity;
    }

    public void getPatientList(String lstVistDt,String pageCheck,String page,String rows) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getPatientList(lstVistDt,pageCheck,page,rows)
                .enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        activity.progressOFF();

                        customerModelResponseMutableLiveData.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        activity.progressOFF();
                        Log.d("myLog", "error " + t.getMessage());
                        Log.d("myLog", "error ");
                    }
                });

    }

    public MutableLiveData<CustomerResponse> getCustomer() {
        return customerModelResponseMutableLiveData;
    }


    public void getPatientListSearch(String custNm) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getPatientListSearch(custNm)
                .enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        activity.progressOFF();

                        customerModelResponseMutableLiveData.setValue(response.body());

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        activity.progressOFF();
                        Log.d("myLog", "error " + t.getMessage());
                        Log.d("myLog", "error ");
                    }
                });

    }

    public void getPatientListCustInfoSearch(String custNm) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getPatientListCustInfoSearch(custNm)
                .enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        activity.progressOFF();

                        customerModelResponseMutableLiveData.setValue(response.body());

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        activity.progressOFF();
                        Log.d("myLog", "error " + t.getMessage());
                        Log.d("myLog", "error ");
                    }
                });

    }

    public void getPatientImageList(String chartNo) {
        Log.d("myLog", "getPatientImageList " + chartNo);
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getPatientImageList(chartNo)
                .enqueue(new Callback<CustomerImageResponse>() {
                    @Override
                    public void onResponse(Call<CustomerImageResponse> call, Response<CustomerImageResponse> response) {
                        activity.progressOFF();
                        customerImageModelResponseMutableLiveData.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<CustomerImageResponse> call, Throwable t) {
                        activity.progressOFF();
                    }
                });

    }

    public MutableLiveData<CustomerImageResponse> getCustomerImage() {
        return customerImageModelResponseMutableLiveData;
    }

    public void getAgreeList(String chartNo) {
        Log.d("myLog", "getPatientImageList " + chartNo);
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getCustomerAgreeList(chartNo)
                .enqueue(new Callback<CustomerAgreeResponse>() {
                    @Override
                    public void onResponse(Call<CustomerAgreeResponse> call, Response<CustomerAgreeResponse> response) {
                        activity.progressOFF();
                        customerAgreeLiveData.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<CustomerAgreeResponse> call, Throwable t) {
                        activity.progressOFF();
                    }
                });

    }

    public MutableLiveData<CustomerAgreeResponse> getCustomerAgreeList() {
        return customerAgreeLiveData;
    }


    public void getXrayList(String custNm) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getXrayList(custNm)
                .enqueue(new Callback<CustomerXrayResponse>() {
                    @Override
                    public void onResponse(Call<CustomerXrayResponse> call, Response<CustomerXrayResponse> response) {
                        activity.progressOFF();

                        xrayResponseMutableLiveData.setValue(response.body());

                    }

                    @Override
                    public void onFailure(Call<CustomerXrayResponse> call, Throwable t) {
                        activity.progressOFF();
                        Log.d("myLog", "error " + t.getMessage());
                        Log.d("myLog", "error ");
                    }
                });

    }

    public MutableLiveData<CustomerXrayResponse> getXrayResponseMutableLiveData() {
        return xrayResponseMutableLiveData;
    }


    public void getAgreementTemplate(String cnsltClssCd) {
        activity.progressON(activity);
        appApi = RetrofitAdapter.getClient(activity).create(AppApi.class);
        appApi.getAgreementTemplate(cnsltClssCd)
                .enqueue(new Callback<CustomerAgreementTemplateResponse>() {
                    @Override
                    public void onResponse(Call<CustomerAgreementTemplateResponse> call, Response<CustomerAgreementTemplateResponse> response) {
                        activity.progressOFF();

                        agreementTemplateResponseMutableLiveData.setValue(response.body());

                    }

                    @Override
                    public void onFailure(Call<CustomerAgreementTemplateResponse> call, Throwable t) {
                        activity.progressOFF();
                        Log.d("myLog", "error " + t.getMessage());
                        Log.d("myLog", "error ");
                    }
                });

    }

    public MutableLiveData<CustomerAgreementTemplateResponse> getTemplateList() {
        return agreementTemplateResponseMutableLiveData;
    }


}
