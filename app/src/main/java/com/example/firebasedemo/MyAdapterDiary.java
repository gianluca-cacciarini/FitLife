package com.example.firebasedemo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyAdapterDiary extends RecyclerView.Adapter<MyAdapterDiary.MyViewHolder>{

    Context context;
    HashMap<String,Food> food_list;
    HashMap<String,Exercise> exercise_list;
    ArrayList<Day> diary;

    @NonNull
    @Override
    public MyAdapterDiary.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.diary_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDiary.MyViewHolder holder, int position) {
        Log.d("debug","position: "+String.valueOf(position));
        Day day = diary.get(position);
        if(day.getOr()==0){
            holder.layout.setBackgroundColor(Color.BLUE);
            Food food = food_list.get(day.getFood_name());
            holder.name.setText(food.getName());
            holder.category.setText(food.getCategory());
            holder.carb.setText(String.valueOf(food.getCarb()));
            holder.prot.setText(String.valueOf(food.getProt()));
            holder.fat.setText(String.valueOf(food.getFat()));
            holder.cal.setText(String.valueOf(food.getCal()));
            Glide.with(context).load(Uri.parse(food.getImageurl())).into(holder.image);
            holder.quant.setText(String.valueOf(day.getQuantity()));

            holder.set.setVisibility(View.INVISIBLE);
            holder.rep.setVisibility(View.INVISIBLE);
        }
        else{
            holder.layout.setBackgroundColor(Color.GREEN);
            Exercise exercise = exercise_list.get(day.getExercise_name());
            holder.name.setText(exercise.getName());
            holder.category.setText(exercise.getCategory());
            Glide.with(context).load(Uri.parse(exercise.getImageurl())).into(holder.image);
            holder.set.setText(String.valueOf(day.getSet()));
            holder.rep.setText(String.valueOf((day.getRep())));

            holder.quant.setVisibility(View.INVISIBLE);
            holder.carb.setVisibility(View.INVISIBLE);
            holder.prot.setVisibility(View.INVISIBLE);
            holder.fat.setVisibility(View.INVISIBLE);
            holder.cal.setVisibility(View.INVISIBLE);
        }
        /*
        if(position<food_list.size()) {
            Log.d("debug","food item");
            holder.layout.setBackgroundColor(Color.BLUE);
            Food food = food_list.get(position);
            holder.name.setText(food.getName());
            holder.category.setText(food.getCategory());
            holder.carb.setText(String.valueOf(food.getCarb()));
            holder.prot.setText(String.valueOf(food.getProt()));
            holder.fat.setText(String.valueOf(food.getFat()));
            holder.cal.setText(String.valueOf(food.getCal()));
            Glide.with(context).load(Uri.parse(food.getImageurl())).into(holder.image);
            //holder.image.setImageURI(Uri.parse(food.getImageurl()));
            //Toast.makeText(context, "myadapter "+food.getName(), Toast.LENGTH_SHORT).show();

            holder.set.setVisibility(View.INVISIBLE);
            holder.rep.setVisibility(View.INVISIBLE);
        }
        else{
            Log.d("debug","exercise item");
            holder.layout.setBackgroundColor(Color.GREEN);
            Exercise exercise = exercise_list.get(position-food_list.size());
            holder.name.setText(exercise.getName());
            holder.category.setText(exercise.getCategory());
            Glide.with(context).load(Uri.parse(exercise.getImageurl())).into(holder.image);

            holder.carb.setVisibility(View.INVISIBLE);
            holder.prot.setVisibility(View.INVISIBLE);
            holder.fat.setVisibility(View.INVISIBLE);
            holder.cal.setVisibility(View.INVISIBLE);
        }

         */
    }

    @Override
    public int getItemCount() {
        return diary.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,category;
        TextView carb,prot,fat,cal;
        TextView set,rep,quant;
        ImageView image;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameDiary);
            category = itemView.findViewById(R.id.categoryDiary);
            carb = itemView.findViewById(R.id.carbDiary);
            prot = itemView.findViewById(R.id.protDiary);
            fat = itemView.findViewById(R.id.fatDiary);
            cal = itemView.findViewById(R.id.calDiary);
            image = itemView.findViewById(R.id.imageDiary);
            set = itemView.findViewById(R.id.setDiary);
            rep = itemView.findViewById(R.id.repDiary);
            quant = itemView.findViewById(R.id.testA);
            layout = itemView.findViewById(R.id.diary_item_layout);

        }
    }

    public MyAdapterDiary(Context context, HashMap<String,Food> food_list, HashMap<String,Exercise> exercise_list, ArrayList<Day> diary) {
        this.context = context;
        this.food_list = food_list;
        this.exercise_list = exercise_list;
        this.diary = diary;
    }
}
