package com.qerat.fstweekadmin;

import java.io.Serializable;
import java.util.Map;

public class EventClass implements Serializable {
    private String pushId;
    private String eventTitle;
    private String speakerName;
    private String eventTime;
    private String eventDate;
    private String eventLocation;
    private String day;
    private String speakerPushId;
    private String participantsNo;
    private Map<String, String> participantsMap;

    EventClass() {

    }

    EventClass(String day, String pushId, String eventTitle, String speakerName, String eventDate, String eventTime, String eventLocation, String speakerPushId) {
        this.eventTitle = eventTitle;
        this.pushId = pushId;
        this.speakerName = speakerName;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        participantsNo = "0";
        this.day = day;
        this.speakerPushId = speakerPushId;
    }

    EventClass(String day, String pushId, String eventTitle, String speakerName, String eventDate, String eventTime, String eventLocation, String speakerPushId, String participantsNo) {
        this.eventTitle = eventTitle;
        this.pushId = pushId;
        this.speakerName = speakerName;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.day = day;
        this.participantsNo = participantsNo;
        this.speakerPushId = speakerPushId;
    }

    EventClass(String day, String pushId, String eventTitle, String speakerName, String eventDate, String eventTime, String eventLocation, String speakerPushId, String participantsNo, Map<String, String> participantsMap) {
        this.eventTitle = eventTitle;
        this.pushId = pushId;
        this.speakerName = speakerName;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.participantsMap = participantsMap;
        this.participantsNo = participantsNo;
        this.day = day;
        this.speakerPushId = speakerPushId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getParticipantsNo() {
        return participantsNo;
    }

    public void setParticipantsNo(String participantsNo) {
        this.participantsNo = participantsNo;
    }

    public Map<String, String> getParticipantsMap() {
        return participantsMap;
    }

    public void setParticipantsMap(Map<String, String> participantsMap) {
        this.participantsMap = participantsMap;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSpeakerPushId() {
        return speakerPushId;
    }

    public void setSpeakerPushId(String speakerPushId) {
        this.speakerPushId = speakerPushId;
    }
}
