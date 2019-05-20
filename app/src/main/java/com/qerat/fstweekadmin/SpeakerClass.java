package com.qerat.fstweekadmin;

import java.io.Serializable;

public class SpeakerClass implements Serializable {
    private String speakerName;
    private String pushId;
    private String speakerDetails;
    private String speakerKeynote;
    private String dayOfTalk;

    SpeakerClass(String pushId, String speakerName, String speakerDetails, String dayOfTalk, String speakerKeynote){
        this.pushId=pushId;
        this.speakerDetails=speakerDetails;
        this.speakerName=speakerName;
        this.speakerKeynote=speakerKeynote;
        this.dayOfTalk=dayOfTalk;
    }
    SpeakerClass(){

    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getSpeakerDetails() {
        return speakerDetails;
    }

    public void setSpeakerDetails(String speakerDetails) {
        this.speakerDetails = speakerDetails;
    }

    public String getSpeakerKeynote() {
        return speakerKeynote;
    }

    public void setSpeakerKeynote(String speakerKeynote) {
        this.speakerKeynote = speakerKeynote;
    }

    public String getDayOfTalk() {
        return dayOfTalk;
    }

    public void setDayOfTalk(String dayOfTalk) {
        this.dayOfTalk = dayOfTalk;
    }
}
