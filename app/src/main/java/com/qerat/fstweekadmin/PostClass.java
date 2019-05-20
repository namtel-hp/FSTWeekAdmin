package com.qerat.fstweekadmin;

import java.util.Date;

public class PostClass {
    private String email;
    private String msg;
    private long msgTime;
    private String pushId;
    PostClass() {

    }

    PostClass(String pushId, String email, String msg, long msgTime) {
        this.email = email;
        this.msg = msg;
        if (msgTime > 0) {
            msgTime *= -1;
        }
        this.msgTime = msgTime;
        this.pushId=pushId;
    }

    PostClass(String pushId, String email, String msg) {
        this.email = email;
        this.msg = msg;
        msgTime = -new Date().getTime();
        this.pushId=pushId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getMsgTime() {
        return -msgTime;
    }

    public void setMsgTime(long msgTime) {
        if (msgTime > 0) {
            msgTime *= -1;
        }
        this.msgTime = msgTime;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
