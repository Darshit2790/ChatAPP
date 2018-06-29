package com.irmsimapp.Model;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by darshit on 25/4/17.
 */
public class ChatModel {


    String name;
    String message;
    private String confId;
    private String time;

    private String file_name;
    private String sender_profile_picture;

    private String image_width;
    private String image_height;
    private String message_id;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    private String file_url;

    public String getSender_profile_picture() {
        return sender_profile_picture;
    }

    public void setSender_profile_picture(String sender_profile_picture) {
        this.sender_profile_picture = sender_profile_picture;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getImage_width() {
        return image_width;
    }

    public void setImage_width(String image_width) {
        this.image_width = image_width;
    }

    public String getImage_height() {
        return image_height;
    }

    public void setImage_height(String image_height) {
        this.image_height = image_height;
    }


    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }


    public String getFile_name() {
        return file_name;
    }
    private int isMultimedia;

    public String getTimeinMillis() {
        return timeinMillis;
    }

    public void setTimeinMillis(String timeinMillis) {
        this.timeinMillis = timeinMillis;
    }

    private String timeinMillis;

    /*private boolean isRead = true;*/

    public ChatModel() {
        this.confId = this.name = this.time = this.message = "";
    }

    public ChatModel(String confId, String senderName, String time, String message,int isMyMessage,int isMultimedia,final String file_name,final String file_url,final String sender_profile_picture,String timeinMillis) {
        super();
        this.confId = confId;
        this.name = senderName;
        this.time = time;
        this.timeStamp = time;
        this.message = message;
        this.isMyMessage = isMyMessage;
        this.isMultimedia = isMultimedia;
        this.file_name = file_name;
        this.file_url = file_url;
        this.sender_profile_picture = sender_profile_picture;
        this.timeinMillis = timeinMillis;
    }


    public int isMultimedia() {
        return isMultimedia;
    }

    public void setMultimedia(int multimedia) {
        isMultimedia = multimedia;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    int isMyMessage;

    public int isMyMessage() {
        return isMyMessage;
    }

    public void setMyMessage(int myMessage) {
        isMyMessage = myMessage;
    }

    String timeStamp;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
