package com.irmsimapp.Model.GroupList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupList {

public class Datum {





    @SerializedName("PhotoUrl")
    @Expose
    private String PhotoUrl;
    @SerializedName("GroupNo")
    @Expose
    private String groupNo;
    @SerializedName("TransfereeName")
    @Expose
    private String transfereeName;

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getTransfereeName() {
        return transfereeName;
    }

    public void setTransfereeName(String transfereeName) {
        this.transfereeName = transfereeName;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

}



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

}