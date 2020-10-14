package com.zerotime.zerotime2020.Room.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public
class Complaint implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
     int complaintID;
    String date;
    String UserComplaint;
    String UserPhone;
    String name;


    public Complaint() {
    }

    public Complaint(@NonNull String userPhone, String userComplaint, String date, String name) {
        UserPhone = userPhone;
        UserComplaint = userComplaint;
        this.date = date;
        this.name = name;
    }

    @NonNull
    public String getUserPhone() {
        return UserPhone;
    }

    public int getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(int complaintID) {
        this.complaintID = complaintID;
    }

    public void setUserPhone(@NonNull String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserComplaint() {
        return UserComplaint;
    }

    public void setUserComplaint(String userComplaint) {
        UserComplaint = userComplaint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "UserPhone='" + UserPhone + '\'' +
                ", UserComplaint='" + UserComplaint + '\'' +
                '}';
    }
}
