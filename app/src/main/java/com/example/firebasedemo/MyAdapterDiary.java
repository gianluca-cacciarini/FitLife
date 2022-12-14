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

    MyAdapterDiary.OnItemClickListener mlistener;
    Context context;
    HashMap<String,Food> food_list;
    HashMap<String,Exercise> exercise_list;
    ArrayList<Day> diary;

    public interface OnItemClickListener {
        void onItemClick(int position, MyAdapterDiary.MyViewHolder v);
        void onDeleteClick(int position, MyAdapterDiary.MyViewHolder v);
    }

    public void setOnItemClickListener(MyAdapterDiary.OnItemClickListener listener){
        mlistener = listener;
    }

    @NonNull
    @Override
    public MyAdapterDiary.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.diary_item,parent,false);
        return new MyAdapterDiary.MyViewHolder(view,mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDiary.MyViewHolder holder, int position) {
        Log.d("debug","position: "+String.valueOf(position));

        Day day = diary.get(position);
        holder.or = day.getOr();
        if(day.getOr()==0){
            holder.layout.setBackgroundColor(Color.rgb(235, 252, 251));
            Food food = food_list.get(day.getFood_name());
            holder.name.setText(food.getName().substring(0, 1).toUpperCase() + food.getName().substring(1));
            holder.g.setVisibility(View.VISIBLE);
            holder.category.setText(food.getCategory().substring(0, 1).toUpperCase() + food.getCategory().substring(1));
            int i1 = (food.getCarb()*day.getQuantity())/100;
            int i2 = (food.getProt()*day.getQuantity())/100;
            int i3 = (food.getFat()*day.getQuantity())/100;
            int i4 = (food.getCal()*day.getQuantity())/100;
            holder.carb.setText("C: " + String.valueOf(i1));
            holder.prot.setText("P: " +String.valueOf(i2));
            holder.fat.setText("F: " +String.valueOf(i3));
            holder.cal.setText("cal: " +String.valueOf(i4));
            Glide.with(context).load(Uri.parse(food.getImageurl())).into(holder.image);
            holder.quant.setText(String.valueOf(day.getQuantity()));
            holder.delete.setColorFilter(Color.GRAY);
            holder.delete.setVisibility(View.VISIBLE);


            holder.set.setVisibility(View.INVISIBLE);
            holder.rep.setVisibility(View.INVISIBLE);
            holder.cross.setVisibility(View.INVISIBLE);
        }
        else{
            holder.layout.setBackgroundColor(Color.rgb(235, 252, 251));
            Exercise exercise = exercise_list.get(day.getExercise_name());
            holder.name.setText(exercise.getName());
            holder.category.setText(exercise.getCategory());
            Glide.with(context).load(Uri.parse(exercise.getImageurl())).into(holder.image);
            holder.set.setText(String.valueOf(day.getSet()));
            holder.cross.setVisibility(View.VISIBLE);
            holder.rep.setText(String.valueOf((day.getRep())));

            holder.delete.setColorFilter(Color.GRAY);
            holder.delete.setVisibility(View.VISIBLE);

            holder.quant.setVisibility(View.INVISIBLE);
            holder.g.setVisibility(View.INVISIBLE);
            holder.carb.setVisibility(View.INVISIBLE);
            holder.prot.setVisibility(View.INVISIBLE);
            holder.fat.setVisibility(View.INVISIBLE);
            holder.cal.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return diary.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,category;
        TextView carb,prot,fat,cal, cross, g;
        TextView set,rep,quant;
        ImageView image, delete;
        ConstraintLayout layout;
        int or;
        MyAdapterDiary.MyViewHolder v = this;

        public MyViewHolder(@NonNull View itemView, MyAdapterDiary.OnItemClickListener listener) {
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
            delete = itemView.findViewById(R.id.delete_item_diary);
            cross = itemView.findViewById(R.id.crossX);
            g = itemView.findViewById(R.id.testA2);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mlistener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mlistener.onDeleteClick(position,v);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mlistener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mlistener.onItemClick(position,v);
                        }
                    }
                }
            });

        }
    }

    public MyAdapterDiary(Context context, HashMap<String,Food> food_list, HashMap<String,Exercise> exercise_list, ArrayList<Day> diary) {
        this.context = context;
        this.food_list = food_list;
        this.exercise_list = exercise_list;
        this.diary = diary;
    }

}
