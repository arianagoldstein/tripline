package com.example.tripline.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.tripline.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    public static final String TAG = "SearchViewModel";

    private List<Trip> allTrips = new ArrayList<>();

    public List<Trip> getAllTrips() {
        return allTrips;
    }

    public void setAllTrips(List<Trip> allTrips) {
        this.allTrips.clear();
        this.allTrips.addAll(allTrips);
    }
}
