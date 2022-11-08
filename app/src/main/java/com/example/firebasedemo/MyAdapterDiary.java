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

public class MyAdapterDiary extends RecyclerView.Adapter<MyAdapterDiary.MyViewHolder>{

    Context context;
    ArrayList<Food> food_list;

    @NonNull
    @Override
    public MyAdapterDiary.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.diary_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDiary.MyViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return food_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,category,carb,prot,fat,cal;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameExercise);
            category = itemView.findViewById(R.id.categoryDiary);
            carb = itemView.findViewById(R.id.carbDiary);
            prot = itemView.findViewById(R.id.protDiary);
            fat = itemView.findViewById(R.id.fatDiary);
            cal = itemView.findViewById(R.id.calDiary);
            image = itemView.findViewById(R.id.imageExercise);


        }
    }

    public MyAdapterDiary(Context context, ArrayList<Food> food_list) {
        this.context = context;
        this.food_list = food_list;
    }
}
