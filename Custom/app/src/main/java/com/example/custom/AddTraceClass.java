package com.example.custom;

public class AddTraceClass {
    private String productName;
    private String traceNumber;
    private String traceYear;
    private String traceCompany;
    private String nowStatus;

    public AddTraceClass() {
        this.productName = null;
        this.traceNumber = null;
        this.traceYear = null;
        this.traceCompany = null;
        this.nowStatus = "송장번호등록";
    }

    public AddTraceClass(String productName, String traceNumber, String traceYear, String traceCompany) {
        this.productName = productName;
        this.traceNumber = traceNumber;
        this.traceYear = traceYear;
        this.traceCompany = traceCompany;
        this.nowStatus = "송장번호등록";
    }


    public String getProductName() {

        return productName;

    }


    public void setProductName(String productName) {

        this.productName = productName;

    }


    public String getTraceNumber() {

        return traceNumber;

    }


    public void setTraceNumber(String traceNumber) {

        this.traceNumber = traceNumber;

    }


    public String getTraceYear() {

        return traceYear;

    }


    public void setTraceYear(String traceYear) {

        this.traceYear = traceYear;

    }


    public String getTraceCompany() {

        return traceCompany;

    }


    public void setTraceCompany(String traceCompany) {

        this.traceCompany = traceCompany;

    }


    public String getNowStatus() {

        return nowStatus;

    }


    public void setNowStatus(String nowStatus) {

        this.nowStatus = nowStatus;

    }

}