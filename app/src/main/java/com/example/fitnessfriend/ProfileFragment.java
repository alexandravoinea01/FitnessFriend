package com.example.fitnessfriend;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessfriend.services.AppDatabase;
import com.example.fitnessfriend.services.User;
import com.example.fitnessfriend.services.UserDao;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    Uri imagePath;
    Bitmap imageToStore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getActivity().getSharedPreferences("SP_NAME", MODE_PRIVATE);

        TextView email = getView().findViewById(R.id.email);
        email.setText(sharedPreferences.getString("email", ""));

        updateUser(email);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton logoutBtn = getView().findViewById(R.id.logoutBtn);
        MaterialButton saveBtn = getView().findViewById(R.id.saveBtn);
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getActivity().getSharedPreferences("SP_NAME", MODE_PRIVATE);
        TextView email = getView().findViewById(R.id.email);
        email.setText(sharedPreferences.getString("email", ""));

        updateUser(email);

        ImageView imageView = getView().findViewById(R.id.ProfilePicture);

        logoutBtnAction(logoutBtn, sharedPreferences);
        saveBtnAction(saveBtn);
        loadImageFromGallery(imageView);
    }

    private void updateUser(TextView email) {
        User currentUser = AppDatabase.getInstance(getContext()).userDao().findByEmail(email.getText().toString());

        EditText weight = getView().findViewById(R.id.weight);
        weight.setText(currentUser.weight);
        EditText height = getView().findViewById(R.id.height);
        height.setText(currentUser.height);
        EditText calorieGoal = getView().findViewById(R.id.calorieGoal);
        calorieGoal.setText(currentUser.calorieGoal);

        ImageView imageView = (ImageView) getView().findViewById(R.id.ProfilePicture);
        byte[] imageInByte = currentUser.image;
        if (imageInByte != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
            imageView.setImageBitmap(bmp);
        }
    }

    private void saveBtnAction(MaterialButton saveBtn) {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((TextView) getView().findViewById(R.id.email)).getText().toString();
                String weight = ((EditText) getView().findViewById(R.id.weight)).getText().toString();
                String height = ((EditText) getView().findViewById(R.id.height)).getText().toString();
                String calorieGoal = ((EditText) getView().findViewById(R.id.calorieGoal)).getText().toString();

                UserDao userDao = AppDatabase.getInstance(getContext()).userDao();
                User user = userDao.findByEmail(email);
                userDao.updateHeight(height, email);
                userDao.updateWeight(weight, email);
                userDao.updateCalorieGoal(calorieGoal, email);
                Toast.makeText(getContext(), "User updated successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutBtnAction(MaterialButton logoutBtn, SharedPreferences sharedPreferences) {
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", "");
                editor.apply();

                GoogleSignInOptions gso = new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent login_intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            login_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(login_intent);
                        }
                    }
                });
            }
        });
    }

    private void loadImageFromGallery(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 3);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ImageView imageView = getView().findViewById(R.id.ProfilePicture);
            imageView.setImageURI(selectedImage);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();

            String email = ((TextView) getView().findViewById(R.id.email)).getText().toString();
            UserDao userDao = AppDatabase.getInstance(getContext()).userDao();
            User user = userDao.findByEmail(email);
            userDao.updateProfilePicture(imageInByte, email);
        }
    }
}