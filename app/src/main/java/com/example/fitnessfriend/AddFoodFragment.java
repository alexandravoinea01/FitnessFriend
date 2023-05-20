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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

                try {
//                    makeRequest(searchedFood);
                    Food[] foods = mockRequest();
                    createViewForFoods(foods);
                } catch (JSONException e) {
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
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout[] linearLayouts = new LinearLayout[foods.length];
        TextView[] textViews = new TextView[foods.length];
        Button[] materialButtons = new Button[foods.length];

        for (int i = 0; i < foods.length; i++) {
            textViews[i] = new TextView(getContext());
            String text = foods[i].label + " - " + foods[i].ENERC_KCAL + " kcal / 100g";
            textViews[i].setText(text);
            textViews[i].setTextColor(Color.BLACK);

            materialButtons[i] = new MaterialButton(getContext());
            materialButtons[i].setText("+");

            final Food food = foods[i];
            materialButtons[i].setOnClickListener(new View.OnClickListener() {
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
            linearLayouts[i].addView(materialButtons[i]);
            linearLayouts[i].addView(textViews[i]);
            layout.addView(linearLayouts[i]);
        }
    }

    private Food[] mockRequest() throws JSONException {
        String response = "{\"text\":\"chicken\",\"parsed\":[{\"food\":{\"foodId\":\"food_bmyxrshbfao9s1amjrvhoauob6mo\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_broiler_chicken\",\"label\":\"Chicken\",\"knownAs\":\"chicken\",\"nutrients\":{\"ENERC_KCAL\":215.0,\"PROCNT\":18.6,\"FAT\":15.1,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/d33/d338229d774a743f7858f6764e095878.jpg\"}}],\"hints\":[{\"food\":{\"foodId\":\"food_bmyxrshbfao9s1amjrvhoauob6mo\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_broiler_chicken\",\"label\":\"Chicken\",\"knownAs\":\"chicken\",\"nutrients\":{\"ENERC_KCAL\":215.0,\"PROCNT\":18.6,\"FAT\":15.1,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/d33/d338229d774a743f7858f6764e095878.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":1200.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_small\",\"label\":\"small\"}],\"weight\":1000.0},{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":920.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_chicken\",\"label\":\"Chicken\",\"weight\":1200.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":920.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_serving\",\"label\":\"Serving\",\"weight\":250.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_breast\",\"label\":\"Breast\",\"weight\":196.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":89.4},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_slice\",\"label\":\"Slice\",\"weight\":28.3},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":140.0}]},{\"food\":{\"foodId\":\"food_bmyxrshbfao9s1amjrvhoauob6mo\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_broiler_chicken\",\"label\":\"Fryer Chicken\",\"knownAs\":\"chicken\",\"nutrients\":{\"ENERC_KCAL\":215.0,\"PROCNT\":18.6,\"FAT\":15.1,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/d33/d338229d774a743f7858f6764e095878.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":1200.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_small\",\"label\":\"small\"}],\"weight\":1000.0},{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":920.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_chicken\",\"label\":\"Chicken\",\"weight\":1200.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":920.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_serving\",\"label\":\"Serving\",\"weight\":250.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_breast\",\"label\":\"Breast\",\"weight\":196.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":89.4},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_slice\",\"label\":\"Slice\",\"weight\":28.3},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":140.0}]},{\"food\":{\"foodId\":\"food_bmyxrshbfao9s1amjrvhoauob6mo\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_broiler_chicken\",\"label\":\"Broiler Chicken\",\"knownAs\":\"chicken\",\"nutrients\":{\"ENERC_KCAL\":215.0,\"PROCNT\":18.6,\"FAT\":15.1,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/d33/d338229d774a743f7858f6764e095878.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":1200.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_small\",\"label\":\"small\"}],\"weight\":1000.0},{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":920.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_chicken\",\"label\":\"Chicken\",\"weight\":1200.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":920.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_serving\",\"label\":\"Serving\",\"weight\":250.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_breast\",\"label\":\"Breast\",\"weight\":196.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":89.4},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_slice\",\"label\":\"Slice\",\"weight\":28.3},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":140.0}]},{\"food\":{\"foodId\":\"food_agzvc6lbxg03stab195szars32lx\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_drumstick\",\"label\":\"Chicken Drumstick\",\"knownAs\":\"chicken drumstick\",\"nutrients\":{\"ENERC_KCAL\":161.0,\"PROCNT\":18.1,\"FAT\":9.2,\"CHOCDF\":0.11,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/491/4916353c22bd1ac381ac81d55597ddbe.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":130.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_skinless\",\"label\":\"skinless\"}],\"weight\":122.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_chicken\",\"label\":\"Chicken\",\"weight\":245.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_drumstick\",\"label\":\"Drumstick\",\"weight\":130.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_skinless\",\"label\":\"skinless\"}],\"weight\":122.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_aqnly5xaq6rsv2bv0oi81bugldyr\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_giblet\",\"label\":\"Chicken Giblet\",\"knownAs\":\"chicken giblet\",\"nutrients\":{\"ENERC_KCAL\":127.0,\"PROCNT\":18.1,\"FAT\":5.04,\"CHOCDF\":1.14,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/fd0/fd0b7c759d9ce103e852430fca17f04b.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_giblet\",\"label\":\"Giblet\",\"weight\":113.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_a3ssteza84mhb2alnunwga2n6yt8\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_heart\",\"label\":\"Chicken Heart\",\"knownAs\":\"chicken heart\",\"nutrients\":{\"ENERC_KCAL\":153.0,\"PROCNT\":15.6,\"FAT\":9.33,\"CHOCDF\":0.71,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/5aa/5aa4ff691b0918bf29d61b181cd662eb.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":1.8},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_heart\",\"label\":\"Heart\",\"weight\":6.1},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_a9xs7abb632dn3aozv3w4a9351fh\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_liver\",\"label\":\"Chicken Liver\",\"knownAs\":\"chicken liver\",\"nutrients\":{\"ENERC_KCAL\":119.0,\"PROCNT\":16.9,\"FAT\":4.83,\"CHOCDF\":0.73,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/9aa/9aa4760ac12b682555a37a1cdc91150b.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":44.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_liver\",\"label\":\"Liver\",\"weight\":44.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":226.0}]},{\"food\":{\"foodId\":\"food_brxyh8lb5bvzopa0by54fbecw55p\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_neck\",\"label\":\"Chicken Neck\",\"knownAs\":\"chicken neck\",\"nutrients\":{\"ENERC_KCAL\":297.0,\"PROCNT\":14.1,\"FAT\":26.2,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/c88/c8895a600cde7d68d0ec78da8a5513f6.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":15.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":50.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_neck\",\"label\":\"Neck\",\"weight\":50.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":50.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_bp1b7enbhl4sw5a9uwvq5amv00a5\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_roasting_chicken\",\"label\":\"Chicken, Roaster\",\"knownAs\":\"roasting chicken\",\"nutrients\":{\"ENERC_KCAL\":213.0,\"PROCNT\":17.1,\"FAT\":15.5,\"CHOCDF\":0.09,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/9c4/9c4d61e12803924814a35c9043d8b33a.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":1509.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_chicken\",\"label\":\"Chicken\",\"weight\":1509.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":140.0}]},{\"food\":{\"foodId\":\"food_a2up75vbr6qxksaht8g6mawrawfr\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#food_chicken_drumette\",\"label\":\"Chicken Drummette\",\"knownAs\":\"chicken drumette\",\"nutrients\":{\"ENERC_KCAL\":191.0,\"PROCNT\":17.5,\"FAT\":12.8,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/f92/f928682fc890edded472c5d8baeffed5.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":107.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_wing\",\"label\":\"Wing\",\"weight\":49.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":49.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_drummette\",\"label\":\"Drummette\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_wingette\",\"label\":\"Wingette\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_package\",\"label\":\"Package\",\"weight\":1150.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_a6bp4f1bgu015waerzbg6bnbmjjk\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_back\",\"label\":\"Chicken Carcass\",\"knownAs\":\"chicken back\",\"nutrients\":{\"ENERC_KCAL\":319.0,\"PROCNT\":14.0,\"FAT\":28.7,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":500.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_back\",\"label\":\"Back\",\"weight\":198.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":198.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_bdrxu94aj3x2djbpur8dhagfhkcn\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_breast_meat\",\"label\":\"Chicken Breast\",\"knownAs\":\"chicken breast\",\"nutrients\":{\"ENERC_KCAL\":120.0,\"PROCNT\":22.5,\"FAT\":2.62,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/da5/da510379d3650787338ca16fb69f4c94.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":272.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_whole\",\"label\":\"whole\"}],\"weight\":174.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":272.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_breast\",\"label\":\"Breast\",\"weight\":174.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":174.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_serving\",\"label\":\"Serving\",\"weight\":174.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cutlet\",\"label\":\"Cutlet\",\"weight\":113.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_half\",\"label\":\"Half\",\"weight\":87.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_slice\",\"label\":\"Slice\",\"weight\":87.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_package\",\"label\":\"Package\",\"weight\":926.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":226.0}]},{\"food\":{\"foodId\":\"food_bozv00zb0zmd5oa7f0tvlah4apam\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_feet\",\"label\":\"Chicken Feet\",\"knownAs\":\"chicken feet\",\"nutrients\":{\"ENERC_KCAL\":215.0,\"PROCNT\":19.4,\"FAT\":14.6,\"CHOCDF\":0.2,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/99d/99d766d1734a6f8e7ee99d942b085594.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_foot\",\"label\":\"Foot\",\"weight\":56.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_serving\",\"label\":\"Serving\",\"weight\":56.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_bpt4b3hbf9b8vraz3g0ifbbcexmm\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_gizzard\",\"label\":\"Chicken Gizzard\",\"knownAs\":\"chicken gizzard\",\"nutrients\":{\"ENERC_KCAL\":94.0,\"PROCNT\":17.7,\"FAT\":2.06,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/e96/e967b0d9bd88d441dda89b1cc41447dc.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":85.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gizzard\",\"label\":\"Gizzard\",\"weight\":85.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_ane1uw5amh8okda3qqu88bow58ul\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_leg\",\"label\":\"Chicken Leg\",\"knownAs\":\"chicken leg\",\"nutrients\":{\"ENERC_KCAL\":214.0,\"PROCNT\":16.4,\"FAT\":16.0,\"CHOCDF\":0.17,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/f53/f53de7dd1054370cdfd98e18ccf77dbe.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":344.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_leg\",\"label\":\"Leg\",\"weight\":344.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_thigh\",\"label\":\"Thigh\",\"weight\":185.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_drumstick\",\"label\":\"Drumstick\",\"weight\":111.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_back\",\"label\":\"Back\",\"weight\":49.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_bm8j53kbu73enhamxkr92a7x5nq6\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_meat\",\"label\":\"Chicken Meat\",\"knownAs\":\"chicken meat\",\"nutrients\":{\"ENERC_KCAL\":119.0,\"PROCNT\":21.4,\"FAT\":3.08,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/e54/e546d27ffc3d338f99031ce1423cd331.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":197.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":658.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_chicken\",\"label\":\"Chicken\",\"weight\":658.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":658.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":226.0}]},{\"food\":{\"foodId\":\"food_aw629ydan8k1d6amflzaubmwx5rl\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_skin\",\"label\":\"Chicken Skin\",\"knownAs\":\"chicken skin\",\"nutrients\":{\"ENERC_KCAL\":440.0,\"PROCNT\":9.58,\"FAT\":44.2,\"CHOCDF\":0.79,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/ffd/ffdd1d0b789e99381a9ae963469b0a8d.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_bsarl08be0gwarb34bpviafna9d4\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_thigh\",\"label\":\"Chicken Thigh\",\"knownAs\":\"chicken thigh\",\"nutrients\":{\"ENERC_KCAL\":221.0,\"PROCNT\":16.5,\"FAT\":16.6,\"CHOCDF\":0.25,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/007/00792642367e1f55de680762f85cfb3b.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":193.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_skinless\",\"label\":\"skinless\"}],\"weight\":149.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_thigh\",\"label\":\"Thigh\",\"weight\":193.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_skinless\",\"label\":\"skinless\"}],\"weight\":149.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":85.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_aftnyj9ap60fc6av2a9nfbju90c1\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_wing\",\"label\":\"Chicken Wing\",\"knownAs\":\"chicken wing\",\"nutrients\":{\"ENERC_KCAL\":191.0,\"PROCNT\":17.5,\"FAT\":12.8,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/f92/f928682fc890edded472c5d8baeffed5.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_piece\",\"label\":\"Piece\",\"weight\":107.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_wing\",\"label\":\"Wing\",\"weight\":49.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless\",\"label\":\"boneless\"}],\"weight\":49.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_drummette\",\"label\":\"Drummette\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_wingette\",\"label\":\"Wingette\",\"weight\":25.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_package\",\"label\":\"Package\",\"weight\":1150.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_b4430z5avkliqsbzns6gpbhdn07c\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_ground_chicken\",\"label\":\"Chicken Mince\",\"knownAs\":\"ground chicken\",\"nutrients\":{\"ENERC_KCAL\":143.0,\"PROCNT\":17.4,\"FAT\":8.1,\"CHOCDF\":0.04,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/d67/d673152269f59d682d27b0a8446354b3.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":114.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_burger\",\"label\":\"Burger\",\"weight\":114.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_hamburger\",\"label\":\"Hamburger\",\"weight\":114.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pattie\",\"label\":\"Pattie\",\"weight\":114.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_tablespoon\",\"label\":\"Tablespoon\",\"weight\":14.1249999997612},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_cup\",\"label\":\"Cup\",\"weight\":226.0}]},{\"food\":{\"foodId\":\"food_a14vpadag4ngf5a56ukeubb688pp\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_feet_boiled\",\"label\":\"Chicken Feet Boiled\",\"knownAs\":\"chicken feet boiled\",\"nutrients\":{\"ENERC_KCAL\":215.0,\"PROCNT\":19.4,\"FAT\":14.6,\"CHOCDF\":0.2,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/99d/99d766d1734a6f8e7ee99d942b085594.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_foot\",\"label\":\"Foot\",\"weight\":56.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_serving\",\"label\":\"Serving\",\"weight\":56.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]},{\"food\":{\"foodId\":\"food_b5gn1oebj7rluzaj7ypcdbgz882f\",\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Food_chicken_leg_meat\",\"label\":\"Chicken Drumstick Meat\",\"knownAs\":\"chicken leg meat\",\"nutrients\":{\"ENERC_KCAL\":120.0,\"PROCNT\":19.2,\"FAT\":4.22,\"CHOCDF\":0.0,\"FIBTG\":0.0},\"category\":\"Generic foods\",\"categoryLabel\":\"food\",\"image\":\"https://www.edamam.com/food-img/567/567cfd8c18ed1a1395f54029801698be.jpg\"},\"measures\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_unit\",\"label\":\"Whole\",\"weight\":88.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":88.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_leg\",\"label\":\"Leg\",\"weight\":88.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":265.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_thigh\",\"label\":\"Thigh\",\"weight\":147.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":147.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_drumstick\",\"label\":\"Drumstick\",\"weight\":88.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":88.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_back\",\"label\":\"Back\",\"weight\":30.0,\"qualified\":[{\"qualifiers\":[{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Qualifier_boneless_and_skinless\",\"label\":\"boneless and skinless\"}],\"weight\":30.0}]},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_gram\",\"label\":\"Gram\",\"weight\":1.0},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_ounce\",\"label\":\"Ounce\",\"weight\":28.349523125},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_pound\",\"label\":\"Pound\",\"weight\":453.59237},{\"uri\":\"http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram\",\"label\":\"Kilogram\",\"weight\":1000.0}]}],\"_links\":{\"next\":{\"title\":\"Next page\",\"href\":\"https://api.edamam.com/api/food-database/v2/parser?session=44&app_key=69d004c3e3c5b967261e625baea627fc&app_id=85b409cf&ingr=chicken\"}}}";
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
            food.ENERC_KCAL = nutrients.getDouble("ENERC_KCAL");
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

                            LinearLayout layout = (LinearLayout) getView().findViewById(R.id.dynamicLayout);
//                                    for (int i = 0; i <= 5; i++) {
                            TextView myText = new TextView(getContext());
//                                        String result = foodsFiltered.hints[i].0label + " " + foodsFiltered.hints[i].nutrients[0];
                            try {
                                JSONObject json = new JSONObject(response.body().string());
                                String food = json.toString();
//                                            txtString.setText(json.getJSONObject("data").getString("first_name")+ " "+json.getJSONObject("data").getString("last_name"));
                                myText.setText(food);
                            } catch (IOException | JSONException e) {
                                throw new RuntimeException(e);
                            }
                            layout.addView(myText);
                        }
                    });
//                                    }
                } else {
                    System.out.println("NOT OK\n" + response.body().string());
                }
            }
        });
    }
}