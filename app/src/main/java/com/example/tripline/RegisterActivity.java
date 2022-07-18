package com.example.tripline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ActivityRegisterBinding;
import com.example.tripline.models.User;
import com.example.tripline.utility.BitmapScaler;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    public static final String TAG = "RegisterActivity";

    // variables for photo upload
    private final static int PICK_PHOTO_CODE = 1046;
    private ParseFile profilePic;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // allowing user to upload a profile picture
        binding.ivProfilePicUpload.setOnClickListener(v -> onPickPhoto(v));

        // once the user signs up and clicks "register," they should go to the profile screen
        binding.btnRegisterRegister.setOnClickListener(v -> onRegister());
    }

    private void onRegister() {
        Log.i(TAG, "onClick register button");

        // get user input
        String firstName = binding.etFirstName.getText().toString();
        String lastName = binding.etLastName.getText().toString();
        String email = binding.etEmailAddressRegister.getText().toString();
        String password = binding.etPasswordRegister.getText().toString();
        String confPassword = binding.etConfirmPassword.getText().toString();

        // can't register without first and last name
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "First and last name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // can't register without an email address
        if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // can't register if passwords don't match
        if (!(password.equals(confPassword))) {
            Log.i(TAG, "Passwords do not match");
            Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // can't register without a profile photo
        if (profilePic == null || binding.ivProfilePicUpload.getDrawable() == null) {
            Toast.makeText(RegisterActivity.this, "A cover photo must be uploaded", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(firstName, lastName, email, password, profilePic);
    }

    // functions for cover photo upload
    public void onPickPhoto(View view) {
        // create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        } else {
            Toast.makeText(RegisterActivity.this, "Could not find photos on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Issue decoding image(s).", Toast.LENGTH_SHORT).show();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            photoFile = resizeFile(selectedImage);
            profilePic = new ParseFile(photoFile);

            // load the selected image into the cover photo preview
            Glide.with(this).load(photoUri).circleCrop().into(binding.ivProfilePicUpload);
        }
    }

    private File resizeFile(Bitmap image) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 110);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File resizedFile = getPhotoFileUri(photoFileName);
        try {
            resizedFile.createNewFile();
            FileOutputStream fos = null;
            fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch(IOException e) {
            Log.e(TAG, "Error creating resized file", e);
        }
        return resizedFile;
    }

    // helper function to get a URI for the image file
    private File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // registers the user with the information they've entered
    private void registerUser(String fName, String lName, String email, String password, ParseFile profilePic) {
        Log.i(TAG, "Attempting to register user with email " + email);

        // constructing a new User object to represent this user
        User user = new User();
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setUsername(email);
        user.setPassword(password);

        profilePic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.setProfilePic(profilePic);
                user.signUpInBackground(ex -> onRegisterDone(ex, user));
            }
        });

    }

    private void onRegisterDone(ParseException e, User user) {
        // if the signup is not successful, we'll get an exception
        if (e != null) {
            Log.e(TAG, "Issue with registration: ", e);
            Toast.makeText(RegisterActivity.this, "Issue with registration.", Toast.LENGTH_SHORT).show();
            return;
        }
        // if the action succeeds, then the exception e will be null and we can start the main activity
        goMainActivity();
    }

    // brings the user to their profile screen
    private void goMainActivity() {
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
