package com.irmsimapp.Model.BadKeyWord;

/**
 * Created by darshit on 31/5/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BadKeyWord {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Msg")
    @Expose
    private String msg;
    @SerializedName("Data")
    @Expose
    private List<Datum> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }



public class Datum {

    @SerializedName("ItemNo")
    @Expose
    private String itemNo;
    @SerializedName("ItemName")
    @Expose
    private String itemName;

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}}