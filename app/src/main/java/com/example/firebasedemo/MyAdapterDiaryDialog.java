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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapterDiaryDialog extends RecyclerView.Adapter<MyAdapterDiaryDialog.MyViewHolder>{

    OnItemClickListener mlistener;
    Context context;
    ArrayList<Food> food_list;
    ArrayList<Exercise> exercise_list;
    Integer filter;

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
        void onQuantityClick(int position, MyViewHolder v);
        void onSetClick(int position, MyViewHolder v);
        void onRepClick(int position, MyViewHolder v);
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
            holder.name.setText(food.getName().substring(0, 1).toUpperCase() + food.getName().substring(1));
            holder.category.setText(food.getCategory().substring(0, 1).toUpperCase() + food.getCategory().substring(1));
            holder.carb.setText("C: " + String.valueOf(food.getCarb()));
            holder.prot.setText("P: " +String.valueOf(food.getProt()));
            holder.fat.setText("F: " +String.valueOf(food.getFat()));
            holder.cal.setText("cal: " +String.valueOf(food.getCal()));
            Glide.with(context).load(Uri.parse(food.getImageurl())).into(holder.image);

            holder.setText.setVisibility(View.INVISIBLE);
            holder.repText.setVisibility(View.INVISIBLE);
            holder.setNumber.setVisibility(View.INVISIBLE);
            holder.repNumber.setVisibility(View.INVISIBLE);
            holder.setNumber.setClickable(false);
            holder.repNumber.setClickable(false);
            holder.gText.setVisibility(View.VISIBLE);
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
            holder.gText.setVisibility(View.INVISIBLE);
            holder.quantityNumber.setClickable(false);
            holder.quantityNumber.setVisibility(View.INVISIBLE);
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
        TextView setText, repText,gText;
        ImageView image;
        ConstraintLayout layout;
        EditText quantityNumber,setNumber,repNumber;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.nameDialog);
            category = itemView.findViewById(R.id.categoryDialog);
            carb = itemView.findViewById(R.id.carbDialog);
            prot = itemView.findViewById(R.id.protDialog);
            fat = itemView.findViewById(R.id.fatDialog);
            cal = itemView.findViewById(R.id.calDialog);
            image = itemView.findViewById(R.id.imageDialog);
            setText = itemView.findViewById(R.id.setDialog);
            repText = itemView.findViewById(R.id.repDialog);
            gText = itemView.findViewById(R.id.textGdialog);
            quantityNumber = itemView.findViewById(R.id.numberDialog);
            setNumber = itemView.findViewById(R.id.setnumberDialog);
            repNumber = itemView.findViewById(R.id.repnumberDialog);
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

            quantityNumber.addTextChangedListener(new TextWatcher() {
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

            repNumber.addTextChangedListener(new TextWatcher() {
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
                        mlistener.onRepClick(position,v);
                    }
                }
            });

            setNumber.addTextChangedListener(new TextWatcher() {
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
                        mlistener.onSetClick(position,v);
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
