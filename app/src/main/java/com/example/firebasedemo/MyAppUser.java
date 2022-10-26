package com.example.firebasedemo;

public class MyAppUser {

    private String name;
    private String calorie;


    public MyAppUser(String name, String calorie) {
        this.name = name;
        this.calorie = calorie;
    }

    public MyAppUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }
}
