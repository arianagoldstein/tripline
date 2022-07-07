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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.LoginActivity;
import com.example.tripline.MainActivity;
import com.example.tripline.R;
import com.example.tripline.adapters.TripProfileAdapter;
import com.example.tripline.databinding.FragmentProfileBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

// this fragment will display the profile of the user who is currently logged in
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";
    private RecyclerView rvTripsProfile;
    protected TripProfileAdapter adapter;
    private int numTripsByThisUser;
    private int numFollowers = 0;
    private int numFollowing = 0;

    // gets triggered every time we come back to the profile fragment
    @Override
    public void onResume() {
        super.onResume();
        // query trips from the database
        Log.i(TAG, "onResume");
        MainActivity.userTrips.clear();
        adapter.notifyDataSetChanged();
        getUserTrips();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User userToDisplay = MainActivity.currentUser;
        Log.i(TAG, "Displaying profile for user " + userToDisplay.getFirstName() + " " + userToDisplay.getLastName());
        binding.tvNameProfile.setText(userToDisplay.getFirstName() + " " + userToDisplay.getLastName());

        // connecting RecyclerView of Trips with the adapter
        rvTripsProfile = binding.rvTripsProfile;
        adapter = new TripProfileAdapter(getContext(), MainActivity.userTrips);
        binding.rvTripsProfile.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvTripsProfile.setLayoutManager(llm);

        binding.btnLogout.setOnClickListener(v -> onLogoutBtnClicked());
        binding.ivMapPlaceholder.setOnClickListener(v -> onMapImgClicked(view));
        binding.tvFollowersCount.setOnClickListener(v -> onFollowerCountClicked(v));
        binding.tvFollowingCount.setOnClickListener(v -> onFollowingCountClicked(v));

        displayStaticMap();
        getFollowers();
        getFollowing();
    }

    private void displayStaticMap() {
        // displaying a static map on the profile page
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=382x155&zoom=1&maptype=terrain&markers=color:0x00C7D1%7Csize:tiny");

        for (int i = 0; i < MainActivity.userTrips.size(); i++) {
            if (i >= 15) {  // static maps can display maximum of 15 markers
                break;
            }
            ParseGeoPoint point = MainActivity.userTrips.get(i).getLocation();
            String result = point.getLatitude() + "," + point.getLongitude();
            url.append("%7C").append(result);
        }
        url.append("&key=").append(getString(R.string.maps_api_key));

        Glide.with(getContext()).load(url.toString()).into(binding.ivMapPlaceholder);
    }

    private void onMapImgClicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_profile_to_navigation_map);
    }

    private void onLogoutBtnClicked() {
        Log.i(TAG, "onClick logout button");
        ParseUser.logOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    private void onFollowerCountClicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_profile_to_navigation_follower);
    }

    private void onFollowingCountClicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_profile_to_navigation_following);
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
                numTripsByThisUser = trips.size();
                binding.tvTripsCount.setText(String.valueOf(numTripsByThisUser));

                // adding the trips from Parse into our trips list
                MainActivity.userTrips.clear();
                MainActivity.userTrips.addAll(trips);
                adapter.notifyDataSetChanged();
            }
        });
    }


    protected void getFollowers() {
        // we want to get the Users that FOLLOW the logged-in user, the followers
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
        query.include(UserFollower.KEY_USER_ID);
        query.include(UserFollower.KEY_FOLLOWER_ID);
        query.whereEqualTo(UserFollower.KEY_USER_ID, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<UserFollower>() {
            @Override
            public void done(List<UserFollower> userFollowers, ParseException e) {
                // if there is an exception, e will not be null
                if (e != null) {
                    Log.e(TAG, "Issue getting followers for user " + ParseUser.getCurrentUser().getUsername(), e);
                }

                // at this point, we have gotten the user-follower list successfully
                MainActivity.userFollowers.clear();
                for (UserFollower uf : userFollowers) {
                    MainActivity.userFollowers.add(uf.getFollower());
                }
                numFollowers = userFollowers.size();
                binding.tvFollowersCount.setText(String.valueOf(numFollowers));
            }
        });
    }

    protected void getFollowing() {
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
        // we want to get the Users that the logged-in user follows, the following
        query.include(UserFollower.KEY_USER_ID);
        query.include(UserFollower.KEY_FOLLOWER_ID);
        query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<UserFollower>() {
            @Override
            public void done(List<UserFollower> userFollowers, ParseException e) {
                // if there is an exception, e will not be null
                if (e != null) {
                    Log.e(TAG, "Issue getting followers for user " + ParseUser.getCurrentUser().getUsername(), e);
                }

                // at this point, we have gotten the user-follower list successfully
                MainActivity.userFollowing.clear();
                for (UserFollower uf : userFollowers) {
                    MainActivity.userFollowing.add(uf.getUserF());
                }
                numFollowing = userFollowers.size();
                binding.tvFollowingCount.setText(String.valueOf(numFollowing));
            }
        });
    }
}
