package com.apps.doctorkeeper_android.ui.customer;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.data.customer.CustomerRepository;
import com.apps.doctorkeeper_android.data.customer.CustomerResponse;


public class CustomerViewModel extends AndroidViewModel {
    private CustomerRepository repository;
    private MutableLiveData<CustomerResponse> _recentCustomerliveData = new MutableLiveData<>();

    BaseActivity baseActivity;
    public CustomerViewModel(@NonNull Application application, Activity activity) {
        super(application);
        baseActivity = (BaseActivity)activity;
        repository = new CustomerRepository(baseActivity);
    }

    public void getPatientList(String lstVistDt,String pageCheck,String page,String rows) {
        repository.getPatientList(lstVistDt,pageCheck,page,rows);
    }

    public void getPatientListSearch(String custNm) {
        repository.getPatientListSearch(custNm);
    }

    public void getPatientListCustInfoSearch(String custInfo) {
        repository.getPatientListCustInfoSearch(custInfo);
    }

    public MutableLiveData<CustomerResponse> getCustomerLiveData() {
        _recentCustomerliveData = repository.getCustomer();

        return _recentCustomerliveData;
    }







}
