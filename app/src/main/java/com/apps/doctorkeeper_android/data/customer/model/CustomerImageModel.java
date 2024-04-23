package com.apps.doctorkeeper_android.data.customer.model;

public class CustomerImageModel {
    String chrtNo ="";
    String custNm ="";
    String sexCd ="";
    String hpNo ="";
    String hmTelNo ="";
    String emailAddr ="";
    String custNo ="";
    String regId ="";
    String fileNm ="";
    String filePathNm ="";
    String groupId ="";
    String fileSeq ="";
    String regDt ="";
    String updId ="";
    String updDt ="";
    String picDt ="";
    String ordSeq ="";
    String thmbFileNm ="";

    public CustomerImageModel(String chrtNo, String custNm, String sexCd, String hpNo, String hmTelNo, String emailAddr, String custNo, String regId, String fileNm, String filePathNm, String groupId, String fileSeq, String regDt, String updId, String updDt, String picDt, String ordSeq, String thmbFileNm, String useFlg) {
        this.chrtNo = chrtNo;
        this.custNm = custNm;
        this.sexCd = sexCd;
        this.hpNo = hpNo;
        this.hmTelNo = hmTelNo;
        this.emailAddr = emailAddr;
        this.custNo = custNo;
        this.regId = regId;
        this.fileNm = fileNm;
        this.filePathNm = filePathNm;
        this.groupId = groupId;
        this.fileSeq = fileSeq;
        this.regDt = regDt;
        this.updId = updId;
        this.updDt = updDt;
        this.picDt = picDt;
        this.ordSeq = ordSeq;
        this.thmbFileNm = thmbFileNm;
        this.useFlg = useFlg;
    }

    public String getChrtNo() {
        return chrtNo;
    }

    public String getCustNm() {
        return custNm;
    }

    public String getSexCd() {
        return sexCd;
    }

    public String getHpNo() {
        return hpNo;
    }

    public String getHmTelNo() {
        return hmTelNo;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public String getCustNo() {
        return custNo;
    }

    public String getRegId() {
        return regId;
    }

    public String getFileNm() {
        return fileNm;
    }

    public String getFilePathNm() {
        return filePathNm;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getFileSeq() {
        return fileSeq;
    }

    public String getRegDt() {
        return regDt;
    }

    public String getUpdId() {
        return updId;
    }

    public String getUpdDt() {
        return updDt;
    }

    public String getPicDt() {
        return picDt;
    }

    public String getOrdSeq() {
        return ordSeq;
    }

    public String getThmbFileNm() {
        return thmbFileNm;
    }

    public String getUseFlg() {
        return useFlg;
    }

    String useFlg ="";

}
