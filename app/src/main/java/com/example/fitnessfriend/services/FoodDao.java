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
    @Query("SELECT * FROM food")
    List<Food> getAll();

    @Query("SELECT * FROM food WHERE food_id IN (:foodIds)")
    List<Food> loadAllByIds(int[] foodIds);

    @Query("SELECT * FROM food WHERE label LIKE :label")
    Food findByLabel(String label);

    @Query("SELECT * FROM food WHERE meal LIKE :meal")
    List<Food> findByMeal(String meal);

    @Query("SELECT * FROM food WHERE creation_date LIKE :creationDate")
    List<Food> findByCreationDate(Date creationDate);

    @Query("SELECT * FROM food WHERE creation_date LIKE :creationDate and meal LIKE :meal")
    List<Food> findByCreationDateAndMeal(String creationDate, String meal);

    @Insert
    void insert(Food food);

    @Delete
    void delete(Food food);
}