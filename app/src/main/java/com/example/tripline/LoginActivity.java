package com.example.tripline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tripline.databinding.ActivityLoginBinding;
import com.example.tripline.databinding.ActivityMainBinding;
import com.example.tripline.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // checking if the user is already logged in
//        if (ParseUser.getCurrentUser() != null) {
//            Log.i(TAG, ParseUser.getCurrentUser().getUsername());
//            goMainActivity();
//        }

        // when the user clicks "log in," we use their credentials to log them in
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");

                // get user input
                String email = binding.etEmailAddressLogin.getText().toString();
                String password = binding.etPasswordLogin.getText().toString();

                loginUser(email, password);
            }
        });

        // if the user hasn't signed up yet, clicking the "register" button takes them to register
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to register screen
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    // logs the user in using the credentials they enter
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to log in user with email " + username + " and password " + password);

        // connect to Parse to log in the user
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // if the login is not successful, we'll get an exception
                if (e != null) {
                    Log.e(TAG, "Issue with login: ", e);
                    return;
                }

                // if the action succeeds, then the exception e will be null and we can start the main activity
                MainActivity.currentUser = (User) ParseUser.getCurrentUser();
                goMainActivity();
            }
        });

    }

    // brings the user to their profile screen
    private void goMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}