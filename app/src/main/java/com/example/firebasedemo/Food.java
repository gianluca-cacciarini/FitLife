package com.example.firebasedemo;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Food implements Comparable<Food> {

    public String name;
    public String category;
    public int carb;
    public int prot;
    public int fat;
    public int cal;
    public String imageurl;

    public Food() {
        super();
        this.name = "dummy";
        this.category = "none";
        this.carb = 0;
        this.prot = 0;
        this.fat = 0;
        this.cal = 0;
        this.imageurl = "imageurl";
    }

    public Food(String name, String category, int carb, int prot, int fat, int cal){
        super();
        this.name = name;
        this.category = category;
        this.carb = carb;
        this.prot = prot;
        this.fat = fat;
        this.cal = cal;
        this.imageurl = "imageurl";
    }

    public Food(String name, String category, int carb, int prot, int fat, int cal, String url){
        super();
        this.name = name;
        this.category = category;
        this.carb = carb;
        this.prot = prot;
        this.fat = fat;
        this.cal = cal;
        this.imageurl = url;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null || !obj.getClass().equals(this.getClass())) return false;
        Food aux = (Food) obj;
        if( aux.name.equals(this.name)) return true;
        else return false;
    }

    @NonNull
    @Override
    public String toString() {
        String res = "";
        String space = "\t\t";
        res += space+name+" "+category+"\n";
        res += space+"c: "+String.valueOf(carb)+" p: "+String.valueOf(prot)+" f: "+String.valueOf(fat)+" c: "+String.valueOf(cal)+"\n";
        return res;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCarb() {
        return carb;
    }

    public void setCarb(int carb) {
        this.carb = carb;
    }

    public int getProt() {
        return prot;
    }

    public void setProt(int prot) {
        this.prot = prot;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }
    public String getImageurl() {return imageurl;}

    public void setImageurl(String imageurl) {this.imageurl = imageurl;}

    @Override
    public int compareTo(Food food) {
        return this.getName().compareTo(food.getName());
    }
}
