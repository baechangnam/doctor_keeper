package com.apps.doctorkeeper_android.data.customer;

import com.apps.doctorkeeper_android.data.customer.model.AgreementTemplateModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;

import java.util.ArrayList;

public class CustomerAgreementTemplateResponse {
    String total="";
    String SUCCESS="";
    ArrayList<AgreementTemplateModel> RESULT;
    String records="";

    public String getTotal() {
        return total;
    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public ArrayList<AgreementTemplateModel> getRESULT() {
        return RESULT;
    }

    public String getRecords() {
        return records;
    }

    public String getPage() {
        return page;
    }

    String page="";






}
