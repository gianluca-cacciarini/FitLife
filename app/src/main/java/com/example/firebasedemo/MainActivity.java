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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private TextView debug;
    private Button logout,add;
    private EditText edit;

    private RecyclerView recyclerView;
    private MyAdapterDiary myAdapterDiary;
    private ArrayList<Food> food_list = new ArrayList<Food>();

    private User user;

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images");
        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        getUser();

        logout = findViewById(R.id.logout);
        edit = findViewById(R.id.edit);
        add = findViewById(R.id.add);
        debug = findViewById(R.id.debug_main);
        debug.setText("DEBUG");

        recyclerView = findViewById(R.id.diary_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        food_list = new ArrayList<Food>();
        myAdapterDiary = new MyAdapterDiary(getApplicationContext(),food_list);
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


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debug","MainActivity 1");
                signOut();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_name = edit.getText().toString();
                if (txt_name.isEmpty()){
                    Toast.makeText(MainActivity.this, "No name entered!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("ProgrammingK").push().child("Name").setValue(txt_name);
                }
            }
        });
    }

    public void insertFoodList(){
        for( String key : user.getFood_list().keySet()){
            food_list.add(user.getFood_list().get(key));
        }
        myAdapterDiary.notifyDataSetChanged();
    }

    public void getUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                user = snapshot.getValue(User.class);
                //getAllImages();
                insertFoodList();
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