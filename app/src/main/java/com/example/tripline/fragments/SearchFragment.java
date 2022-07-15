package com.example.tripline.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.tripline.MainActivity;
import com.example.tripline.adapters.TripSearchAdapter;
import com.example.tripline.databinding.FragmentSearchBinding;
import com.example.tripline.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    private FragmentSearchBinding binding;
    protected TripSearchAdapter adapter;
    protected List<Trip> trips;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // trips = new ArrayList<>();
        adapter = new TripSearchAdapter(getContext(), MainActivity.allTrips);
        binding.rvSearchResults.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvSearchResults.setLayoutManager(llm);

        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
