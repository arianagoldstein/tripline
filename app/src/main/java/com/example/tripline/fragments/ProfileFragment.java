package com.example.tripline.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.tripline.LoginActivity;
import com.example.tripline.R;
import com.example.tripline.viewmodels.UserViewModel;
import com.example.tripline.adapters.TripProfileAdapter;
import com.example.tripline.databinding.FragmentProfileBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.example.tripline.viewmodels.ProfileViewModel;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// this fragment will display the profile of the user who is currently logged in
public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;
    protected TripProfileAdapter adapter;
    private User user;
    private boolean isCurrentUser;

    private UserViewModel sharedViewModel;
    private ProfileViewModel profileViewModel;
    private String source;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // noting how we got to this fragment: from the navbar or someone else's post
        if (getArguments() != null) {
            source = getArguments().getString("source");
        } else {
            source = null;
        }
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
        profileViewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // based on where we came from, display logged-in user or other user
        if ("bottomNav".equals(source)) {
            user = (User) ParseUser.getCurrentUser();
            sharedViewModel.setUserToDisplay(user);
        } else {
            user = sharedViewModel.getUserToDisplay();
        }
        isCurrentUser = sharedViewModel.isCurrentUser();

        Log.i(TAG, "Displaying profile for user " + user.getFirstName() + " " + user.getLastName());
        binding.tvNameProfile.setText(user.getFirstName() + " " + user.getLastName());
        Glide.with(this).load(user.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePic);

        // connecting RecyclerView of Trips with the adapter
        adapter = new TripProfileAdapter(getContext(), profileViewModel.getUserTrips());
        binding.rvTripsProfile.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvTripsProfile.setLayoutManager(llm);

        displayButtons();
        displayStaticMap();
        getFollowers();
        getFollowing();

        binding.ivMapPlaceholder.setOnClickListener(v -> onMapImgClicked(view));
        binding.tvFollowersCount.setOnClickListener(v -> onFollowerCountClicked(v));
        binding.tvFollowingCount.setOnClickListener(v -> onFollowingCountClicked(v));
    }

    // gets triggered every time we come back to the profile fragment
    @Override
    public void onResume() {
        super.onResume();
        // query trips from the database
        Log.i(TAG, "onResume");
        getUserTrips();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // if this is someone else's profile, we should be able to follow
    private void displayButtons() {
        if (!isCurrentUser) {
            binding.btnFollowContainer.setVisibility(View.VISIBLE);

            // check if we are already following this user
            if (checkIfFollowing(user)) {
                Log.i(TAG, "We are already following this user");
                binding.btnFollow.setBackgroundColor(getContext().getColor(R.color.gray));
                binding.btnFollow.setText(R.string.following);
            } else {
                binding.btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followUser(user);
                    }
                });
            }
        } else {
            binding.btnFollowContainer.setVisibility(View.GONE);
        }
    }

    // checking if the logged-in user is following the user we're displaying
    private boolean checkIfFollowing(User user) {
        for (int i = 0; i < profileViewModel.getUserFollowing().size(); i++) {
            if (profileViewModel.getUserFollowing().get(i).hasSameId(user)) {
                return true;
            }
        }
        return false;
    }

    // connects to Parse to follow this user
    private void followUser(User userToDisplay) {
        binding.btnFollow.setBackgroundColor(getContext().getColor(R.color.gray));
        binding.btnFollow.setText(R.string.following);

        UserFollower userFollower = new UserFollower();
        userFollower.setFollower((User) ParseUser.getCurrentUser());
        userFollower.setUserF(userToDisplay);

        userFollower.saveInBackground(e -> onFollowedUser(e));
    }

    private void onFollowedUser(ParseException e) {
        if (e != null) {
            Log.e(TAG, "Error following user", e);
            Toast.makeText(getContext(), "Error following user", Toast.LENGTH_SHORT).show();
        }

        // if we get here, the user was followed
        Log.i(TAG, "User followed successfully!");
    }

    // displaying a static map on the profile page
    private void displayStaticMap() {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=382x155&zoom=1&maptype=terrain&markers=color:0x00C7D1%7Csize:tiny");

        for (int i = 0; i < profileViewModel.getUserTrips().size(); i++) {
            if (i >= 15) {  // static maps can display maximum of 15 markers
                break;
            }
            ParseGeoPoint point = profileViewModel.getUserTrips().get(i).getLocation();
            String result = point.getLatitude() + "," + point.getLongitude();
            url.append("%7C").append(result);
        }
        url.append("&key=").append(getString(R.string.maps_api_key));

        Glide.with(getContext()).load(url.toString()).placeholder(R.drawable.map_placeholder_img).into(binding.ivMapPlaceholder);
    }

    private void onMapImgClicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_profile_to_navigation_map);
    }

    private void onFollowerCountClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCurrentUser", isCurrentUser);
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_profile_to_navigation_follower, bundle);
    }

    private void onFollowingCountClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCurrentUser", isCurrentUser);
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_profile_to_navigation_following, bundle);
    }

    // loads the 20 most recently created trips from the Parse database
    protected void getUserTrips() {
        // specifying the type of data we want to query
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.include(Trip.KEY_AUTHOR);
        query.addDescendingOrder(Trip.KEY_CREATED_AT);
        query.setLimit(20);

        // get posts created by the user who is currently logged in
        query.whereEqualTo(Trip.KEY_AUTHOR, user);
        query.findInBackground((trips, e) -> onTripsFound(trips, e));
    }

    private void onTripsFound(List<Trip> trips, ParseException e) {
        // if there is an exception, e will not be null
        if (e != null) {
            Log.e(TAG, "Issue getting trips for user " + user.getUsername(), e);
        }

        // at this point, we have gotten the trips successfully
        int numTripsByThisUser = trips.size();
        binding.tvTripsCount.setText(String.valueOf(numTripsByThisUser));

        // adding the trips from Parse into our trips list
        profileViewModel.setUserTrips(trips);
        adapter.notifyDataSetChanged();
        displayStaticMap();
    }


    protected void getFollowers() {
        // we want to get the Users that FOLLOW the logged-in user, the followers
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
        query.include(UserFollower.KEY_USER_ID);
        query.include(UserFollower.KEY_FOLLOWER_ID);
        query.whereEqualTo(UserFollower.KEY_USER_ID, user);

        query.findInBackground((userFollowers, e) -> onFollowersFound(userFollowers, e));
    }

    private void onFollowersFound(List<UserFollower> userFollowers, ParseException e) {
        // if there is an exception, e will not be null
        if (e != null) {
            Log.e(TAG, "Issue getting followers for user " + user.getFirstName(), e);
        }

        // at this point, we have gotten the user-follower list successfully
        List<User> followersToAdd = new ArrayList<>();
        for (UserFollower uf : userFollowers) {
            followersToAdd.add(uf.getFollower());
        }
        profileViewModel.setUserFollowers(followersToAdd);
        int numFollowers = userFollowers.size();
        binding.tvFollowersCount.setText(String.valueOf(numFollowers));
    }

    protected void getFollowing() {
        // we want to get the Users that the logged-in user follows, the following
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
        query.include(UserFollower.KEY_USER_ID);
        query.include(UserFollower.KEY_FOLLOWER_ID);
        query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, user);
        query.findInBackground((userFollowers, e) -> onFollowingFound(userFollowers, e));
    }

    private void onFollowingFound(List<UserFollower> userFollowers, ParseException e) {
        // if there is an exception, e will not be null
        if (e != null) {
            Log.e(TAG, "Issue getting followers for user " + user.getFirstName(), e);
        }

        // at this point, we have gotten the user-follower list successfully
        List<User> followingToAdd = new ArrayList<>();
        for (UserFollower uf : userFollowers) {
            followingToAdd.add(uf.getUserF());
        }
        profileViewModel.setUserFollowing(followingToAdd);
        int numFollowing = userFollowers.size();
        binding.tvFollowingCount.setText(String.valueOf(numFollowing));
    }
}
