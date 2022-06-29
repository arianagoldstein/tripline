package com.example.tripline.ui.addtrip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tripline.MainActivity;
import com.example.tripline.R;
import com.example.tripline.databinding.FragmentAddtripBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

// this fragment will allow the user to add a new trip to their profile
public class AddTripFragment extends Fragment {

    public static final String TAG = "AddTripFragment";
    private FragmentAddtripBinding binding;

    // variables for photo upload
    private final static int PICK_PHOTO_CODE = 1046;
    private ParseFile coverPhoto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddTripViewModel addTripViewModel =
                new ViewModelProvider(this).get(AddTripViewModel.class);

        binding = FragmentAddtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // when the user clicks on the cover photo placeholder, they can upload a photo
        binding.ivCoverPhotoAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        // when the user clicks Add Trip, gather the user input and check for empty fields
        binding.btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting user input
                String title = binding.etTitle.getText().toString();
                ParseGeoPoint location = new ParseGeoPoint(0, 0);
                String description = binding.etDescription.getText().toString();
                Date startDate = new Date();
                Date endDate = new Date();

                // can't post with an empty title
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // can't post with an empty description
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // can't post Trip without a cover photo
                if (coverPhoto == null || binding.ivCoverPhotoAddTrip.getDrawable() == null) {
                    Toast.makeText(getContext(), "A cover photo must be uploaded", Toast.LENGTH_SHORT).show();
                    return;
                }

                // posting this trip with the user's input from the fields
                postTrip(title, location, description, startDate, endDate, coverPhoto, MainActivity.currentUser);

            }
        });
    }

    private void postTrip(String title, ParseGeoPoint location, String description, Date startDate, Date endDate, ParseFile coverPhoto, User currentUser) {
        // constructing the new trip to post to Parse
        Trip trip = new Trip();
        trip.setTitle(title);
        trip.setLocation(location);
        trip.setDescription(description);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setCoverPhoto(coverPhoto);
        trip.setAuthor(currentUser);

        trip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving trip with title " + title, e);
                }
                // if we get here, the trip was saved successfully
                Log.i(TAG, "Trip added successfully!");

                // navigate to profile after adding trip
                NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_navigation_addtrip_to_navigation_profile);

                // clearing fields when the trip is successfully posted
                binding.etTitle.setText("");
                binding.etLocation.setText("");
                binding.etDescription.setText("");
                binding.etStartDate.setText("");
                binding.etEndDate.setText("");
                binding.ivCoverPhotoAddTrip.setImageResource(0);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // functions for cover photo upload
    public void onPickPhoto(View view) {
        // create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);

            // compressing the image so that it can upload to Parse successfully
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            coverPhoto = new ParseFile("coverphoto.jpg", stream.toByteArray());

            // load the selected image into the cover photo preview
            binding.ivCoverPhotoAddTrip.setImageBitmap(selectedImage);
        }
    }
}
