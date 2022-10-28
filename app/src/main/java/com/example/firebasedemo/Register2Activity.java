package com.example.firebasedemo;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Register2Activity extends AppCompatActivity {

    private Button done;
    private EditText carb_goal;
    private EditText fat_goal;
    private EditText prot_goal;
    private EditText cal_goal;
    private CheckBox male;
    private CheckBox female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        done = findViewById(R.id.done);
        carb_goal = findViewById(R.id.carbo_goal);
        fat_goal = findViewById(R.id.fat_goal);
        cal_goal = findViewById(R.id.cal_goal);
        prot_goal = findViewById(R.id.prot_goal);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int carb_num = parseInt(carb_goal.getText().toString());
                int fat_num = parseInt(fat_goal.getText().toString());
                int cal_num = parseInt(cal_goal.getText().toString());
                int prot_num = parseInt(prot_goal.getText().toString());

            }
        });
    }
}