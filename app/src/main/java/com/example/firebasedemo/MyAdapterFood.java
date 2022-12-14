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

public class MyAdapterFood extends RecyclerView.Adapter<MyAdapterFood.MyViewHolder>{

    Context context;
    ArrayList<Food> food_list;

    @NonNull
    @Override
    public MyAdapterFood.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.food_item,parent,false);
        return new MyAdapterFood.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterFood.MyViewHolder holder, int position) {
        Food food = food_list.get(position);
        holder.name.setText(food.getName().substring(0, 1).toUpperCase() + food.getName().substring(1));
        holder.category.setText(food.getCategory().substring(0, 1).toUpperCase() + food.getCategory().substring(1));
        holder.carb.setText("C: " + String.valueOf(food.getCarb()));
        holder.prot.setText("P: " +String.valueOf(food.getProt()));
        holder.fat.setText("F: " +String.valueOf(food.getFat()));
        holder.cal.setText("cal: " +String.valueOf(food.getCal()));
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

            name = itemView.findViewById(R.id.nameFood);
            category = itemView.findViewById(R.id.categoryFood);
            carb = itemView.findViewById(R.id.carbFood);
            prot = itemView.findViewById(R.id.protFood);
            fat = itemView.findViewById(R.id.fatFood);
            cal = itemView.findViewById(R.id.calFood);
            image = itemView.findViewById(R.id.imageFood);

        }
    }

    public MyAdapterFood(Context context, ArrayList<Food> food_list) {
        this.context = context;
        this.food_list = food_list;
    }
}
