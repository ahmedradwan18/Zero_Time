package com.zerotime.zerotime2020.Moderator.Pojos;

public class OrdersNumber {
    private String name,phone;
    long ordersNumber;

    public OrdersNumber(String name, String phone, long ordersNumber) {
        this.name = name;
        this.phone = phone;
        this.ordersNumber = ordersNumber;
    }

    public OrdersNumber() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getOrdersNumber() {
        return ordersNumber;
    }

    public void setOrdersNumber(long ordersNumber) {
        this.ordersNumber = ordersNumber;
    }
}
