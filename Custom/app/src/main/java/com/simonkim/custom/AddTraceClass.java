package com.simonkim.custom;

import java.util.Map;

public class AddTraceClass {
    private String productName;
    private String traceNumber;
    private String traceYear;
    private String traceCompany;
    private String nowStatus;
    private Map nowTime;

    public AddTraceClass(String productName, String traceNumber, String traceYear, String traceCompany, Map nowTime) {
        this.productName = productName;
        this.traceNumber = traceNumber;
        this.traceYear = traceYear;
        this.traceCompany = traceCompany;
        this.nowStatus = "송장번호등록";
        this.nowTime = nowTime;
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


    public Map getNowTime() {
        return nowTime;
    }


    public void setNowTime(Map nowTime) {
        this.nowTime = nowTime;
    }

}