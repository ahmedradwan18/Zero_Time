package com.zerotime.zerotime2020.Moderator.Pojos;

public class Clerks {
    private String name,phone1,phone2,address,hasVehicle;
    private int age;

    public Clerks(String name, String phone1, String phone2, String address, String hasVehicle, int age) {
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.address = address;
        this.hasVehicle = hasVehicle;
        this.age = age;
    }

    public Clerks() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHasVehicle() {
        return hasVehicle;
    }

    public void setHasVehicle(String hasVehicle) {
        this.hasVehicle = hasVehicle;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
