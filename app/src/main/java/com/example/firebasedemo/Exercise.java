package com.example.firebasedemo;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Exercise implements Comparable<Exercise> {

    public String name;
    public String category;
    public String imageurl;

    public Exercise() {
        super();
        this.name = "dummy";
        this.category = "none";
        this.imageurl = "imageurl";
    }

    public Exercise(String name, String category){
        this.name=name;
        this.category=category;
    }

    public Exercise(String name, String category, String imageurl) {
        this.name = name;
        this.category = category;
        this.imageurl = imageurl;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null || !obj.getClass().equals(this.getClass())) return false;
        Exercise aux = (Exercise) obj;
        if( aux.name.equals(this.name)) return true;
        else return false;
    }

    @NonNull
    @Override
    public String toString() {
        String res = "";
        String space = "\t\t";
        res += space+name+" "+category+"\n";
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public int compareTo(Exercise exercise) {
        return this.getName().compareTo(exercise.getName());
    }
}
