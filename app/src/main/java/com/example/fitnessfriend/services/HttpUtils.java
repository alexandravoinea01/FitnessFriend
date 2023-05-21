package com.example.fitnessfriend.services;

import android.os.StrictMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    public static Call getByIngredient(String ingredient, Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String baseUrl = "https://edamam-food-and-grocery-database.p.rapidapi.com/api/food-database/v2/parser";
        String requestUrl = baseUrl + "?ingr=" + ingredient;

        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .addHeader("X-RapidAPI-Key", "25f02c1053mshc1322f06c7839a5p113c11jsn76181d8194d9")
                .addHeader("X-RapidAPI-Host", "edamam-food-and-grocery-database.p.rapidapi.com")
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}

