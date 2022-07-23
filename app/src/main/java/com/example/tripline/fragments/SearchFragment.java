package com.example.tripline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.tripline.R;
import com.example.tripline.adapters.CitySearchRecAdapter;
import com.example.tripline.adapters.TripSearchAdapter;
import com.example.tripline.adapters.TripSearchRecAdapter;
import com.example.tripline.databinding.FragmentSearchBinding;
import com.example.tripline.models.City;
import com.example.tripline.models.Trip;
import com.example.tripline.viewmodels.SearchViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.RangeSlider;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Headers;

public class SearchFragment extends Fragment implements SearchViewModel.OnCityChangedListener, SearchViewModel.OnFilteredTripsChangedListener {

    public static final String TAG = "SearchFragment";
    private FragmentSearchBinding binding;
    private TripSearchAdapter searchAdapter;
    private TripSearchRecAdapter tripRecAdapter;
    private CitySearchRecAdapter cityRecAdapter;
    private List<Trip> weekendTrips;
    private List<City> cityRecs;
    private SearchViewModel searchViewModel;
    private ParseGeoPoint latLongPair = new ParseGeoPoint(0.0, 0.0);

    public SearchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpSearchResultsAdapter();
        setUpWeekendTripsAdapter();
        setUpCityRecsAdapter();
        setUpSearchListener();
    }

    private void showFilterDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_filter, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
        builder.setView(view);
        builder.setTitle("Search for trips")
                .setNegativeButton("CLEAR", (dialog, which) -> {

                }).setPositiveButton("SHOW RESULTS", (dialog, which) -> {
                    getUserInputFromDialog(view);
                }).show();
    }

    private void getUserInputFromDialog(View view) {
        String zipcode = ((EditText) view.findViewById(R.id.etZipCode)).getText().toString();

        ChipGroup filterChipGroup = ((ChipGroup) view.findViewById(R.id.chipGroupContaining));
        List<Integer> ids = filterChipGroup.getCheckedChipIds();
        List<String> eventAttributeList = new ArrayList<>();
        for (Integer id : ids) {
            Chip chip = filterChipGroup.findViewById(id);
            eventAttributeList.add(chip.getText().toString());
        }

        RangeSlider lengthRangeSlider = ((RangeSlider) view.findViewById(R.id.rangeSliderLength));
        List<Float> bounds = lengthRangeSlider.getValues();
        int lowerBound = (int) (float)bounds.get(0);
        int upperBound = (int) (float)bounds.get(1);

        displaySearchResultString(zipcode, eventAttributeList, lowerBound, upperBound);
        getLatLngFromZip(zipcode, eventAttributeList, lowerBound, upperBound);
    }

    private void displaySearchResultString(String zipcode, List<String> eventAttributeList, int lowerBound, int upperBound) {
        String displayString = "Displaying trips ";
        if (!(zipcode.isEmpty())) {
            displayString += "near " + zipcode + " ";
        }
        if (!eventAttributeList.isEmpty()) {
            displayString += "containing ";
            for (int i = 0; i < eventAttributeList.size(); i++) {
                if ((i == eventAttributeList.size()-1) && eventAttributeList.size() > 1) {
                    displayString += " and ";
                }
                displayString += eventAttributeList.get(i);
                if ((i != eventAttributeList.size() - 1) && eventAttributeList.size() > 2) {
                    displayString += ", ";
                }
            }
        }
        displayString += " between " + lowerBound + " and " + upperBound + " days long";
        binding.tvDisplayingResults.setVisibility(View.VISIBLE);
        binding.tvDisplayingResults.setText(displayString);
    }

    private void getLatLngFromZip(String zipcode, List<String> eventAttributeList, int lowerBound, int upperBound) {
        String api_key = getString(R.string.zipcode_api_key);
        String url = "https://www.zipcodeapi.com/rest/"+ api_key + "/info.json/" + zipcode + "/degrees";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successfully got zip code information");
                JSONObject object = json.jsonObject;
                try {
                    latLongPair = new ParseGeoPoint(object.getDouble("lat"), object.getDouble("lng"));
                    queryFilteredTrips(eventAttributeList, lowerBound, upperBound);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                latLongPair = null;
                Log.e(TAG, "Error getting zip code information");
                queryFilteredTrips(eventAttributeList, lowerBound, upperBound);
            }
        });
    }

    private void queryFilteredTrips(List<String> eventAttributeList, int lowerBound, int upperBound) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        if (latLongPair != null) {
            query.whereWithinMiles(Trip.KEY_LOCATION, latLongPair, 100.0);
        }
        if (!(eventAttributeList.isEmpty())) {
            query.whereContainsAll(Trip.KEY_EVENT_ATTRIBUTES, eventAttributeList);
        }
        query.include(Trip.KEY_TITLE);
        query.include(Trip.KEY_DURATION);
        query.include(Trip.KEY_CITY);
        query.include(Trip.KEY_AUTHOR);
        query.whereLessThanOrEqualTo(Trip.KEY_DURATION, upperBound);
        query.whereGreaterThanOrEqualTo(Trip.KEY_DURATION, lowerBound);
        query.findInBackground((trips, e) -> onFilteredTripsFound(trips, e));
    }

    private void onFilteredTripsFound(List<Trip> trips, ParseException e) {
        if ( e != null) {
            Log.e(TAG, "Error querying filtered trips", e);
            return;
        }
        Log.i(TAG, "successfully got trips");
        searchViewModel.setFilteredTripsViewModel(trips);
    }

    private void setUpSearchListener() {
        binding.ibFilter.setOnClickListener(v -> showFilterDialog());
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
        searchViewModel.setOnAllTripsChangedListener(this);
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
        searchAdapter = new TripSearchAdapter(getContext(), searchViewModel.getFullTripList());
        binding.rvSearchResults.setAdapter(searchAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvSearchResults.setLayoutManager(llm);
    }

    private void showRecommendations() {
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.tvDisplayingResults.setVisibility(View.GONE);
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
        query.findInBackground((objects, e) -> onWeekendTripsFound(objects, e));
    }

    private void onWeekendTripsFound(List<Trip> objects, ParseException e) {
        if (e != null) {
            Log.e(TAG, "Error finding weekend trips", e);
            return;
        }
        for (Trip trip : objects) {
            Log.i(TAG, "Trip: " + trip.getTitle());
            Log.i(TAG, "Duration: " + trip.getDuration());
        }
        Collections.shuffle(objects);
        weekendTrips.clear();
        weekendTrips.addAll(objects);
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
        Collections.shuffle(cities);
        cityRecs.clear();
        cityRecs.addAll(cities);
        cityRecAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCityChanged(@NonNull City city) {
        if (searchViewModel.getCity() != null) {
            hideRecommendations();
            binding.svSearch.setQuery(searchViewModel.getCity().getCityName(), true);
        }
    }

    @Override
    public void onFilteredTripsChanged(@NonNull List<Trip> newTrips) {
        Log.i(TAG, "onFilteredTripsChanged");
        if (searchViewModel.getFilteredTripsViewModel() != null) {
            hideRecommendations();
            searchAdapter.setFilteredTrips(newTrips);
        }
    }
}
