package com.example.tripline.fragments;

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

import com.example.tripline.adapters.TripStreamAdapter;
import com.example.tripline.databinding.FragmentStreamBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// this fragment will display a stream of trips created by the logged-in user and other users
public class StreamFragment extends Fragment {

    public static final String TAG = "StreamFragment";
    private FragmentStreamBinding binding;
    private RecyclerView rvTrips;
    protected TripStreamAdapter adapter;
    protected List<Trip> allTrips;
    private List<User> following;

    public StreamFragment() {

    }

    // gets triggered every time we come back to the stream fragment
    @Override
    public void onResume() {
        super.onResume();
        // query trips from the database
        Log.i(TAG, "onResume");
        queryTrips();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStreamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connecting RecyclerView of Trips with the adapter
        rvTrips = binding.rvTrips;
        allTrips = new ArrayList<>();
        adapter = new TripStreamAdapter(getContext(), allTrips);
        binding.rvTrips.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvTrips.setLayoutManager(llm);

        following = new ArrayList<>();
        queryUserFollows();

        binding.swipeContainer.setOnRefreshListener(() -> onRefresh());

        // configuring the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light);
    }

    private void onRefresh() {
        queryUserFollows();
        queryTrips();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // loads the 20 most recently created trips from the Parse database
    protected void queryTrips() {

        // specifying the type of data we want to query
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.include(Trip.KEY_AUTHOR);
        query.addDescendingOrder(Trip.KEY_CREATED_AT);
        query.whereContainedIn(Trip.KEY_AUTHOR, following);
        query.setLimit(20);

        query.findInBackground((trips, e) -> addTrips(trips, e));
    }

    private void addTrips(List<Trip> trips, ParseException e) {
        binding.swipeContainer.setRefreshing(false);

        if (e != null) {
            Log.e(TAG, "Issue getting trips: ", e);
        }

        // at this point, we have gotten the trips successfully
        allTrips.clear();
        allTrips.addAll(trips);
        adapter.notifyDataSetChanged();
    }

    protected void queryUserFollows() {

        // specifying the type of data we want to query
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);

        // we want to get the Users that the logged-in user follows
        query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, ParseUser.getCurrentUser());

        query.findInBackground((userFollowers, e) -> addFollowing(userFollowers, e));
    }

    private void addFollowing(List<UserFollower> userFollowers, ParseException e) {
        if (e != null) {
            Log.e(TAG, "Issue getting following for user " + ParseUser.getCurrentUser().getUsername(), e);
        }
        following.clear();
        // at this point, we have gotten the user-follower list successfully
        for (UserFollower userFollower : userFollowers) {
            following.add(userFollower.getUserF());
        }
        // now, we have a list of the Users that the logged-in user follows
    }

}
