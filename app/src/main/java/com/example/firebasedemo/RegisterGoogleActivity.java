package com.example.firebasedemo;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterGoogleActivity extends AppCompatActivity {

    private Button done;
    private EditText carb_goal, fat_goal, prot_goal, cal_goal, height, weight;

    private int gender = -1;
    String txt_email, txt_password;
    User user;
    private CheckBox male, female;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_google);

        done = findViewById(R.id.done);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        carb_goal = findViewById(R.id.carbo_goal);
        fat_goal = findViewById(R.id.fat_goal);
        cal_goal = findViewById(R.id.cal_goal);
        prot_goal = findViewById(R.id.prot_goal);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        loadData();

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setChecked(true);
                female.setChecked(false);
                gender = 1;
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setChecked(false);
                female.setChecked(true);
                gender = 0;
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //da fare il parseInt
                String carb_str = carb_goal.getText().toString();
                String fat_str = fat_goal.getText().toString();
                String cal_str = cal_goal.getText().toString();
                String prot_str = prot_goal.getText().toString();
                String height_str = height.getText().toString();
                String weight_str = weight.getText().toString();

                if (carb_str.equals("") || fat_str.equals("") || cal_str.equals("") || prot_str.equals("") || gender == -1) {
                    Toast.makeText(getApplicationContext(), "Empty credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    user = new User();
                    user.setName(mFirebaseAuth.getCurrentUser().getEmail());
                    user.setPassword("google_user");
                    user.setCal_goal(parseInt(cal_str));
                    user.setCarb_goal(parseInt(carb_str));
                    user.setFat_goal(parseInt(fat_str));
                    user.setProt_goal(parseInt(prot_str));
                    user.setHeight(parseInt(height_str));
                    user.setWeight(parseInt(weight_str));
                    // gender = 1 if male OR gender = 0 if female
                    user.setSex(gender);
                    insertUser();

                }
            }
        });

    }

    private void insertUser() {
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener( RegisterGoogleActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Database updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext() , MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "failed to update the database!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("ALL_ACTIVITY", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EMAIL", txt_email);
        editor.putString("PASS", txt_password);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("ALL_ACTIVITY", MODE_PRIVATE);
        txt_email = sharedPreferences.getString("EMAIL", "none");
        txt_password = sharedPreferences.getString("PASS", "none");
    }
}