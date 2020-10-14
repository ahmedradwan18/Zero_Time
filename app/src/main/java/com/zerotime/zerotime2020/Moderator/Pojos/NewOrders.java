package com.zerotime.zerotime2020.Moderator.Pojos;

public class NewOrders {
    String orderDescription, orderDate, orderPrice, orderNotes, orderState, orderSize, receiverName, receiverAddress, receiverPrimaryPhone,
           receiverSecondaryPhone, userPrimaryPhone;
    public NewOrders(){

    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(String orderSize) {
        this.orderSize = orderSize;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPrimaryPhone() {
        return receiverPrimaryPhone;
    }

    public void setReceiverPrimaryPhone(String receiverPrimaryPhone) {
        this.receiverPrimaryPhone = receiverPrimaryPhone;
    }

    public String getReceiverSecondaryPhone() {
        return receiverSecondaryPhone;
    }

    public void setReceiverSecondaryPhone(String receiverSecondaryPhone) {
        this.receiverSecondaryPhone = receiverSecondaryPhone;
    }

    public String getUserPrimaryPhone() {
        return userPrimaryPhone;
    }

    public void setUserPrimaryPhone(String userPrimaryPhone) {
        this.userPrimaryPhone = userPrimaryPhone;
    }
}
