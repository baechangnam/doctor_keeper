package com.apps.doctorkeeper_android.data.consult;

import java.util.ArrayList;

public class ConsultDocResponse {
    String total="";
    String SUCCESS="";
    ArrayList<DocumentModel> RESULT;
    String records="";

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

    public ArrayList<DocumentModel> getRESULT() {
        return RESULT;
    }

    public void setRESULT(ArrayList<DocumentModel> RESULT) {
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

    String page="";



}
