package com.apps.doctorkeeper_android.data.customer;

import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;

import java.util.ArrayList;

public class CustomerResponse {
    String total="";
    String SUCCESS="";
    ArrayList<CustomerModel> RESULT;
    String records="";
    String page="";

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(String SUCCESS) {
        this.SUCCESS = SUCCESS;
    }

    public ArrayList<CustomerModel> getRESULT() {
        return RESULT;
    }

    public void setRESULT(ArrayList<CustomerModel> RESULT) {
        this.RESULT = RESULT;
    }

    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }





}
