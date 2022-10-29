package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private Button logout, add;
    private EditText edit;
    private ListView listView;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
        edit = findViewById(R.id.edit);
        add = findViewById(R.id.add);
        listView = findViewById(R.id.listView);

        /*mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        //HashMap<String, Object> map = new HashMap<>();
        //map.put("Name", "Gianluca");
        //map.put("Email", "prova123@gmail1.com");
        //FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Utente1").child("Name").setValue("Marco");
        //FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Utente1").child("Name").removeValue();
        //FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Languages").child("n1").setValue("Java");
        //FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Languages").child("n2").setValue("Kotlin");
        //FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Languages").child("n3").setValue("Flutter");
        //FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Languages").child("n4").setValue("React Native");

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Information");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Information info = snapshot.getValue(Information.class);
                    String txt = info.getName() + " : " + info.getEmail();
                    list.add(txt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void signOut(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getApplicationContext(), snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                String user_json = snapshot.getValue().toString();
                Gson gson = new Gson();
                Type type = new TypeToken<User>() {}.getType();
                User current_user = gson.fromJson(user_json,type);
                if (current_user.password.equals("google_user")) GoogleSignOut();
                if (current_user.password.equals("default_user")) DefaultSignOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DefaultSignOut(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

    private void GoogleSignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // When task is successful
                    // Sign out from firebase
                    mFirebaseAuth.signOut();

                    // Display Toast
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                }
            }
        });
    }
}