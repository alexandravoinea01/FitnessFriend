package com.example.fitnessfriend.services;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    String id;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "weight")
    public String weight;

    @ColumnInfo(name = "height")
    public String height;

    @ColumnInfo(name = "calorie_goal")
    public String calorieGoal;

    @ColumnInfo(name = "profile_picture")
    public String picture;

    public User() {
        id = new String(String.valueOf(View.generateViewId()));
    }

}
