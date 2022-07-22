package com.example.tripline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tripline.adapters.TripProfileAdapter;
import com.example.tripline.databinding.FragmentSavedBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    public static final String TAG = "SavedFragment";
    private FragmentSavedBinding binding;
    private TripProfileAdapter adapter;
    private List<Trip> savedTrips;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedTrips = new ArrayList<>();
        getSavedTrips();
        adapter = new TripProfileAdapter(getContext(), savedTrips);
        binding.rvSavedTrips.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvSavedTrips.setLayoutManager(llm);
        binding.swipeContainerSaved.setOnRefreshListener(() -> getSavedTrips());
    }

    private void getSavedTrips() {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        List<User> id = new ArrayList<>();
        id.add((User) ParseUser.getCurrentUser());
        query.whereContainsAll(Trip.KEY_SAVED_BY, id);
        query.findInBackground((trips, e) -> onSavedTripsFound(trips, e));
    }

    private void onSavedTripsFound(List<Trip> trips, ParseException e) {
        binding.swipeContainerSaved.setRefreshing(false);
        if (e != null) {
            Log.e(TAG, "Error finding saved trips", e);
        }
        Log.i(TAG, "success");
        for (Trip trip : trips) {
            Log.i(TAG, trip.getTitle());
        }
        savedTrips.clear();
        savedTrips.addAll(trips);
        adapter.notifyDataSetChanged();
    }
}
