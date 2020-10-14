package com.zerotime.zerotime2020.Moderator.Pojos;
 public class Complaint_Pojo {
    String UserName,UserPhone,Complaint,ComplaintDate;


     public Complaint_Pojo(String userName, String userPhone, String complaint, String complaintDate) {
         UserName = userName;
         UserPhone = userPhone;
         Complaint = complaint;
         ComplaintDate = complaintDate;
     }

     public Complaint_Pojo() {
     }

     public String getUserName() {
         return UserName;
     }

     public void setUserName(String userName) {
         UserName = userName;
     }

     public String getUserPhone() {
         return UserPhone;
     }

     public void setUserPhone(String userPhone) {
         UserPhone = userPhone;
     }

     public String getComplaint() {
         return Complaint;
     }

     public void setComplaint(String complaint) {
         Complaint = complaint;
     }

     public String getComplaintDate() {
         return ComplaintDate;
     }

     public void setComplaintDate(String complaintDate) {
         ComplaintDate = complaintDate;
     }
 }
