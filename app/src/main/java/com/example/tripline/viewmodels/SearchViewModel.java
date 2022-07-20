package com.example.tripline.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.tripline.models.City;
import com.example.tripline.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    public static final String TAG = "SearchViewModel";

    private final List<Trip> allTrips = new ArrayList<>();
    private @Nullable City city;
    private @Nullable OnCityChangedListener cityListener;
    private @Nullable OnAllTripsChangedListener tripsListener;

    public void setOnCityChangedListener(@Nullable OnCityChangedListener listener) {
        this.cityListener = listener;
    }

    public void setOnAllTripsChangedListener(@Nullable OnAllTripsChangedListener listener) {
        this.tripsListener = listener;
    }

    public List<Trip> getAllTrips() {
        return allTrips;
    }

    public void setAllTrips(List<Trip> allTrips) {
        this.allTrips.clear();
        this.allTrips.addAll(allTrips);
        if (tripsListener != null) {
            tripsListener.onAllTripsChanged(allTrips);
        }
    }

    public City getCity() {
        return city;
    }

    public void setCity(@NonNull City city) {
        this.city = city;
        if (cityListener != null) {
            cityListener.onCityChanged(city);
        }
    }

    public interface OnCityChangedListener {
        void onCityChanged(@NonNull City city);
    }

    public interface OnAllTripsChangedListener {
        void onAllTripsChanged(@NonNull List<Trip> allTrips);
    }
}
