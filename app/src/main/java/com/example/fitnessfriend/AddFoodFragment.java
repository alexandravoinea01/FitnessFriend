package com.example.fitnessfriend;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.fitnessfriend.services.AppDatabase;
import com.example.fitnessfriend.services.Food;
import com.example.fitnessfriend.services.FoodDao;
import com.example.fitnessfriend.services.HttpUtils;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFoodFragment extends DialogFragment {

    ProgressBar progressBar;
    String selectedMeal = new String("");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFoodDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFoodFragment newInstance(String param1, String param2) {
        AddFoodFragment fragment = new AddFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner dropdown = getView().findViewById(R.id.selectMeal);
        handleDropdown(dropdown);

        MaterialButton searchBtn = getView().findViewById(R.id.searchBtn);
        handleSearchButton(searchBtn);

        MaterialButton backBtn = getView().findViewById(R.id.backBtn);
        handleBackBtn(backBtn);
    }

    private void handleDropdown(Spinner dropdown) {
        String[] items = new String[]{"Breakfast", "Lunch", "Dinner", "Snacks"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                selectedMeal = parent.getSelectedItem().toString();
                System.out.println(selectedMeal);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void handleSearchButton(MaterialButton searchBtn) {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchBarText = getView().findViewById(R.id.searchFood);
                String searchedFood = searchBarText.getText().toString();
                progressBar = getView().findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                try {
                    makeRequest(searchedFood);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void handleBackBtn(MaterialButton backBtn) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaloryIntakeFragment caloryIntakeFragment = new CaloryIntakeFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, caloryIntakeFragment)
                        .commit();
            }
        });
    }

    private void createViewForFoods(Food[] foods) {
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.dynamicLayout);

        layout.removeAllViews();

        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout[] linearLayouts = new LinearLayout[foods.length];
        TextView[] textViews = new TextView[foods.length];
        TextView[] addButtons = new TextView[foods.length];

        for (int i = 0; i < foods.length; i++) {
            textViews[i] = new TextView(getContext());
            String text = foods[i].label + " - " + foods[i].ENERC_KCAL + " kcal / 100g";
            textViews[i].setText(text);
            textViews[i].setTextColor(Color.BLACK);

            addButtons[i] = new TextView(getContext());
            addButtons[i].setText("+");
            addButtons[i].setTextSize(30);
            addButtons[i].setTextColor(0xFF3700B3);
            addButtons[i].setPadding(30, 30, 10, 0);

            final Food food = foods[i];
            addButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Food newFood = new Food();
                    newFood.foodId = food.foodId;
                    newFood.label = food.label;
                    newFood.ENERC_KCAL = food.ENERC_KCAL;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = df.format(new Date());
                    newFood.creationDate = formattedDate;
                    newFood.meal = selectedMeal;
                    FoodDao foodDao = AppDatabase.getInstance(getContext()).foodDao();
                    foodDao.insert(newFood);
                    Toast.makeText(getContext(), "Food added.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            linearLayouts[i] = new LinearLayout(getContext());
            linearLayouts[i].addView(addButtons[i]);
            linearLayouts[i].addView(textViews[i]);
            layout.addView(linearLayouts[i]);
        }
        progressBar = getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private Food[] createRequest(String response) throws JSONException {
        final JSONObject responseObj = new JSONObject(response);
        String text = responseObj.getString("text");
        System.out.println(text);
        final JSONArray hints = responseObj.getJSONArray("hints");
        Food[] foods = new Food[hints.length()];
        for (int i = 0; i < hints.length(); i++) {
            JSONObject jsonObject = hints.getJSONObject(i);
            JSONObject foodObject = jsonObject.getJSONObject("food");
            JSONObject nutrients = foodObject.getJSONObject("nutrients");
            Food food = new Food();
            food.foodId = foodObject.getString("foodId");
            food.label = foodObject.getString("label");
            food.ENERC_KCAL = (int)nutrients.getDouble("ENERC_KCAL");
            TextView myText = new TextView(getContext());
            foods[i] = food;
        }
        return foods;
    }

    private void makeRequest(String searchedFood) throws IOException {
        HttpUtils.getByIngredient(searchedFood, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Food[] foods = new Food[0];
                                foods = createRequest(response.body().string());
                                createViewForFoods(foods);
                            } catch (IOException | JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else {
                    System.out.println("NOT OK\n" + response.body().string());
                }
            }
        });
    }
}