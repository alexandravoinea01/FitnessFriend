package com.example.fitnessfriend;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessfriend.services.AppDatabase;
import com.example.fitnessfriend.services.FoodDao;
import com.example.fitnessfriend.services.User;
import com.example.fitnessfriend.services.UserDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView todayIntake, targetIntake;
        todayIntake = requireView().findViewById(R.id.todayIntake);
        targetIntake = getView().findViewById(R.id.targetIntake);

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getActivity().getSharedPreferences("SP_NAME", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        FoodDao foodDao = AppDatabase.getInstance(getContext()).foodDao();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(new Date());

        Double today = Double.valueOf(foodDao.findByUser(email, formattedDate)
                .stream()
                .map(obj -> obj.ENERC_KCAL)
                .reduce(0, Integer::sum));

        String calorieGoal = AppDatabase.getInstance(getContext()).userDao().findByEmail(email).calorieGoal;

        String todayText = today.toString() + " kcal";

        todayIntake.setText(todayText);

        if (calorieGoal == null) {
            targetIntake.setText("No goal was set.");
        } else {
            calorieGoal = calorieGoal + " kcal / day";
            targetIntake.setText(calorieGoal);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        VideoView videoView = view.findViewById(R.id.videoView);
        Random rand = new Random();
        int videoNumber = rand.nextInt(4);
        videoNumber += 1;
        String videoPath;
        if (videoNumber == 1) {
            videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.video1;
        } else if (videoNumber == 2) {
            videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.video2;
        } else if (videoNumber == 3) {
            videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.video3;
        } else {
            videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.video4;
        }
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();
        return view;
    }
}