package com.example.firebasedemo;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Register2Activity extends AppCompatActivity {

    private Button done;
    private EditText carb_goal, weight, fat_goal, prot_goal, cal_goal, height;
    private int gender = -1;
    private String txt_email;
    private String txt_password;
    private String txt_full_name;
    private User user;
    private CheckBox male, female;

    private int goMain = 0;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

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
        startActivity(new Intent(getApplicationContext(),StartActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        Log.d("debug","Register2Activit");

        done = findViewById(R.id.done);
        carb_goal = findViewById(R.id.carbo_goal);
        fat_goal = findViewById(R.id.fat_goal);
        cal_goal = findViewById(R.id.cal_goal);
        prot_goal = findViewById(R.id.prot_goal);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images");
        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();
        loadData();

        //Toast.makeText(Register2Activity.this, txt_full_name, Toast.LENGTH_SHORT).show();

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
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

                if (TextUtils.isEmpty(carb_str) || TextUtils.isEmpty(fat_str) || TextUtils.isEmpty(cal_str) || TextUtils.isEmpty(prot_str) || gender == -1) {
                    Toast.makeText(Register2Activity.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register2Activity.this, Register2Activity.class));
                    finish();
                } else {
                    user = new User();
                    //user.setName(mFirebaseAuth.getCurrentUser().getEmail());
                    user.setFull_name("Mario Rossi");
                    user.setPassword("google_user");
                    user.setCal_goal(parseInt(cal_str));
                    user.setCarb_goal(parseInt(carb_str));
                    user.setFat_goal(parseInt(fat_str));
                    user.setProt_goal(parseInt(prot_str));
                    user.setHeight(parseInt(height_str));
                    user.setWeight(parseInt(weight_str));
                    // gender = 1 if male OR gender = 0 if female
                    user.setSex(gender);
                    if (mFirebaseAuth.getCurrentUser()!=null){
                        insertFoods();
                        insertExercises();
                    }
                    else{
                        registerUser(txt_email, txt_password, txt_full_name);
                    }

                }
            }
        });
    }

    private void registerUser(String email, String password, String full_name) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register2Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register2Activity.this, "Registering user successful!", Toast.LENGTH_SHORT).show();
                    user.setName(mFirebaseAuth.getCurrentUser().getEmail());
                    user.setPassword("default_user");
                    user.setFull_name(full_name);
                    insertFoods();
                    insertExercises();
                } else {
                    Toast.makeText(Register2Activity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertData() {
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(Register2Activity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.setName(mFirebaseAuth.getCurrentUser().getEmail());
                    Toast.makeText(Register2Activity.this, "Database updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register2Activity.this , MainActivity.class));
                } else {
                    Toast.makeText(Register2Activity.this, "failed to update the database!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void insertExercises(){
        mStorageReference.child("exercise").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                goMain += listResult.getItems().size();
                for( StorageReference fileref: listResult.getItems()){
                    addExerciseToUser(fileref.getName());
                }
            }
        });
    }

    public void addExerciseToUser(String image){
        mStorageReference.child("exercise").child(image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri image_url = task.getResult();
                String[] l = image.replace(".jpg","").split("-");
                Exercise e = new Exercise(l[0],l[1],image_url.toString());
                user.addExercise(e);
                mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user);
                goMain--;
                if(goMain == 0){
                    insertData();
                }
            }
        });
    }

    public void insertFoods(){
        mStorageReference.child("food").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                goMain = listResult.getItems().size();
                for( StorageReference fileref: listResult.getItems()){
                    addFoodToUser(fileref.getName());
                }
            }
        });
    }

    public void addFoodToUser(String image){
        mStorageReference.child("food").child(image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri image_url = task.getResult();
                String[] l = image.replace(".jpg","").split("-");
                Food f = new Food(l[0],l[1],Integer.parseInt(l[2]),Integer.parseInt(l[3]),Integer.parseInt(l[4]),Integer.parseInt(l[5]),image_url.toString());
                user.addFood(f);
                mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user);
                goMain--;
                if(goMain == 0){
                    insertData();
                }
            }
        });
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("ALL_ACTIVITY", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EMAIL", txt_email);
        editor.putString("PASS", txt_password);
        editor.putString("FULL_NAME", txt_full_name);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("ALL_ACTIVITY", MODE_PRIVATE);
        txt_email = sharedPreferences.getString("EMAIL", "none");
        txt_password = sharedPreferences.getString("PASS", "none");
        txt_full_name = sharedPreferences.getString("FULL_NAME", "none");
    }
}