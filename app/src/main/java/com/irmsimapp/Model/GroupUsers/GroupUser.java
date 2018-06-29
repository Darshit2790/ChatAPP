package com.irmsimapp.Model.GroupUsers;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GroupUser implements Serializable{

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
    public class Datum implements Serializable{

        @SerializedName("GroupNo")
        @Expose
        private String groupNo;
        @SerializedName("TransfereeName")
        @Expose
        private String transfereeName;
        @SerializedName("PhotoUrl")
        @Expose
        private String photoUrl;
        @SerializedName("UserList")
        @Expose
        private ArrayList<UserList> userList = null;

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
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public ArrayList<UserList> getUserList() {
            return userList;
        }

        public void setUserList(ArrayList<UserList> userList) {
            this.userList = userList;
        }



        public class UserList implements Serializable{

            @SerializedName("UserName")
            @Expose
            private String userName;
            @SerializedName("LoginName")
            @Expose
            private String loginName;
            @SerializedName("FullName")
            @Expose
            private String fullName;
            @SerializedName("Email")
            @Expose
            private String email;
            @SerializedName("Phone")
            @Expose
            private String phone;
            @SerializedName("PhotoUrl")
            @Expose
            private String photoUrl;
            @SerializedName("UserType")
            @Expose
            private String userType;

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

            public String getFullName() {
                return fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPhotoUrl() {
                return photoUrl;
            }

            public void setPhotoUrl(String photoUrl) {
                this.photoUrl = photoUrl;
            }

            public String getUserType() {
                return userType;
            }

            public void setUserType(String userType) {
                this.userType = userType;
            }



        }



    }

}
