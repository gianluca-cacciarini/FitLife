package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartActivity extends AppCompatActivity {

    private Button register;
    private Button login;

    private ImageView google_img;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.d("debug","StartActivity");

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        Log.d("debug","StartActivity 1");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent( StartActivity.this , RegisterActivity.class));
               finish();
            }
        });

        Log.d("debug","StartActivity 2");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( StartActivity.this , LoginActivity.class));
                finish();
            }
        });

        Log.d("debug","StartActivity 3");
        // Initialize firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        // Initialize firebase user

        Log.d("debug","StartActivity 4");
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null) {
            // When user already sign in
            // redirect to profile activity
            Log.d("debug","StartActivity 5");
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        Log.d("debug","StartActivity 6");
    }

    private void SignIn() {

        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()) {
                Toast.makeText(this, "Log in successful!", Toast.LENGTH_SHORT).show();
            }
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if(googleSignInAccount!=null) {
                    // When sign in account is not equal to null
                    // Initialize auth credential
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    // Check credential
                    firebaseAuth.signInWithCredential(authCredential);
                    //TODO ordine signin insert data
                    Log.d("debug","StartActivity 7");
                    startActivity( new Intent(getApplicationContext(),Register2Activity.class));
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                Log.d("myapp",e.toString());
            }
        }
    }
}
