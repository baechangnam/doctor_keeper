package com.apps.doctorkeeper_android.data.customer;

import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.data.customer.model.MedicalModel;

import java.util.ArrayList;

public class CustomerUpdateResponse {
    String SUCCESS="";

    public ArrayList<CustomerModel> getRESULT() {
        return RESULT;
    }

    ArrayList<CustomerModel> RESULT;

    public String getSUCCESS() {
        return SUCCESS;
    }


    public ArrayList<MedicalModel> getMEDICAL() {
        return MEDICAL;
    }

    ArrayList<MedicalModel> MEDICAL;


}
