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
    private @Nullable OnCityChangedListener listener;

    public void setOnCityChangedListener(@Nullable OnCityChangedListener listener) {
        this.listener = listener;
    }

    public List<Trip> getAllTrips() {
        return allTrips;
    }

    public void setAllTrips(List<Trip> allTrips) {
        this.allTrips.clear();
        this.allTrips.addAll(allTrips);
    }

    public City getCity() {
        return city;
    }

    public void setCity(@NonNull City city) {
        this.city = city;
        if (listener != null) {
            listener.onCityChanged(city);
        }
    }

    public interface OnCityChangedListener {
        void onCityChanged(@NonNull City city);
    }
}
