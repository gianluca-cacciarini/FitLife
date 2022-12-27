package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private RecyclerView recyclerViewDiary;
    private MyAdapterDiary myAdapterDiary;
    private HashMap<String,Food> food_list = new HashMap<String,Food>();
    private HashMap<String,Exercise> exercise_list = new HashMap<String,Exercise>();
    private ArrayList<Day> diary = new ArrayList<Day>();

    private User user;

    private BottomNavigationView navigationView;

    private Button test_button;
    private String currentDay;

    private TextView total_carb_text, total_prot_text, total_fat_text, total_cal_text;
    private int total_carb, total_prot, total_fat, total_cal;

    private Dialog add_dialog;
    private FloatingActionButton add_button;
    private Button dialog_close,dialog_ok;
    private ImageView dialog_food_btn,dialog_exercise_btn;
    private Integer dialog_filter = 0;
    private RecyclerView recyclerViewDialog;
    private MyAdapterDiaryDialog myAdapterDiaryDialog;
    private ArrayList<Food> dialog_food_list = new ArrayList<Food>();
    private ArrayList<Exercise> dialog_exercise_list = new ArrayList<Exercise>();
    private ArrayList<Food> diary_food_list = new ArrayList<Food>();
    private ArrayList<Exercise> diary_exercise_list = new ArrayList<Exercise>();

    private Button calendar;

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
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        currentDay = df.format(currentTime);
        calendar = findViewById(R.id.calendarbutton);
        total_carb_text = findViewById(R.id.total_carb_txt);
        total_prot_text = findViewById(R.id.total_prot_txt);
        total_fat_text = findViewById(R.id.total_fat_txt);
        total_cal_text = findViewById(R.id.total_cal_txt);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images");
        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        getUser();

        recyclerViewDiary = findViewById(R.id.diary_view);
        recyclerViewDiary.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        food_list = new HashMap<String,Food>();
        exercise_list = new HashMap<String,Exercise>();
        diary = new ArrayList<Day>();
        myAdapterDiary = new MyAdapterDiary(getApplicationContext(),food_list,exercise_list,diary);
        recyclerViewDiary.setAdapter(myAdapterDiary);

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

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "date picker");
            }
        });
        calendar.setText(currentDay);

        add_dialog =new Dialog(this);
        add_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        add_dialog.setCanceledOnTouchOutside(true);
        add_dialog.setContentView(R.layout.diary_dialog_layout);
        dialog_ok = add_dialog.findViewById(R.id.dialog_ok_btn);
        dialog_close = add_dialog.findViewById(R.id.dialog_close_btn);
        dialog_food_btn = add_dialog.findViewById(R.id.dialog_food_imgview);
        dialog_exercise_btn = add_dialog.findViewById(R.id.dialog_exercise_imgview);

        recyclerViewDialog = add_dialog.findViewById(R.id.dialog_recycleview);
        recyclerViewDialog.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myAdapterDiaryDialog = new MyAdapterDiaryDialog(getApplicationContext(),dialog_food_list,dialog_exercise_list,dialog_filter);
        recyclerViewDialog.setAdapter(myAdapterDiaryDialog);

        add_button = findViewById(R.id.add_diary_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debug","dialog");
                buildDialogRecyclerView();
                add_dialog.show();
            }
        });

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_dialog.dismiss();
            }
        });

        dialog_food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_filter = 0;
                setDialogFilter();
            }
        });

        dialog_exercise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_filter = 1;
                setDialogFilter();
            }
        });

    }

    public void buildDialogRecyclerView(){
        if( dialog_food_list.size() != food_list.size()){
            dialog_food_list.clear();
            for( String key : food_list.keySet()){
                Toast.makeText(this, key+" "+user.getDiary().get(currentDay+key), Toast.LENGTH_SHORT).show();
                if(user.getDiary().get(currentDay+key)!=null){
                    //dialog_food_list.add(food_list.get(key));
                }
                else {
                    dialog_food_list.add(food_list.get(key));
                }
            }
            Collections.sort(dialog_food_list);
        }
        if(dialog_exercise_list.size() != exercise_list.size()){
            dialog_exercise_list.clear();
            for( String key : exercise_list.keySet()){
                Toast.makeText(this, key+" "+user.getDiary().get(currentDay+key), Toast.LENGTH_SHORT).show();
                if(user.getDiary().get(currentDay+key)!=null){
                    //dialog_exercise_list.add(exercise_list.get(key));
                }
                else {
                    dialog_exercise_list.add(exercise_list.get(key));
                }
            }
            Collections.sort(dialog_exercise_list);
        }
        myAdapterDiaryDialog = new MyAdapterDiaryDialog(getApplicationContext(),dialog_food_list,dialog_exercise_list,dialog_filter);
        recyclerViewDialog.setAdapter(myAdapterDiaryDialog);

        myAdapterDiaryDialog.setOnItemClickListener(new MyAdapterDiaryDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if(dialog_filter==0){
                    Food f = dialog_food_list.get(position);
                    Toast.makeText(MainActivity.this, String.valueOf(position)+" "+f.getName(), Toast.LENGTH_SHORT).show();
                    Day new_d = new Day(currentDay,f.getName(),100);
                    //user.addDay(new_d);
                    //updateUser();
                }
                else{
                    Exercise e = dialog_exercise_list.get(position);
                    Toast.makeText(MainActivity.this, String.valueOf(position)+" "+e.getName(), Toast.LENGTH_SHORT).show();
                    Day new_d = new Day(currentDay,e.getName(),1,2);
                    //user.addDay(new_d);
                    //updateUser();
                }
            }

            @Override
            public void onQuantityClick(int position, MyAdapterDiaryDialog.MyViewHolder v) {
                Toast.makeText(MainActivity.this, v.name.getText().toString()+" "+v.number.getText().toString(), Toast.LENGTH_SHORT).show();
                if(dialog_filter==0){
                    Food f = dialog_food_list.get(position);
                    if(v.number.getText().toString().equals("") || v.number.getText().toString().equals("0")) return;
                    Day new_d = new Day(currentDay,f.getName(),Integer.parseInt(v.number.getText().toString()));
                    user.addDay(new_d);
                    updateUser();
                }
                else{
                    Exercise e = dialog_exercise_list.get(position);
                    if(v.number.getText().toString().equals("") || v.number.getText().toString().equals("0")) return;
                    Day new_d = new Day(currentDay,e.getName(),1,2);
                    user.addDay(new_d);
                    updateUser();
                }
            }

        });
    }

    public void buildRecyclerWiew(){
        diary_food_list.clear();
        for( String key : user.getDiary().keySet()){
            if (user.getDiary().get(key).getDate().equals(currentDay) && user.getDiary().get(key).getOr()==0){
                String foodname = user.getDiary().get(key).getFood_name();
                diary_food_list.add(food_list.get(foodname));
            }
        }
        Collections.sort(diary_food_list);

        diary_exercise_list.clear();
        for( String key : user.getDiary().keySet()){
            if (user.getDiary().get(key).getDate().equals(currentDay) && user.getDiary().get(key).getOr()==1){
                String exercisename = user.getDiary().get(key).getExercise_name();
                diary_exercise_list.add(exercise_list.get(exercisename));
            }
        }
        Collections.sort(diary_food_list);

        myAdapterDiary = new MyAdapterDiary(getApplicationContext(),food_list,exercise_list,diary);
        recyclerViewDiary.setAdapter(myAdapterDiary);

        myAdapterDiary.setOnItemClickListener(new MyAdapterDiary.OnItemClickListener() {
            @Override
            public void onItemClick(int position, MyAdapterDiary.MyViewHolder v) {
                if(position<diary_food_list.size()){
                    Food f = diary_food_list.get(position);
                    Toast.makeText(MainActivity.this, String.valueOf(position)+" "+f.getName(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Exercise e = diary_exercise_list.get(position-diary_food_list.size());
                    Toast.makeText(MainActivity.this, String.valueOf(position-diary_food_list.size())+" "+e.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(int position, MyAdapterDiary.MyViewHolder v) {
                if(position<diary_food_list.size()){
                    Food f = diary_food_list.get(position);
                    Toast.makeText(MainActivity.this, String.valueOf(position)+" "+f.getName()+" DELETE", Toast.LENGTH_SHORT).show();
                    Day old_d = new Day(currentDay,f.getName(),Integer.parseInt(v.quant.getText().toString()));
                    user.deleteDay(old_d);
                    updateMacro(old_d,-1);
                    updateUser();
                }
                else{
                    Exercise e = diary_exercise_list.get(position-diary_food_list.size());
                    Toast.makeText(MainActivity.this, String.valueOf(position-diary_food_list.size())+" "+e.getName()+" DELETE", Toast.LENGTH_SHORT).show();
                    Day old_d = new Day(currentDay,e.getName(),1,2);
                    user.deleteDay(old_d);
                    updateMacro(old_d,-1);
                    updateUser();
                }
            }

        });
    }

    public void setDialogFilter(){
        if (dialog_filter==0) {
            //dialog_food_btn.setBackgroundColor(getResources().getColor(R.color.celeste));
            //dialog_exercise_btn.setBackgroundColor(getResources().getColor(R.color.dialog));
            dialog_food_btn.setColorFilter(getResources().getColor(R.color.celeste));
            dialog_exercise_btn.setColorFilter(getResources().getColor(R.color.black));
            buildDialogRecyclerView();
        }
        else {
            //dialog_exercise_btn.setBackgroundColor(getResources().getColor(R.color.celeste));
            //dialog_food_btn.setBackgroundColor(getResources().getColor(R.color.dialog));
            dialog_exercise_btn.setColorFilter(getResources().getColor(R.color.celeste));
            dialog_food_btn.setColorFilter(getResources().getColor(R.color.black));
            buildDialogRecyclerView();
        }
    }

    public void updateMacro(Day day,int i){
        Food food_tmp = food_list.get(day.getFood_name());
        int q,c,p,f,k;
        if(food_tmp==null) return;
        q = day.getQuantity();
        c = food_tmp.getCarb();
        p = food_tmp.getProt();
        f = food_tmp.getFat();
        k = food_tmp.getCal();

        if(i>0){
            total_carb += q*c;
            total_prot += q*p;
            total_fat += q*f;
            total_cal += q*k;
        }
        else{
            total_carb -= q*c;
            total_prot -= q*p;
            total_fat -= q*f;
            total_cal -= q*k;
        }
        total_carb_text.setText("carb: "+String.valueOf(total_carb));
        total_prot_text.setText("prot: "+String.valueOf(total_prot));
        total_fat_text.setText("fat: "+String.valueOf(total_fat));
        total_cal_text.setText("cal: "+String.valueOf(total_cal));

    }

    public void insertDiary(){
        diary.clear();
        total_carb = 0; total_prot = 0; total_fat = 0; total_cal = 0;
        total_carb_text.setText("0");
        total_prot_text.setText("0");
        total_fat_text.setText("0");
        total_cal_text.setText("0");

        for( String key : user.getDiary().keySet()){
            //Log.d("debug",currentDay+" vs "+user.getDiary().get(key).getDate());
            if(user.getDiary().get(key).getDate().equals(currentDay)) {
                diary.add(user.getDiary().get(key));
                updateMacro(user.getDiary().get(key),1);
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
                buildRecyclerWiew();
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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH, i1);
        c.set(Calendar.DAY_OF_MONTH, i2);
        SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        currentDay = df.format(c.getTime());
        calendar.setText(currentDay);
        insertDiary();
        buildRecyclerWiew();

    }
}
