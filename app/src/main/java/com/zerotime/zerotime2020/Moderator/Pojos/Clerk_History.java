package com.zerotime.zerotime2020.Moderator.Pojos;

public class Clerk_History {

    private String clerkName,receiverPhone,date,description,price,size,
    receiverAddress;

    public Clerk_History() {
    }

    public Clerk_History(String clerkName, String receiverPhone, String date, String description, String price, String size, String receiverAddress) {
        this.clerkName = clerkName;
        this.receiverPhone = receiverPhone;
        this.date = date;
        this.description = description;
        this.price = price;
        this.size = size;
        this.receiverAddress = receiverAddress;
    }

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}
