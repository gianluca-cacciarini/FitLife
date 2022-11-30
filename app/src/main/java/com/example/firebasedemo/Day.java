package com.example.firebasedemo;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Day implements Comparable<Day>{

    public String date;
    //0: food
    //1: exercise
    public int or;
    public String food_name;
    public int quantity;
    public String exercise_name;
    public int set;
    public int rep;

    public Day() {
        super();
        this.date = "00 00 0000";
        this.or = 0;
        this.food_name = "none";
        this.quantity = 0;
        this.exercise_name = "none";
        this.set = 0;
        this.rep = 0;
    }

    public Day(String date){
        super();
        this.date = date;
    }

    public Day(String date,String food, int quantity){
        super();
        this.date = date;
        this.food_name = food;
        this.quantity = quantity;
        this.or = 0;
    }

    public Day(String date, String exercise, int set, int rep){
        super();
        this.date = date;
        this.exercise_name = exercise;
        this.set = set;
        this.rep = rep;
        this.or = 1;
    }

    @Override
    public int hashCode() {
        return date.hashCode()+food_name.hashCode()+exercise_name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null || !obj.getClass().equals(this.getClass())) return false;
        Day aux = (Day) obj;
        if(aux.date.equals(this.date) && food_name.equals(this.food_name)) return true;
        return aux.date.equals(this.date) && exercise_name.equals(this.exercise_name);
    }

    @NonNull
    @Override
    public String toString() {
        String res = "";
        String space = "\t\t";
        res += space+date+"\n";
        if(this.getOr()==0) {
            res += space+food_name+" q"+String.valueOf(quantity)+"\n";
        }
        else{
            res += space+exercise_name+" s: "+String.valueOf(set)+" r: "+String.valueOf(rep)+"\n";
        }
        return res;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOr() {
        return or;
    }

    public void setOr(int or) {
        this.or = or;
    }

    public String getFood_name() {
        return food_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    @Override
    public int compareTo(Day day) {
        //function that will be use for showing the foods and exercises in the diary. in our case we want to show them in order
        //and for us the order is:
        //first we show the foods (in lexicographical order) and then the exercise (in lexicographical order)
        //soo food>exercise

        if (this.getOr() == day.getOr()) {
            int ret = 0;
            if(this.getOr()==0){
                ret = this.getFood_name().compareTo(day.getFood_name());
            }
            else ret = this.getExercise_name().compareTo(day.getExercise_name());
            Log.d("deubg", this.getFood_name()+this.getExercise_name()+this.getOr()+" vs "+day.getFood_name()+day.getExercise_name()+day.getOr()+" --> "+ret);
            return ret;
        }

        //if exercise vs food return a positive number
        else if (this.getOr() > day.getOr()) {
            Log.d("deubg", this.getFood_name()+this.getExercise_name()+this.getOr()+" vs "+day.getFood_name()+day.getExercise_name()+day.getOr()+" --> "+1);
            return 1;
        }

        //if food vs exercise return a negative number
        else {
            Log.d("deubg", this.getFood_name()+this.getExercise_name()+this.getOr()+" vs "+day.getFood_name()+day.getExercise_name()+day.getOr()+" --> "+-1);
            return -1;
        }
    }
}
