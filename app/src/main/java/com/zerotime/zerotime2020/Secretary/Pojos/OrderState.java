package com.zerotime.zerotime2020.Secretary.Pojos;

public class OrderState {
    private String name,price,address,date,description,Receiver_phone,User_Phone;
    private int currentState;

    public OrderState(String name, String price, String address, String date, String description, String phone,String User_Phone,int currentState) {
        this.name = name;
        this.price = price;
        this.address = address;
        this.date = date;
        this.description = description;
        this.Receiver_phone = phone;
        this.User_Phone=User_Phone;
        this.currentState=currentState;
    }

    public OrderState() {
    }

    public String getUser_Phone() {
        return User_Phone;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public void setUser_Phone(String user_Phone) {
        User_Phone = user_Phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPhone() {
        return Receiver_phone;
    }

    public void setPhone(String phone) {
        this.Receiver_phone = phone;
    }
}
