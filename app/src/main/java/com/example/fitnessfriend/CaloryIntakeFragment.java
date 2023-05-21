package com.example.fitnessfriend;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessfriend.services.AppDatabase;
import com.example.fitnessfriend.services.Food;
import com.example.fitnessfriend.services.FoodDao;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CaloryIntakeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaloryIntakeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CaloryIntakeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CaloryIntakeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CaloryIntakeFragment newInstance(String param1, String param2) {
        CaloryIntakeFragment fragment = new CaloryIntakeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calory_intake, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView dateText = getView().findViewById(R.id.date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(new Date());
        dateText.setText(formattedDate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dateText = getView().findViewById(R.id.date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(new Date());
        dateText.setText(formattedDate);

        setupDate();

        String date = df.format(new Date());

        setupMeals(date);

        MaterialButton addFoodButton = getView().findViewById(R.id.addFoodBtn);
        AddFoodFragment foodDialogFragment = new AddFoodFragment();
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, foodDialogFragment)
                        .commit();
            }
        });

        EditText searchFood = getView().findViewById(R.id.searchFood);
    }

    private void setupMeals(String date) {
        LinearLayout breakfastLayout = getView().findViewById(R.id.breakfast);
        LinearLayout lunchLayout = getView().findViewById(R.id.lunch);
        LinearLayout dinnerLayout = getView().findViewById(R.id.dinner);
        LinearLayout snacksLayout = getView().findViewById(R.id.snacks);

        clearContents(getView().findViewById(R.id.breakfast));
        clearContents(getView().findViewById(R.id.lunch));
        clearContents(getView().findViewById(R.id.dinner));
        clearContents(getView().findViewById(R.id.snacks));

        List<Food> breakfastFoods = getEntries(breakfastLayout, "Breakfast", date);
        List<Food> lunchFoods = getEntries(lunchLayout, "Lunch", date);
        List<Food> dinnerFoods = getEntries(dinnerLayout, "Dinner", date);
        List<Food> snacksFoods = getEntries(snacksLayout, "Snacks", date);

        showSumPerMeal((TextView) getView().findViewById(R.id.breakfastText), breakfastFoods);
        showSumPerMeal((TextView) getView().findViewById(R.id.lunchText), lunchFoods);
        showSumPerMeal((TextView) getView().findViewById(R.id.dinnerText), dinnerFoods);
        showSumPerMeal((TextView) getView().findViewById(R.id.snacksText), snacksFoods);
    }

    private void clearContents(LinearLayout layout) {
        while (layout.getChildCount() != 1) {
            View v = layout.getChildAt(1);
            layout.removeView(v);
        }
    }

    private void setupDate() {
        final Calendar myCalendar = Calendar.getInstance();

        TextView dateText = getView().findViewById(R.id.date);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                dateText.setText(df.format(myCalendar.getTime()));

                setupMeals(df.format(myCalendar.getTime()));
            }
        };
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void showSumPerMeal(TextView viewById, List<Food> foods) {
        Double sum = Double.valueOf(foods.stream()
                .map(obj -> obj.ENERC_KCAL)
                .reduce(0, Integer::sum));
        String newText = viewById.getText().toString().split(" ")[0] + " - " + sum.intValue() + " kcal";
        viewById.setText(newText);
    }

    private List<Food> getEntries(LinearLayout layout, String meal, String date) {
        FoodDao foodDao = AppDatabase.getInstance(getContext()).foodDao();

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getActivity().getSharedPreferences("SP_NAME", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        List<Food> todayFoods = foodDao.findByCreationDateAndMeal(date, meal, email);
        TextView[] textViews = new TextView[todayFoods.size()];
        LinearLayout[] layouts = new LinearLayout[todayFoods.size()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (todayFoods.size() == 0) {
            TextView textView = new TextView(getContext());
            textView.setText("No foods added.");
            textView.setTextSize(20);
            textView.setPadding(0, 0, 30, 0);
            params.setMargins(40, 0, 0, 0);
            LinearLayout newLayout = new LinearLayout(getContext());
            newLayout.setLayoutParams(params);
            newLayout.addView(textView);
            layout.addView(newLayout);
        }
        for (int i = 0; i < todayFoods.size(); i++) {
            textViews[i] = new TextView(getContext());
            String text = todayFoods.get(i).label + " " + todayFoods.get(i).ENERC_KCAL + "kcal / 100g";
            textViews[i].setText(text);
            layouts[i] = new LinearLayout(getContext());
            textViews[i].setTextSize(20);
            layouts[i].addView(textViews[i]);
            params.setMargins(40, 0, 0, 0);
            layouts[i].setLayoutParams(params);
            layout.addView(layouts[i]);
        }
        return todayFoods;
    }
}