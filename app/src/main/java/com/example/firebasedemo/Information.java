package com.example.firebasedemo;

public class Information {

    private String email;
    private String name;

    public Information() {
    }

    public Information(String email, String name) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
