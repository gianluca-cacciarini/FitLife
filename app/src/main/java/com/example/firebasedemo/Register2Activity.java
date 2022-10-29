package com.example.firebasedemo;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register2Activity extends AppCompatActivity {

    private Button done;
    private EditText carb_goal;
    private EditText fat_goal;
    private EditText prot_goal;
    private EditText cal_goal;
    private EditText height;
    private EditText weight;
    private int gender = -1;
    String txt_email;
    String txt_password;
    User user;


    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        done = findViewById(R.id.done);
        carb_goal = findViewById(R.id.carbo_goal);
        fat_goal = findViewById(R.id.fat_goal);
        cal_goal = findViewById(R.id.cal_goal);
        prot_goal = findViewById(R.id.prot_goal);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();
        loadData();

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
                    user.setCal_goal(parseInt(cal_str));
                    user.setCarb_goal(parseInt(carb_str));
                    user.setFat_goal(parseInt(fat_str));
                    user.setProt_goal(parseInt(prot_str));
                    user.setHeight(parseInt(height_str));
                    user.setWeight(parseInt(weight_str));
                    // gender = 1 if male OR gender = 0 if female
                    user.setSex(gender);
                    registerUser(txt_email, txt_password);

                }
            }
        });
    }
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked) gender = 1;
                // Put some meat on the sandwich
            else
                // Remove the meat
                break;
            case R.id.female:male:
                if (checked) gender = 0;
                // Cheese me
            else
                gender = -1;
                break;
            // TODO: Veggie sandwich
        }
    }

    private void registerUser(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register2Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register2Activity.this, "Registering user successful!", Toast.LENGTH_SHORT).show();
                    user.setName(mFirebaseAuth.getCurrentUser().getEmail());
                    insertData();
                    startActivity(new Intent(Register2Activity.this, MainActivity.class));
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
                    Toast.makeText(Register2Activity.this, "Database updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register2Activity.this , MainActivity.class));
                } else {
                    Toast.makeText(Register2Activity.this, "failed to update the database!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveData(){
        //
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