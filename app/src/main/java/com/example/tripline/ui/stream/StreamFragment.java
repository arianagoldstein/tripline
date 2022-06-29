package com.example.tripline.ui.stream;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tripline.adapters.TripStreamAdapter;
import com.example.tripline.databinding.FragmentStreamBinding;
import com.example.tripline.models.Trip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

// this fragment will display a stream of trips created by the logged-in user and other users
public class StreamFragment extends Fragment {

    public static final String TAG = "StreamFragment";
    private FragmentStreamBinding binding;
    private RecyclerView rvTrips;
    protected TripStreamAdapter adapter;
    protected List<Trip> allTrips;

    public StreamFragment() {

    }

    // gets triggered every time we come back to the stream fragment
    @Override
    public void onResume() {
        super.onResume();
        // query trips from the database
        Log.i(TAG, "onResume");
        allTrips.clear();
        adapter.notifyDataSetChanged();
        queryTrips();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StreamViewModel homeViewModel =
                new ViewModelProvider(this).get(StreamViewModel.class);

        binding = FragmentStreamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connecting RecyclerView of Trips with the adapter
        rvTrips = binding.rvTrips;
        allTrips = new ArrayList<>();
        adapter = new TripStreamAdapter(getContext(), allTrips);
        binding.rvTrips.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvTrips.setLayoutManager(llm);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryTrips();
            }
        });

        // configuring the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // loads the 20 most recently created trips from the Parse database
    protected void queryTrips() {

        // specifying the type of data we want to query
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.include(Trip.KEY_AUTHOR);
        query.addDescendingOrder(Trip.KEY_CREATED_AT);
        query.setLimit(20);

        query.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                binding.swipeContainer.setRefreshing(false);

                // if there is an exception, e will not be null
                if (e != null) {
                    Log.e(TAG, "Issue getting trips: ", e);
                }

                // at this point, we have gotten the trips successfully
                for (Trip trip : trips) {
                    Log.i(TAG, "Trip title: " + trip.getTitle());
                }

                // adding the trips from Parse into our trips list
                allTrips.clear();
                allTrips.addAll(trips);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
