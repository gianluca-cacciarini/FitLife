package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private User user;

    private TextView debug;
    private String debug_txt = "";

    private Button logout;
    private EditText edit;
    private Button add;
    private ListView listView;
    private ArrayList<Food> list;

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("debug","MainActivity");


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images");

        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
        edit = findViewById(R.id.edit);
        add = findViewById(R.id.add);
        listView = findViewById(R.id.listView);
        debug = findViewById(R.id.debug_main);
        debug.setText("DEBUG");
        image = findViewById(R.id.imageView);
        getUser();


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

        list = new ArrayList<Food>();
        ArrayAdapter adapter = new ArrayAdapter<Food>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        DatabaseReference FoodRef = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Food");
        FoodRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Food f = snapshot.getValue(Food.class);
                //list.add(f);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getAllImages(){
        mStorageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for( StorageReference fileref: listResult.getItems()){
                    debug_txt += fileref.getName() + "\n";
                    debug.setText(debug_txt);
                    addImage(fileref.getName());
                }
            }
        });
    }

    public void addImage(String image){
        mStorageReference.child(image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri image_url = task.getResult();
                String[] l = image.replace(".jpg","").split("-");
                Food f = new Food(l[0],"none",Integer.parseInt(l[1]),Integer.parseInt(l[2]),Integer.parseInt(l[3]),Integer.parseInt(l[4]),image_url.toString());
                user.addFood(f);
                mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user);
            }
        });
    }

    public void getUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                user = snapshot.getValue(User.class);
                getAllImages();
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