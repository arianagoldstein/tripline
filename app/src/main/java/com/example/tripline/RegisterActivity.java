package com.example.tripline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tripline.databinding.ActivityLoginBinding;
import com.example.tripline.databinding.ActivityRegisterBinding;

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
                String name = binding.etName.getText().toString();
                String email = binding.etEmailAddressRegister.getText().toString();
                String password = binding.etPasswordRegister.getText().toString();
                String confPassword = binding.etConfirmPassword.getText().toString();

                registerUser(name, email, password, confPassword);
            }
        });
    }

    // registers the user with the information they've entered
    private void registerUser(String name, String email, String password, String confPassword) {
        Log.i(TAG, "Attempting to register user with email " + email);

        // connect to Parse to register the user

        goMainActivity();
    }

    // brings the user to their profile screen
    private void goMainActivity() {
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}