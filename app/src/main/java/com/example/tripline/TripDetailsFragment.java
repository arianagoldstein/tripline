package com.example.tripline;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.FragmentTripDetailsBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.ParseUser;

public class TripDetailsFragment extends Fragment {

    public static final String TAG = "TripDetailsFragment";
    private FragmentTripDetailsBinding binding;

    public TripDetailsFragment() {
        // Required empty public constructor
    }
    public static TripDetailsFragment newInstance(String param1, String param2) {
        TripDetailsFragment fragment = new TripDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTripDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Trip trip = ((MainActivity) getContext()).selectedTrip;
        Log.i(TAG, "in TripDetailsFragment with trip " + trip.getTitle());

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

        binding.btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).selectedTrip = trip;

                // now we have an instance of the navbar, so we can go anywhere
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_tripdetails_to_navigation_addevent);
            }
        });
    }
}
