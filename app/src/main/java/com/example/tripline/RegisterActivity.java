package com.example.tripline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tripline.databinding.ActivityLoginBinding;
import com.example.tripline.databinding.ActivityRegisterBinding;
import com.example.tripline.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.net.PasswordAuthentication;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    public static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // once the user signs up and clicks "register," they should go to the profile screen
        binding.btnRegisterRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick register button");

                // get user input
                String firstName = binding.etFirstName.getText().toString();
                String lastName = binding.etLastName.getText().toString();
                String email = binding.etEmailAddressRegister.getText().toString();
                String password = binding.etPasswordRegister.getText().toString();
                String confPassword = binding.etConfirmPassword.getText().toString();

                registerUser(firstName, lastName, email, password, confPassword);
            }
        });
    }

    // registers the user with the information they've entered
    private void registerUser(String fName, String lName, String email, String password, String confPassword) {
        Log.i(TAG, "Attempting to register user with email " + email);

        if (!(password.equals(confPassword))) {
            Log.i(TAG, "Passwords do not match");
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // constructing a new User object to represent this user
        User user = new User();
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setUsername(email);
        user.setPassword(password);

        // connect to Parse to register the user
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // if the signup is not successful, we'll get an exception
                if (e != null) {
                    Log.e(TAG, "Issue with registration: ", e);
                    return;
                }
                // if the action succeeds, then the exception e will be null and we can start the main activity
                MainActivity.currentUser = user;
                goMainActivity();
            }
        });
    }

    // brings the user to their profile screen
    private void goMainActivity() {
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}