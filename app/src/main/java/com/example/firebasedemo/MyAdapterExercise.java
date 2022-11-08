package com.example.firebasedemo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapterExercise extends RecyclerView.Adapter<MyAdapterExercise.MyViewHolder>{

    Context context;
    ArrayList<Exercise> exercise_list;

    @NonNull
    @Override
    public MyAdapterExercise.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.exercise_item,parent,false);
        return new MyAdapterExercise.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterExercise.MyViewHolder holder, int position) {
        Exercise exercise = exercise_list.get(position);
        holder.name.setText(exercise.getName());
        holder.category.setText(exercise.getCategory());
        Glide.with(context).load(Uri.parse(exercise.getImageurl())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return exercise_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,category;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameExercise);
            category = itemView.findViewById(R.id.categoryDiary);
            image = itemView.findViewById(R.id.imageExercise);
        }
    }

    public MyAdapterExercise(Context applicationContext, ArrayList<Exercise> exercise_list) {
        this.context = applicationContext;
        this.exercise_list = exercise_list;
    }

}
