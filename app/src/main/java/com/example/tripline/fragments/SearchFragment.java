package com.example.tripline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripline.adapters.CitySearchRecAdapter;
import com.example.tripline.adapters.TripSearchAdapter;
import com.example.tripline.adapters.TripSearchRecAdapter;
import com.example.tripline.databinding.FragmentSearchBinding;
import com.example.tripline.models.City;
import com.example.tripline.models.Trip;
import com.example.tripline.viewmodels.SearchViewModel;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchViewModel.OnCityChangedListener {

    public static final String TAG = "SearchFragment";
    private FragmentSearchBinding binding;
    protected TripSearchAdapter searchAdapter;
    protected TripSearchRecAdapter tripRecAdapter;
    protected CitySearchRecAdapter cityRecAdapter;
    protected List<Trip> trips;
    protected List<Trip> weekendTrips;
    protected List<City> cityRecs;

    private SearchViewModel searchViewModel;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trips = searchViewModel.getAllTrips();
        setUpSearchResultsAdapter();
        setUpWeekendTripsAdapter();
        setUpCityRecsAdapter();
        setUpSearchListener();
    }

    private void setUpSearchListener() {
        binding.svSearch.setSubmitButtonEnabled(true);
        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideRecommendations();
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showRecommendations();
                } else {
                    hideRecommendations();
                }
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchViewModel.setOnCityChangedListener(this);
    }

    private void setUpCityRecsAdapter() {
        // RecyclerView for city recommendations
        cityRecs = new ArrayList<>();
        cityRecAdapter = new CitySearchRecAdapter(getContext(), cityRecs);
        binding.rvCitiesToExplore.setAdapter(cityRecAdapter);
        LinearLayoutManager llm3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvCitiesToExplore.setLayoutManager(llm3);
        queryCityRecs();
    }

    private void setUpWeekendTripsAdapter() {
        // RecyclerView for weekend getaway recommendations
        weekendTrips = new ArrayList<>();
        tripRecAdapter = new TripSearchRecAdapter(getContext(), weekendTrips);
        binding.rvWeekendGetaways.setAdapter(tripRecAdapter);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvWeekendGetaways.setLayoutManager(llm2);
        queryWeekendTrips();
    }

    private void setUpSearchResultsAdapter() {
        // RecyclerView for overall search results
        searchAdapter = new TripSearchAdapter(getContext(), trips);
        binding.rvSearchResults.setAdapter(searchAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvSearchResults.setLayoutManager(llm);
    }

    private void showRecommendations() {
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.tvCitiesToExplore.setVisibility(View.VISIBLE);
        binding.rvCitiesToExplore.setVisibility(View.VISIBLE);
        binding.tvWeekendGetaways.setVisibility(View.VISIBLE);
        binding.rvWeekendGetaways.setVisibility(View.VISIBLE);
    }

    // hides recommendations when the user starts to type
    private void hideRecommendations() {
        binding.rvSearchResults.setVisibility(View.VISIBLE);
        binding.tvCitiesToExplore.setVisibility(View.GONE);
        binding.rvCitiesToExplore.setVisibility(View.GONE);
        binding.tvWeekendGetaways.setVisibility(View.GONE);
        binding.rvWeekendGetaways.setVisibility(View.GONE);
    }

    // queries Parse database for trips <= 3 days long
    private void queryWeekendTrips() {
        ParseQuery<Trip> query = new ParseQuery<>(Trip.class);
        query.include(Trip.KEY_TITLE);
        query.include(Trip.KEY_AUTHOR);
        query.include(Trip.KEY_START_DATE);
        query.include(Trip.KEY_END_DATE);
        query.include(Trip.KEY_DURATION);
        query.setLimit(20);

        query.whereLessThanOrEqualTo(Trip.KEY_DURATION, 3);
        query.findInBackground((objects, e) -> onWeekendTripsFound(e));
    }

    private void onWeekendTripsFound(ParseException e) {
        if (e != null) {
            Log.e(TAG, "Error finding weekend trips", e);
            return;
        }
        for (Trip trip : trips) {
            Log.i(TAG, "Trip: " + trip.getTitle());
            Log.i(TAG, "Duration: " + trip.getDuration());
        }
        weekendTrips.clear();
        weekendTrips.addAll(trips);
        tripRecAdapter.notifyDataSetChanged();
    }

    private void queryCityRecs() {
        ParseQuery<City> query = new ParseQuery<>(City.class);
        query.include(City.KEY_CITY_NAME);
        query.include(City.KEY_IMAGE);
        query.setLimit(20);
        query.findInBackground((cities, e) -> onCitiesFound(cities, e));
    }

    private void onCitiesFound(List<City> cities, ParseException e) {
        if (e != null) {
            Log.e(TAG, "Error finding cities", e);
            return;
        }
        cityRecs.clear();
        cityRecs.addAll(cities);
        cityRecAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCityChanged(@NonNull City city) {
        if (searchViewModel.getCity() != null) {
            Log.i(TAG, "Here");
            hideRecommendations();
            binding.svSearch.setQuery(searchViewModel.getCity().getCityName(), true);
        }
    }
}
