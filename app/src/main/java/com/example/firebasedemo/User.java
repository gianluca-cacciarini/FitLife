package com.example.firebasedemo;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class User {

    public String name;
    public String password;
    public int sex;
    public int weight;
    public int height;
    public int carb_goal;
    public int prot_goal;
    public int fat_goal;
    public int cal_goal;
    public String full_name;

    //for each user we want to keep track of the list of all his/her foods and exercises
    //and we want to keep track of his/her diary
    public HashMap<String,Food> food_list;
    public HashMap<String,Exercise> exercise_list;
    public HashMap<String,Day> diary;
    public HashMap<String, Step> step_list;


    public User() {
        super();
        full_name = "dummy";
        name = "dummy";
        password = "none";
        food_list = new HashMap<String,Food>();
        exercise_list = new HashMap<String,Exercise>();
        diary = new HashMap<String,Day>();
        step_list = new HashMap<String, Step>();
        weight = 0;
        height = 0;
        carb_goal = 0;
        prot_goal = 0;
        fat_goal = 0;
        cal_goal = 0;
    }

    public User(String email, String password, String full_name,
                int sex, int weight, int height, int carb_goal, int prot_goal, int fat_goal, int cal_goal,
                 HashMap<String, Food> food_list, HashMap<String, Exercise> exercise_list, HashMap<String, Day> diary, HashMap<String,Step> step_list) {
        this.name = email;
        this.password = password;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.carb_goal = carb_goal;
        this.prot_goal = prot_goal;
        this.fat_goal = fat_goal;
        this.cal_goal = cal_goal;
        this.food_list = food_list;
        this.exercise_list = exercise_list;
        this.diary = diary;
        this.step_list = step_list;
        this.full_name = full_name;
    }

    public User(String email, String password, String full_name, int sex, int weight, int height, int carb_goal, int prot_goal, int fat_goal, int cal_goal) {
        this.name = email;
        this.password = password;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.carb_goal = carb_goal;
        this.prot_goal = prot_goal;
        this.fat_goal = fat_goal;
        this.cal_goal = cal_goal;
        this.food_list = new HashMap<String,Food>();
        this.exercise_list = new HashMap<String, Exercise>();
        this.diary = new HashMap<String,Day>();
        this.full_name = full_name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null || !obj.getClass().equals(this.getClass())) return false;
        User aux = (User) obj;
        if(aux.name.equals(this.name) ) return true;
        else return false;
    }

    @NonNull
    @Override
    public String toString() {
        String res = "";
        res += name +" "+password+"\n";
        res += String.valueOf(weight)+" "+String.valueOf(height)+" "+String.valueOf(cal_goal)+" "+String.valueOf(prot_goal)+" "+String.valueOf(fat_goal)+" "+String.valueOf(cal_goal)+"\n";
        res += "foods:\n";
        for(String key : food_list.keySet()){
            res += food_list.get(key).toString();
        }
        res += "exercises:\n";
        for(String key : exercise_list.keySet()){
            res += exercise_list.get(key).toString();
        }
        res += "diary:\n";
        for(String key : diary.keySet()){
            res += diary.get(key).toString();
        }
        return res;
    }

    public void addDay(Day new_day){
        if (this.diary == null){
            this.diary = new HashMap<String,Day>();
        }
        //cant put only the date as string otherwise i will not be able to have more item in the same day
        this.diary.put(new_day.date+new_day.food_name+new_day.exercise_name,new_day);
    }

    public void deleteDay(Day old_day){
        if (this.diary == null) return;
        diary.remove(old_day.date+old_day.food_name+old_day.exercise_name);
    }

    public void addExercise(Exercise new_exercise){
        if (this.exercise_list == null){
            this.exercise_list = new HashMap<String,Exercise>();
        }
        this.exercise_list.put(new_exercise.name,new_exercise);
    }

    public void addFood(Food new_food){
        if (this.food_list == null){
            this.food_list = new HashMap<String,Food>();
        }
        this.food_list.put(new_food.name,new_food);
    }

    public void addStep(Step new_step){
        if (this.step_list == null){
            this.step_list = new HashMap<String,Step>();
        }
        this.step_list.put(new_step.getDate(),new_step);
    }

    public String getName() {
        return name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCarb_goal() {
        return carb_goal;
    }

    public void setCarb_goal(int carb_goal) {
        this.carb_goal = carb_goal;
    }

    public int getProt_goal() {
        return prot_goal;
    }

    public void setProt_goal(int prot_goal) {
        this.prot_goal = prot_goal;
    }

    public int getFat_goal() {
        return fat_goal;
    }

    public void setFat_goal(int fat_goal) {
        this.fat_goal = fat_goal;
    }

    public int getCal_goal() {
        return cal_goal;
    }

    public void setCal_goal(int cal_goal) {
        this.cal_goal = cal_goal;
    }

    public HashMap<String, Food> getFood_list() {
        return food_list;
    }

    public void setFood_list(HashMap<String, Food> food_list) {
        this.food_list = food_list;
    }

    public HashMap<String, Exercise> getExercise_list() {
        return exercise_list;
    }

    public void setExercise_list(HashMap<String, Exercise> exercise_list) {
        this.exercise_list = exercise_list;
    }

    public HashMap<String, Day> getDiary() {
        return diary;
    }

    public void setDiary(HashMap<String, Day> diary) {
        this.diary = diary;
    }

    public HashMap<String, Step> getStep_list() {
        return step_list;
    }

    public void setStep_list(HashMap<String, Step> step_list) {
        this.step_list = step_list;
    }
}

