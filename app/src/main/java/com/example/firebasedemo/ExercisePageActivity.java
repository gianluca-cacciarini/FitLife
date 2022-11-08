package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ExercisePageActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private User user;

    private BottomNavigationView navigationView;

    private RecyclerView recyclerView;
    private MyAdapterExercise myAdapterExercise;
    private ArrayList<Exercise> exercise_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_page);

        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        getUser();

        recyclerView = findViewById(R.id.exercise_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        exercise_list = new ArrayList<Exercise>();
        myAdapterExercise = new MyAdapterExercise(getApplicationContext(), exercise_list);
        recyclerView.setAdapter(myAdapterExercise);

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
                        //do nothing im already in the exericise page
                        return true;
                    case R.id.bottom_food:
                        Toast.makeText(getApplicationContext(),"food",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),FoodPageActivity.class));
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
    }

    public void insertExerciseList(){
        for( String key : user.getExercise_list().keySet()){
            exercise_list.add(user.getExercise_list().get(key));
        }
        myAdapterExercise.notifyDataSetChanged();
    }

    public void getUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                user = snapshot.getValue(User.class);
                insertExerciseList();
            }
        });
    }
}