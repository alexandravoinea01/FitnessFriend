package com.example.fitnessfriend.services;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Food {
    @PrimaryKey
    @NonNull
    String id;
    @ColumnInfo(name = "food_id")
    public String foodId;
    @ColumnInfo(name = "label")
    public String label;
    @ColumnInfo(name = "enerc_kcal")
    public int ENERC_KCAL;

    @ColumnInfo(name = "meal")
    public String meal;

    @ColumnInfo(name = "creation_date")
    public String creationDate;

    public Food() {
        id = new String(String.valueOf(View.generateViewId()));
    }
}

