package com.example.tripline.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.tripline.models.Trip;

public class TripViewModel extends ViewModel {

    public static final String TAG = "TripViewModel";

    private Trip selectedTrip;

    public Trip getSelectedTrip() {
        return selectedTrip;
    }

    public void setSelectedTrip(Trip selectedTrip) {
        this.selectedTrip = selectedTrip;
    }
}
