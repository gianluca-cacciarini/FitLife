package com.example.firebasedemo;

import androidx.annotation.Nullable;

public class Step {

    public String date;
    public int step;

    public Step() {
        super();
        this.step = 0;
    }

    public Step(String date, int step) {
        this.date = date;
        this.step = step;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null || !obj.getClass().equals(this.getClass())) return false;
        Step aux = (Step) obj;
        if( aux.date.equals(this.date) ) return true;
        else return false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

}
