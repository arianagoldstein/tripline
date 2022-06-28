package com.example.tripline.ui.addtrip;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripline.MainActivity;
import com.example.tripline.databinding.FragmentAddtripBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Date;

// this fragment will allow the user to add a new trip to their profile
public class AddTripFragment extends Fragment {

    public static final String TAG = "AddTripFragment";
    private FragmentAddtripBinding binding;

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

                // constructing ParseFile based on the image uploaded by the user
                File file = new File("");
                ParseFile coverPhoto = new ParseFile(file);

                postTrip(title, location, description, startDate, endDate, coverPhoto, MainActivity.currentUser);

            }
        });
    }

    private void postTrip(String title, ParseGeoPoint location, String description, Date startDate, Date endDate, ParseFile coverPhoto, User currentUser) {
        Trip trip = new Trip();
        trip.setTitle(title);
        trip.setLocation(location);
        trip.setDescription(description);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        // trip.setCoverPhoto(coverPhoto);
        trip.setAuthor(currentUser);

        trip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving trip with title " + title, e);
                }
                // if we get here, the trip was saved successfully
                Log.i(TAG, "Trip added successfully!");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
