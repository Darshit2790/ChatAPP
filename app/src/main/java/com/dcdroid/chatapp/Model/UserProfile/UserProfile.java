
package com.dcdroid.chatapp.Model.UserProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class UserProfile {

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

        @SerializedName("UserName")
        @Expose
        private String userName;
        @SerializedName("LoginName")
        @Expose
        private String loginName;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("FullName")
        @Expose
        private String fullName;
        @SerializedName("UserType")
        @Expose
        private String userType;
        @SerializedName("PhotoUrl")
        @Expose
        private String photoUrl;

        @SerializedName("HeadUrl")
        @Expose
        private String headUrl;


        @SerializedName("Phone")
        @Expose
        private String Phone;


        public String getPhone() {
            return Phone;
        }

        public void setPhone(String phone) {
            Phone = phone;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

    }

}