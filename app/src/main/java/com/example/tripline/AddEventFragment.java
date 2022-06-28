package com.example.tripline;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tripline.databinding.FragmentAddEventBinding;
import com.example.tripline.models.Event;
import com.example.tripline.models.Trip;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class AddEventFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "AddEventFragment";
    private FragmentAddEventBinding binding;
    private String activityType;
    private Trip trip;
    private Boolean nothingSelected;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // creating dropdown menu for Activity Type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.activityTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.spActivityType.setAdapter(adapter);
        binding.spActivityType.setSelection(0);
        binding.spActivityType.setOnItemSelectedListener(this);

        // keeping track of which trip this event is associated with
        // tripId = getArguments().getString("tripId");
        trip = ((MainActivity) getContext()).selectedTrip;

        // outlining onclick listener for add event button
        binding.btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.etTitleEvent.getText().toString();
                String description = binding.etDescriptionEvent.getText().toString();

                if (nothingSelected || activityType.equals("select an activity type")) {
                    Toast.makeText(getContext(), "Activity type cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // calling function to post this new event to the database
                postEvent(title, description, activityType, trip);
            }
        });
    }

    // saves this new event to the Parse database
    private void postEvent(String title, String description, String activityType, Trip trip) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setActivityType(activityType);
        event.setTrip(trip);

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving event with title " + title, e);
                }
                // if we get here, the event was saved successfully
                Log.i(TAG, "Event added successfully!");

                // navigate to profile after adding event
                NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_navigation_addevent_to_navigation_profile);
            }
        });

    }

    // methods to get the value the user selects from the dropdown menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        nothingSelected = false;
        activityType = parent.getItemAtPosition(position).toString();
        Log.i(TAG, activityType + " selected");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        nothingSelected = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
