package com.zerotime.zerotime2020.Pojos;

public class HistoryPojo {
    private String description, date, price, size, Raddress, Rname, Rphone1, Rphone2;

    public HistoryPojo(String description, String date, String price, String size, String raddress, String rname, String rphone1, String rphone2) {
        this.description = description;
        this.date = date;
        this.price = price;
        this.size = size;
        Raddress = raddress;
        Rname = rname;
        Rphone1 = rphone1;
        Rphone2 = rphone2;
    }

    public HistoryPojo() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getRaddress() {
        return Raddress;
    }

    public void setRaddress(String raddress) {
        Raddress = raddress;
    }

    public String getRname() {
        return Rname;
    }

    public void setRname(String rname) {
        Rname = rname;
    }

    public String getRphone1() {
        return Rphone1;
    }

    public void setRphone1(String rphone1) {
        Rphone1 = rphone1;
    }

    public String getRphone2() {
        return Rphone2;
    }

    public void setRphone2(String rphone2) {
        Rphone2 = rphone2;
    }
}
