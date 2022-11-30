package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private RecyclerView recyclerView;
    private MyAdapterDiary myAdapterDiary;
    private HashMap<String,Food> food_list = new HashMap<String,Food>();
    private HashMap<String,Exercise> exercise_list = new HashMap<String,Exercise>();
    private ArrayList<Day> diary = new ArrayList<Day>();

    private User user;

    private BottomNavigationView navigationView;

    private Button test_button;
    private String currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        currentDay = df.format(currentTime);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images");
        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        getUser();

        recyclerView = findViewById(R.id.diary_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        food_list = new HashMap<String,Food>();
        exercise_list = new HashMap<String,Exercise>();
        diary = new ArrayList<Day>();
        myAdapterDiary = new MyAdapterDiary(getApplicationContext(),food_list,exercise_list,diary);
        recyclerView.setAdapter(myAdapterDiary);

        navigationView = findViewById(R.id.navigation_bar);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("debug","clicked");
                switch (item.getItemId()){

                    case R.id.bottom_diary:
                        Toast.makeText(getApplicationContext(),"diary",Toast.LENGTH_SHORT).show();
                        //do nothing since we are already in the Diary activity
                        return true;
                    case R.id.bottom_exercise:
                        Toast.makeText(getApplicationContext(),"exercise",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),ExercisePageActivity.class));
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

        test_button = findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("deubg","diary size: "+String.valueOf(user.getDiary().size()));
                Day new_d = new Day(currentDay,"curl",1,2);
                user.addDay(new_d);
                updateUser();
            }
        });
    }

    public void insertDiary(){
        diary.clear();
        for( String key : user.getDiary().keySet()){
            //Log.d("debug",currentDay+" vs "+user.getDiary().get(key).getDate());
            if(user.getDiary().get(key).getDate().equals(currentDay)) {
                diary.add(user.getDiary().get(key));
            }
            Log.d("debug",diary.toString());
        }
        Log.d("debug","size diary: "+String.valueOf(diary.size()));
        Collections.sort(diary);
    }

    public void getUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                user = snapshot.getValue(User.class);
                food_list = user.getFood_list();
                exercise_list = user.getExercise_list();
                insertDiary();
                myAdapterDiary = new MyAdapterDiary(getApplicationContext(),food_list,exercise_list,diary);
                recyclerView.setAdapter(myAdapterDiary);
            }
        });
    }

    public void updateUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getUser();
            }
        });
    }


    //function that check if the user is a google user or a default user in order to signout him/her
    public void signOut(){
        //we get the user values
        Log.d("debug","MainActivity 2 "+mFirebaseAuth.getCurrentUser().toString());
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("debug","MainActivity 3");
                //Toast.makeText(getApplicationContext(), snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                String user_json = snapshot.getValue().toString();
                user = snapshot.getValue(User.class);
                Toast.makeText(MainActivity.this, user.password, Toast.LENGTH_SHORT).show();
                if (user.password.equals("google_user")) {
                    Log.d("debug","MainActivity 4");
                    GoogleSignOut();
                }
                if (user.password.equals("default_user")) {
                    Log.d("debug","MainActivity 5");
                    DefaultSignOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //signout if the user is not a google user
    private void DefaultSignOut(){
        Log.d("debug","MainActivity-->default");
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

    //signout for the google users
    private void GoogleSignOut() {
        Log.d("debug","MainActivity-->google");
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // When task is successful
                    // Sign out from firebase
                    FirebaseAuth.getInstance().signOut();
                    GoogleSignIn.getClient(getApplicationContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();

                    // Display Toast
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                }
            }
        });
    }
}