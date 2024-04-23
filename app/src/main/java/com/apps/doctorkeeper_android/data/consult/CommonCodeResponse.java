package com.apps.doctorkeeper_android.data.consult;

import java.util.ArrayList;

public class CommonCodeResponse {
    String SUCCESS="";
    ArrayList<CommonCode> RESULT;

    public ArrayList<CommonCode> getRESULT() {
        return RESULT;
    }

    public void setRESULT(ArrayList<CommonCode> RESULT) {
        this.RESULT = RESULT;
    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(String SUCCESS) {
        this.SUCCESS = SUCCESS;
    }




}
