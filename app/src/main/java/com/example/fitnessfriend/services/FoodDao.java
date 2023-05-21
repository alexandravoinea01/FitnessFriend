package com.example.fitnessfriend.services;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters({Converters.class})
public interface FoodDao {

    @Query("SELECT * FROM food WHERE creation_date LIKE :creationDate and meal LIKE :meal and made_by LIKE :email")
    List<Food> findByCreationDateAndMeal(String creationDate, String meal, String email);

    @Query("SELECT * FROM food WHERE made_by LIKE :email")
    List<Food> findByUser(String email);

    @Insert
    void insert(Food food);

    @Delete
    void delete(Food food);
}