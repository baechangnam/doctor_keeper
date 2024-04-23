package com.apps.doctorkeeper_android.data.customer;

import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerXrayModel;

import java.util.ArrayList;

public class CustomerXrayResponse {
    String SUCCESS="";

    public String getSUCCESS() {
        return SUCCESS;
    }

    public ArrayList<CustomerXrayModel> getRESULT() {
        return RESULT;
    }

    ArrayList<CustomerXrayModel> RESULT;


}
