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

    private final List<Trip> fullTripList = new ArrayList<>();
    private final List<Trip> filteredTripsViewModel = new ArrayList<>();
    private @Nullable City city;
    private @Nullable OnCityChangedListener cityListener;
    private @Nullable OnFilteredTripsChangedListener tripsListener;

    public void setOnCityChangedListener(@Nullable OnCityChangedListener listener) {
        this.cityListener = listener;
    }

    public void setOnAllTripsChangedListener(@Nullable OnFilteredTripsChangedListener listener) {
        this.tripsListener = listener;
    }

    public List<Trip> getFilteredTripsViewModel() {
        return filteredTripsViewModel;
    }

    public void setFilteredTripsViewModel(List<Trip> filteredTripsViewModel) {
        this.filteredTripsViewModel.clear();
        this.filteredTripsViewModel.addAll(filteredTripsViewModel);
        if (tripsListener != null) {
            tripsListener.onFilteredTripsChanged(filteredTripsViewModel);
        }
    }

    public List<Trip> getFullTripList() {
        return fullTripList;
    }

    public void setFullTripList(List<Trip> fullTripList) {
        this.fullTripList.clear();
        this.fullTripList.addAll(fullTripList);
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

    public interface OnFilteredTripsChangedListener {
        void onFilteredTripsChanged(@NonNull List<Trip> allTrips);
    }
}
