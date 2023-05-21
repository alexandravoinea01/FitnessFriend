package com.example.fitnessfriend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    MaterialButton logoutBtn;
    TextView title;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//
//        logoutBtn = findViewById(R.id.logoutBtn);
//        title = findViewById(R.id.welcomeTitle);
//        sharedPreferences = getSharedPreferences("SP_NAME", MODE_PRIVATE);
//
//        String newTitle = "Welcome,\n" + sharedPreferences.getString("email", "") + "!";
//        title.setText(newTitle);
//
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("email", "");
//                editor.apply();
//
//                GoogleSignInOptions gso = new GoogleSignInOptions
//                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .build();
//
//                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(HomeActivity.this, gso);
//                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseAuth.getInstance().signOut();
//                            Intent login_intent = new Intent(getApplicationContext(), LoginActivity.class);
//                            login_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(login_intent);
//                        }
//                    }
//                });
//            }
//        });
    }
}