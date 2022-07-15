package com.example.tripline;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.tripline.adapters.EventAdapter;
import com.example.tripline.databinding.FragmentTripDetailsBinding;
import com.example.tripline.models.Event;
import com.example.tripline.models.Trip;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsFragment extends Fragment {

    public static final String TAG = "TripDetailsFragment";
    private FragmentTripDetailsBinding binding;
    private Trip trip;
    private List<Event> eventList;
    private EventAdapter adapter;

    private TripViewModel sharedViewModel;

    public TripDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTripDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trip = ((MainActivity) getContext()).selectedTrip;
        Log.i(TAG, "in TripDetailsFragment with trip " + trip.getTitle());

        displayTripDetails(view);
        binding.btnAddEvent.setOnClickListener(v -> goToAddEvent(view));
        queryEvents();

        // setting up recyclerview for events
        eventList = new ArrayList<>();
        adapter = new EventAdapter(getContext(), eventList);
        binding.rvEvents.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvEvents.setLayoutManager(llm);
    }

    // displays the details of the trip in the Views
    private void displayTripDetails(View view) {
        // populating the XML elements with the details of this trip
        binding.tvTripTitleDetails.setText(trip.getTitle());
        binding.tvLocationDetails.setText(trip.getFormattedLocation());
        binding.tvStartDateDetails.setText(trip.getFormattedDate(trip.getStartDate()) + " - ");
        binding.tvEndDateDetails.setText(trip.getFormattedDate(trip.getEndDate()));
        Glide.with(this).load(trip.getCoverPhoto().getUrl()).into(binding.ivCoverPhotoDetails);
        binding.tvDescriptionDetails.setText(trip.getDescription());
        binding.tvAuthorDetails.setText(trip.getAuthor().getFirstName() + " " + trip.getAuthor().getLastName());

        // displaying options to edit if the user created this trip
        if (trip.getAuthor().hasSameId(ParseUser.getCurrentUser())){
            Log.i(TAG, "user logged in is the author of this trip");
            binding.btnAddEvent.setVisibility(View.VISIBLE);
            binding.tvAuthorDetails.setVisibility(View.GONE);
        } else {
            binding.btnAddEvent.setVisibility(View.GONE);
            binding.tvAuthorDetails.setVisibility(View.VISIBLE);
            // clicking on the author's name brings you to their profile
            binding.tvAuthorDetails.setOnClickListener(v -> goToAuthorProfile(view));
        }
    }

    // brings user to addEvent fragment
    private void goToAddEvent(@NonNull View view) {
        ((MainActivity) getContext()).selectedTrip = trip;
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_tripdetails_to_navigation_addevent);
    }

    // brings user to the profile of the author of the trip
    private void goToAuthorProfile(@NonNull View view) {
        Bundle bundle = new Bundle();
        bundle.putString("source", "tripDetail");
        sharedViewModel.setUserToDisplay(trip.getAuthor());
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_navigation_tripdetails_to_navigation_profile, bundle);
    }

    // loads the events associated with this Trip
    protected void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo(Event.KEY_TRIP, trip);
        query.findInBackground((events, e) -> onEventsFound(events, e));
    }

    private void onEventsFound(List<Event> events, ParseException e) {
        // if there is an exception, e will not be null
        if (e != null) {
            Log.e(TAG, "Issue getting events: ", e);
        }

        // at this point, we have gotten the events successfully
        for (Event event : events) {
            Log.i(TAG, "Event title: " + event.getTitle());
        }

        // adding the events from Parse into our events list
        eventList.clear();
        eventList.addAll(events);
        adapter.notifyDataSetChanged();
    }
}
