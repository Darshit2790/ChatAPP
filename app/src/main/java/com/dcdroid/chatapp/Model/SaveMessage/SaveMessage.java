package com.dcdroid.chatapp.Model.SaveMessage;

/**
 * Created by darshit on 31/5/17.
 */


        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

        import java.util.List;

public class SaveMessage {

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

        @SerializedName("FileUrl")
        @Expose
        private String fileUrl;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

    }
}