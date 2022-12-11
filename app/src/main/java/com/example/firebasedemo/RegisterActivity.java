package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText full_name;
    private Button signup;
    String txt_email;
    String txt_password;
    String txt_full_name;

    private FirebaseAuth mfirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        full_name = findViewById(R.id.name);

        mfirebaseAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_email = email.getText().toString();
                txt_password = password.getText().toString();
                txt_full_name = full_name.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_full_name)){
                    Toast.makeText(RegisterActivity.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this , RegisterActivity.class));
                    finish();
                } else if (txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                } else {
                    checkEmail();
                }
            }
        });
    }

    private void checkEmail(){
        mfirebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Boolean check = !task.getResult().getSignInMethods().isEmpty();

                if(check){
                    Log.d("debug","email already present");
                    loginUser(email.getText().toString(),password.getText().toString());
                }
                else{
                    Log.d("debug","email not present");
                    saveData();
                    startActivity(new Intent(RegisterActivity.this , Register2Activity.class));
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mfirebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("debug","login successfull");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void registerUser(String email, String password) {
        mfirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registering user successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this , Register2Activity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
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