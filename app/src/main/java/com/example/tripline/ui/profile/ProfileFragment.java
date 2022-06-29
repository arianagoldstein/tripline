package com.example.tripline.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripline.LoginActivity;
import com.example.tripline.MainActivity;
import com.example.tripline.adapters.TripProfileAdapter;
import com.example.tripline.databinding.FragmentProfileBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// this fragment will display the profile of the user who is currently logged in
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";
    private RecyclerView rvTripsProfile;
    protected TripProfileAdapter adapter;
    protected List<Trip> userTrips;
    private int numTripsByThisUser;

    // gets triggered every time we come back to the profile fragment
    @Override
    public void onResume() {
        super.onResume();
        // query trips from the database
        Log.i(TAG, "onResume");
        userTrips.clear();
        adapter.notifyDataSetChanged();
        getUserTrips();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        User userToDisplay = MainActivity.currentUser;
        Log.i(TAG, "Displaying profile for user " + userToDisplay.getFirstName() + " " + userToDisplay.getLastName());
        binding.tvNameProfile.setText(userToDisplay.getFirstName() + " " + userToDisplay.getLastName());

        // clicking the logout button logs the user out and brings them to the login page
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick logout button");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connecting RecyclerView of Trips with the adapter
        rvTripsProfile = binding.rvTripsProfile;
        userTrips = new ArrayList<>();
        adapter = new TripProfileAdapter(getContext(), userTrips);
        binding.rvTripsProfile.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvTripsProfile.setLayoutManager(llm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // loads the 20 most recently created trips from the Parse database
    protected void getUserTrips() {

        // specifying the type of data we want to query
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.include(Trip.KEY_AUTHOR);
        query.addDescendingOrder(Trip.KEY_CREATED_AT);
        query.setLimit(20);

        // get posts created by the user who is currently logged in
        query.whereEqualTo(Trip.KEY_AUTHOR, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {

                // if there is an exception, e will not be null
                if (e != null) {
                    Log.e(TAG, "Issue getting trips for user " + ParseUser.getCurrentUser().getUsername(), e);
                }

                // at this point, we have gotten the trips successfully
                for (Trip trip : trips) {
                    Log.i(TAG, "Trip title: " + trip.getTitle());
                }

                // displaying the number of tripscreated by this user
                numTripsByThisUser = trips.size();
                binding.tvTripsCount.setText(String.valueOf(numTripsByThisUser));

                // adding the trips from Parse into our trips list
                userTrips.clear();
                userTrips.addAll(trips);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
