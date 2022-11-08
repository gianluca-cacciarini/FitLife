package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PedometerPageActivity extends AppCompatActivity implements SensorEventListener {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private SensorManager sensorManager;
    private Sensor countSensor;

    private User user;

    private BottomNavigationView navigationView;
    private TextView day_steps, week_step, month_step, date_text;
    private int current_steps =0;
    private int increment=0;
    private int prev=0;
    private String currentDay, lastDay;

    @Override
    protected void onResume() {
        super.onResume();
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor!=null){
            sensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_UI);
            Toast.makeText(this, "sensor OK", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "sensor not found", Toast.LENGTH_SHORT).show();
        }
        loadData();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer_page);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        mDatabaseReference = FirebaseDatabase.getInstance("https://fir-demo-5bf06-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("my_app_user");
        mFirebaseAuth = FirebaseAuth.getInstance();

        //SENSOR
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //DISPLAY
        date_text = findViewById(R.id.date);
        day_steps = findViewById(R.id.step_counter_day);
        week_step = findViewById(R.id.step_counter_week);
        month_step = findViewById(R.id.step_counter_month);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        currentDay = df.format(currentTime);
        date_text.setText(currentDay);

        loadData();
        getUser();

        navigationView = findViewById(R.id.navigation_bar);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("debug","clicked");
                switch (item.getItemId()){

                    case R.id.bottom_diary:
                        Toast.makeText(getApplicationContext(),"diary",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
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
                        //do nothing im already in the stepcounter page
                        return true;
                    case R.id.bottom_user:
                        Toast.makeText(getApplicationContext(),"user",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),UserPageActivity.class));
                        return true;
                }
                return true;
            }
        });



    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.e("debug: ", currentDay+"   "+lastDay+" "+String.valueOf(current_steps));

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        currentDay = df.format(currentTime);
        date_text.setText(currentDay);

        //case where this is the first time i open the app so i dont have any info on the previous day...
        if(!currentDay.equals(lastDay) && lastDay.equals("none")){
            lastDay=currentDay;
            current_steps = 0;
            prev = (int) sensorEvent.values[0];
        }
        //case where this isn't the first i open the app, but it is the first
        //time i open the app today
        else if(!currentDay.equals(lastDay) && !lastDay.equals("none")){
            current_steps = 0;
            prev = (int) sensorEvent.values[0];
            lastDay = currentDay;
        }
        //case where it's the the second time i open the app today
        else {
            int aux = (int) sensorEvent.values[0];
            increment = aux - prev;
            current_steps += increment;
            prev = aux;
        }
        day_steps.setText(String.valueOf(current_steps));
        Step step = new Step(currentDay,current_steps);
        saveData();
        if(user!=null) {
            user.addStep(step);
            updateUser();
            getWeekSteps();
            getMonthSteps();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void getUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                user = snapshot.getValue(User.class);
            }
        });
    }

    public void updateUser(){
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("step_list").setValue(user.getStep_list()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("debug","update completed");
            }
        });
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("ALL_ACTIVITY", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("STEPS", current_steps);
        editor.putInt("PREV_COUNTER",prev);
        editor.putString("CURRENT_DAY",currentDay);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("ALL_ACTIVITY", MODE_PRIVATE);
        current_steps = sharedPreferences.getInt("STEPS",0);
        prev = sharedPreferences.getInt("PREV_COUNTER", current_steps);
        lastDay = sharedPreferences.getString("CURRENT_DAY","none");
    }

    public void getWeekSteps(){
        int sum = 0;
        for( String key : user.step_list.keySet()){
            Step s = user.step_list.get(key);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
            try {
                Date date1 = myFormat.parse(s.getDate());
                Date date2 = myFormat.parse(currentDay);
                long duration  = date2.getTime() - date1.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
                //week_step.setText(String.valueOf(diffInDays));
                if( diffInDays<=7 ){
                    sum += s.getStep();
                    week_step.setText(String.valueOf(sum));
                }
            }catch (Exception e){

            }
        }
    }

    public void getMonthSteps(){
        int sum = 0;
        for( String key : user.step_list.keySet()){
            Step s = user.step_list.get(key);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
            try {
                Date date1 = myFormat.parse(s.getDate());
                Date date2 = myFormat.parse(currentDay);
                long duration  = date2.getTime() - date1.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
                //week_step.setText(String.valueOf(diffInDays));
                if( diffInDays<=30 ){
                    sum += s.getStep();
                    month_step.setText(String.valueOf(sum));
                }
            }catch (Exception e){

            }
        }
    }

}