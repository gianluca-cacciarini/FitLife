package com.example.firebasedemo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapterDiaryDialog extends RecyclerView.Adapter<MyAdapterDiaryDialog.MyViewHolder>{

    OnItemClickListener mlistener;
    Context context;
    ArrayList<Food> food_list;
    ArrayList<Exercise> exercise_list;
    Integer filter;

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
        void onQuantityClick(int position, MyViewHolder v);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    @NonNull
    @Override
    public MyAdapterDiaryDialog.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_item,parent,false);
        return new MyAdapterDiaryDialog.MyViewHolder(view,mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDiaryDialog.MyViewHolder holder, int position) {
        Log.d("debug","position: "+String.valueOf(position));
        if(this.filter==0){
            holder.layout.setBackgroundColor(Color.GRAY);
            Food food = this.food_list.get(position);
            holder.name.setText(food.getName());
            holder.category.setText(food.getCategory());
            holder.carb.setText(String.valueOf(food.getCarb()));
            holder.prot.setText(String.valueOf(food.getProt()));
            holder.fat.setText(String.valueOf(food.getFat()));
            holder.cal.setText(String.valueOf(food.getCal()));
            Glide.with(context).load(Uri.parse(food.getImageurl())).into(holder.image);

            holder.set.setVisibility(View.INVISIBLE);
            holder.rep.setVisibility(View.INVISIBLE);
        }
        else{
            holder.layout.setBackgroundColor(Color.GRAY);
            Exercise exercise = exercise_list.get(position);
            holder.name.setText(exercise.getName());
            holder.category.setText(exercise.getCategory());
            Glide.with(context).load(Uri.parse(exercise.getImageurl())).into(holder.image);

            //holder.quant.setVisibility(View.INVISIBLE);
            holder.carb.setVisibility(View.INVISIBLE);
            holder.prot.setVisibility(View.INVISIBLE);
            holder.fat.setVisibility(View.INVISIBLE);
            holder.cal.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(this.filter==0){
            return food_list.size();
        }
        else return exercise_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,category;
        TextView carb,prot,fat,cal;
        TextView set,rep,quant;
        ImageView image;
        ConstraintLayout layout;
        EditText number;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.nameDialog);
            category = itemView.findViewById(R.id.categoryDialog);
            carb = itemView.findViewById(R.id.carbDialog);
            prot = itemView.findViewById(R.id.protDialog);
            fat = itemView.findViewById(R.id.fatDialog);
            cal = itemView.findViewById(R.id.calDialog);
            image = itemView.findViewById(R.id.imageDialog);
            set = itemView.findViewById(R.id.setDialog);
            rep = itemView.findViewById(R.id.repDialog);
            number = itemView.findViewById(R.id.numberDialog);
            //quant = itemView.findViewById(R.id.testA);
            layout = itemView.findViewById(R.id.dialog_item_layout);
            MyViewHolder v = this;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mlistener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mlistener.onItemClick(position,itemView);
                        }
                    }
                }
            });

            number.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //nulla
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //nulla
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mlistener.onQuantityClick(position,v);
                    }
                }
            });

        }

    }

    public MyAdapterDiaryDialog(Context context, ArrayList<Food> food_list, ArrayList<Exercise> exercise_list, Integer filter) {
        this.context = context;
        this.food_list = food_list;
        this.exercise_list = exercise_list;
        this.filter = filter;
    }

}
