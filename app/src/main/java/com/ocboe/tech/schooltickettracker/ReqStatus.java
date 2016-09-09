package com.ocboe.tech.schooltickettracker;

/**
 * Created by Brad on 9/8/2016.
 */
public class ReqStatus {
    private int reqNumber;
    private String reqPO;
    private String reqDate;
    private String reqVendor;
    private String reqStatus;

    //Constructor

    public ReqStatus(int reqNumber, String reqPO, String reqDate, String reqVendor, String reqStatus) {
        this.reqNumber = reqNumber;
        this.reqPO = reqPO;
        this.reqDate = reqDate;
        this.reqVendor = reqVendor;
        this.reqStatus = reqStatus;
    }

    public int getReqNumber() {
        return reqNumber;
    }

    public void setReqNumber(int reqNumber) {
        this.reqNumber = reqNumber;
    }

    public String getReqPO() {
        return reqPO;
    }

    public void setReqPO(String reqPO) {
        this.reqPO = reqPO;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqVendor() {
        return reqVendor;
    }

    public void setReqVendor(String reqVendor) {
        this.reqVendor = reqVendor;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }
}
