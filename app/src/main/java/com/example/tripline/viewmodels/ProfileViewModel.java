package com.example.tripline.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.tripline.models.Trip;
import com.example.tripline.models.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    public static final String TAG = "ProfileViewModel";

    private final List<Trip> userTrips = new ArrayList<>();
    private final List<User> userFollowing = new ArrayList<>();
    private final List<User> userFollowers = new ArrayList<>();

    public List<Trip> getUserTrips() {
        return userTrips;
    }

    public void setUserTrips(final List<Trip> userTrips) {
        this.userTrips.clear();
        this.userTrips.addAll(userTrips);
    }

    public List<User> getUserFollowing() {
        return userFollowing;
    }

    public void setUserFollowing(final List<User> userFollowing) {
        this.userFollowing.clear();
        this.userFollowing.addAll(userFollowing);
    }

    public List<User> getUserFollowers() {
        return userFollowers;
    }

    public void setUserFollowers(final List<User> userFollowers) {
        this.userFollowers.clear();
        this.userFollowers.addAll(userFollowers);
    }
}
