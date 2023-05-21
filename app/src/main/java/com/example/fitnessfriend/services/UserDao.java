package com.example.fitnessfriend.services;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE email LIKE :email")
    User findByEmail(String email);

    @Query("UPDATE user SET height=:height WHERE email = :email")
    void updateHeight(String height, String email);

    @Query("UPDATE user SET weight=:weight WHERE email = :email")
    void updateWeight(String weight, String email);

    @Query("UPDATE user SET calorie_goal=:calorieGoal WHERE email = :email")
    void updateCalorieGoal(String calorieGoal, String email);

    @Query("UPDATE user SET profile_picture=:profilePicture WHERE email = :email")
    void updateProfilePicture(String profilePicture, String email);

    @Insert
    void insert(User user);
}
