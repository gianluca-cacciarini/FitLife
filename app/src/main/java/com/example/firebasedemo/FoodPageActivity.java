package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FoodPageActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private RecyclerView recyclerView;
    private MyAdapterFood myAdapterFood;
    private ArrayList<Food> food_list;
    private ArrayList<String> food_name_list;
    private AutoCompleteTextView search;
    private ArrayAdapter adapter;

    private User user;
    private BottomNavigationView navigationView;

    private FloatingActionButton filter;
    private ExtendedFloatingActionButton filterA,filterB,filterC,filterD,filterE,filterF;
    private Animation filterOpen, filterClose;
    private Boolean isOpen = false;

    @Override
    public void onBackPressed() {
        //Intent i = new Intent();
        Log.d("AddExercisePage","onBackPressed");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 0);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_page);

        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        getUser();

        recyclerView = findViewById(R.id.food_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        food_list = new ArrayList<Food>();
        food_name_list = new ArrayList<String>();
        myAdapterFood = new MyAdapterFood(getApplicationContext(),food_list);
        recyclerView.setAdapter(myAdapterFood);

        search = findViewById(R.id.search_food_text);
        adapter = (ArrayAdapter<String>) new ArrayAdapter<String>(getApplicationContext(), R.layout.autocomplete_layout,food_name_list);
        search.setAdapter(adapter);

        filter = findViewById(R.id.foodfilter);
        filterA = findViewById(R.id.foodfiltertypeA);
        filterB = findViewById(R.id.foodfiltertypeB);
        filterC = findViewById(R.id.foodfiltertypeC);
        filterD = findViewById(R.id.foodfiltertypeD);
        filterE = findViewById(R.id.foodfiltertypeE);
        filterF = findViewById(R.id.foodfiltertypeF);

        filterA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodPageActivity.this, "Filter remove", Toast.LENGTH_SHORT).show();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFilteredFoodList("");
            }
        });

        filterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodPageActivity.this, "fish", Toast.LENGTH_SHORT).show();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFilteredFoodList("fish");
            }
        });

        filterC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodPageActivity.this, "meat", Toast.LENGTH_SHORT).show();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFilteredFoodList("meat");
            }
        });

        filterD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodPageActivity.this, "cereal", Toast.LENGTH_SHORT).show();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFilteredFoodList("cereal");
            }
        });

        filterE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodPageActivity.this, "fruit&veg", Toast.LENGTH_SHORT).show();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFilteredFoodList("fruit&veg");
            }
        });

        filterF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FoodPageActivity.this, "other", Toast.LENGTH_SHORT).show();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFilteredFoodList("other");
            }
        });

        filterOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.filter_open);
        filterClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.filter_close);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFunction();
            }
        });

        navigationView = findViewById(R.id.navigation_bar);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("debug","clicked");
                switch (item.getItemId()){

                    case R.id.bottom_diary:
                        Toast.makeText(getApplicationContext(),"diary",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return true;
                    case R.id.bottom_exercise:
                        Toast.makeText(getApplicationContext(),"exercise",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),ExercisePageActivity.class));
                        return true;
                    case R.id.bottom_food:
                        Toast.makeText(getApplicationContext(),"food",Toast.LENGTH_SHORT).show();
                        //do nothing im already in the food page
                        return true;
                    case R.id.bottom_step_counter:
                        Toast.makeText(getApplicationContext(),"timer",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),PedometerPageActivity.class));
                        return true;
                    case R.id.bottom_user:
                        Toast.makeText(getApplicationContext(),"user",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),UserPageActivity.class));
                        return true;
                }
                return true;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //non mi serve
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("debug","clicked");
                String prefix = search.getText().toString();
                food_list = new ArrayList<Food>();
                food_name_list = new ArrayList<String>();
                insertFoodList(prefix);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //non mi serve
            }
        });

    }

    public void animateFunction(){
        if(isOpen){
            filterA.startAnimation(filterClose);
            filterB.startAnimation(filterClose);
            filterC.startAnimation(filterClose);
            filterD.startAnimation(filterClose);
            filterE.startAnimation(filterClose);
            filterF.startAnimation(filterClose);
            filterA.setClickable(false);
            filterB.setClickable(false);
            filterC.setClickable(false);
            filterD.setClickable(false);
            filterE.setClickable(false);
            filterF.setClickable(false);
            isOpen=false;
        }
        else{
            filterA.startAnimation(filterOpen);
            filterB.startAnimation(filterOpen);
            filterC.startAnimation(filterOpen);
            filterD.startAnimation(filterOpen);
            filterE.startAnimation(filterOpen);
            filterF.startAnimation(filterOpen);
            filterA.setClickable(true);
            filterB.setClickable(true);
            filterC.setClickable(true);
            filterD.setClickable(true);
            filterE.setClickable(true);
            filterF.setClickable(true);
            isOpen=true;
        }
    }

    public void insertFilteredFoodList(String category){
        category = category.toLowerCase();
        for( String key : user.getFood_list().keySet()){
            if(category.equals("")){
                food_list.add(user.getFood_list().get(key));
                food_name_list.add(user.getFood_list().get(key).getName());
            }
            else if(user.getFood_list().get(key).getCategory().toLowerCase().equals(category)){
                food_list.add(user.getFood_list().get(key));
                food_name_list.add(user.getFood_list().get(key).getName());
            }
        }
        Collections.sort(food_list);
        Collections.sort(food_name_list);
        myAdapterFood = new MyAdapterFood(getApplicationContext(),food_list);
        recyclerView.setAdapter(myAdapterFood);
        myAdapterFood.notifyDataSetChanged();
        Log.d("debug","notifydatasethaschanged");
        Log.d("debug",food_name_list.toString());
        Log.d("debug",food_list.toString());
    }

    public void insertFoodList(String prefix){
        prefix = prefix.toLowerCase();
        for( String key : user.getFood_list().keySet()){
            Log.d("debug",user.getFood_list().get(key).getCategory());
            if(prefix.equals("")) {
                food_list.add(user.getFood_list().get(key));
                food_name_list.add(user.getFood_list().get(key).getName());
            }
            else{
                String name = user.getFood_list().get(key).getName();
                if( name.startsWith(prefix)){
                    food_list.add(user.getFood_list().get(key));
                    food_name_list.add(user.getFood_list().get(key).getName());
                }
            }
        }
        Collections.sort(food_list);
        Collections.sort(food_name_list);
        myAdapterFood = new MyAdapterFood(getApplicationContext(),food_list);
        recyclerView.setAdapter(myAdapterFood);
        myAdapterFood.notifyDataSetChanged();
        Log.d("debug","notifydatasethaschanged");
        Log.d("debug",food_name_list.toString());
        Log.d("debug",food_list.toString());
    }

    public void getUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                user = snapshot.getValue(User.class);
                String prefix = "";
                insertFoodList(prefix);
            }
        });
    }


}