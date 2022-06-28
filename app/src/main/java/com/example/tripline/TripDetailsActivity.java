package com.example.tripline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ActivityMainBinding;
import com.example.tripline.databinding.ActivityTripDetailsBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.Parse;
import com.parse.ParseUser;

public class TripDetailsActivity extends AppCompatActivity {

    public static final String TAG = "TripDetailsActivity";
    private Trip trip;
    private ActivityTripDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTripDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting the trip to display
        trip = getIntent().getParcelableExtra("trip");

        // populating the XML elements with the details of this trip
        binding.tvTripTitleDetails.setText(trip.getTitle());
        binding.tvLocationDetails.setText(trip.getLocation().toString());
        binding.tvStartDateDetails.setText(trip.getFormattedDate(trip.getStartDate()) + " - ");
        binding.tvEndDateDetails.setText(trip.getFormattedDate(trip.getEndDate()));
        Glide.with(this).load(trip.getCoverPhoto().getUrl()).into(binding.ivCoverPhotoDetails);
        binding.tvDescriptionDetails.setText(trip.getDescription());

        // displaying options to edit if the user created this trip
        if (trip.getAuthor().hasSameId((User) ParseUser.getCurrentUser())){
            Log.i(TAG, "user logged in is the author of this trip");
            binding.btnAddEvent.setVisibility(View.VISIBLE);
        } else {
            binding.btnAddEvent.setVisibility(View.GONE);
        }

    }
}
