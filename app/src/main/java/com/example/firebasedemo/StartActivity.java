package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    private Button register, login;
    private ImageView google_img;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private String txt_email, txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mfirebaseAuth =FirebaseAuth.getInstance();

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        google_img=findViewById(R.id.google);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("333832431832-gmajdqe6922lfmo44q90anipp982njgb.apps.googleusercontent.com")
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent( StartActivity.this , RegisterActivity.class));
               finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( StartActivity.this , LoginActivity.class));
                finish();
            }
        });

        FirebaseUser firebaseUser= mfirebaseAuth.getCurrentUser();
        if(firebaseUser!=null) {
            startActivity(new Intent(StartActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
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
                    //now i need to check if the user exist in my database, because if he/she its not present i need to add him/her
                    //so i try to get their values
                    mfirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            txt_email = mfirebaseAuth.getCurrentUser().getEmail();
                            txt_password = "google_account";
                            saveData();
                            checkUser();
                        }
                    });

                    //HomeActivity();
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                Log.d("myapp",e.toString());
            }
        }
    }

    private void checkUser() {
        mDatabaseReference.child(mfirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if snapshot exist then it means that he/she is already in the database
                if (snapshot.exists()){
                    startActivity( new Intent(getApplicationContext(),MainActivity.class));
                }
                //then this mean he/she is not already in the database so we need to get the info and the add
                else{
                    startActivity( new Intent(getApplicationContext(), RegisterGoogleActivity.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void HomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
}
