package com.qerat.fstweekadmin;

import java.io.Serializable;

public class MentorGroupClass implements Serializable {
    private String purpose;
    private String area;
    private String added;

    MentorGroupClass() {

    }

    MentorGroupClass(String purpose, String area, String added) {
        this.purpose = purpose;
        this.area = area;
        this.added = added;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }
}
