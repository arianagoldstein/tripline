package com.example.tripline.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tripline.MainActivity;
import com.example.tripline.R;
import com.example.tripline.databinding.FragmentAddtripBinding;
import com.example.tripline.models.City;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// this fragment will allow the user to add a new trip to their profile
public class AddTripFragment extends BasePhotoFragment {

    public static final String TAG = "AddTripFragment";
    private FragmentAddtripBinding binding;

    // variables for photo upload
    private final static int PICK_PHOTO_CODE = 1046;
    private ParseFile coverPhoto;

    private Date startDate;
    private Date endDate;
    private double latitude;
    private double longitude;
    private String formattedLocation;
    private String cityName;

    private final static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // adding a location with Google Places API
        binding.ivLocationIcon.setOnClickListener(v -> onLocationAdd());
        binding.tvLocationAdd.setOnClickListener(v -> onLocationAdd());

        // initializing dates as null before they're selected
        startDate = null;
        endDate = null;

        // constructing a Material Date Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("SELECT A RANGE OF DATES");
        final MaterialDatePicker materialDatePicker = builder.build();

        // shows the date picker
        binding.dateHitArea.setOnClickListener(v -> materialDatePicker.show(((MainActivity) getContext()).getSupportFragmentManager(), "DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> onSaveRange(materialDatePicker));

        // when the user clicks on the cover photo placeholder, they can upload a photo
        binding.ivCoverPhotoAddTrip.setOnClickListener(v -> onPickPhoto(v));

        // when the user clicks Add Trip, gather the user input and check for empty fields
        binding.btnAddTrip.setOnClickListener(v -> onAddTripClicked());
    }

    private void onAddTripClicked() {
        // getting user input
        String title = binding.etTitle.getText().toString();
        ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);
        String description = binding.etDescription.getText().toString();

        // can't post with an empty title
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // can't post with an empty description
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // can't post without a start and end date
        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "A start and end date must be selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // can't post Trip without a cover photo
        if (coverPhoto == null || binding.ivCoverPhotoAddTrip.getDrawable() == null) {
            Toast.makeText(getContext(), "A cover photo must be uploaded", Toast.LENGTH_SHORT).show();
            return;
        }
        int duration = (int) ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
        City city = new City();
        city.setCityName(cityName);
        city.setImage(coverPhoto);

        binding.pbLoadingTrip.setVisibility(View.VISIBLE);
        binding.vAddTripCover.setVisibility(View.VISIBLE);

        postTrip(title, location, description, startDate, endDate, coverPhoto, formattedLocation, city, duration, (User) ParseUser.getCurrentUser());
    }

    private void onLocationAdd() {
        // set the fields to specify which types of place data to
        // return after the user has made a selection
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);

        // start the autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void onSaveRange(MaterialDatePicker materialDatePicker) {
        Pair selectedDates = (Pair) materialDatePicker.getSelection();
        // then obtain the startDate & endDate from the range
        final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
        // assigned variables
        startDate = rangeDate.first;
        endDate = rangeDate.second;

        // format the dates in your desired display mode
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM dd, yyyy");
        // display it with setText
        binding.tvStartDateDisplay.setText(simpleFormat.format(startDate));
        binding.tvEndDateDisplay.setText(simpleFormat.format(endDate));
    }

    private void postTrip(String title, ParseGeoPoint location, String description, Date startDate, Date endDate, ParseFile coverPhoto, String formattedLocation, City city, int duration, User currentUser) {
        // constructing the new trip to post to Parse
        Trip trip = new Trip();
        trip.setTitle(title);
        trip.setLocation(location);
        trip.setDescription(description);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setCoverPhoto(coverPhoto);
        trip.setFormattedLocation(formattedLocation);
        trip.setCity(city);
        trip.setDuration(duration);
        trip.setAuthor(currentUser);
        trip.setEventAttributes(new ArrayList<>());

        trip.saveInBackground(e -> onTripAdded(e, title));
    }

    private void onTripAdded(ParseException e, String title) {
        binding.pbLoadingTrip.setVisibility(View.INVISIBLE);
        binding.vAddTripCover.setVisibility(View.INVISIBLE);
        if (e != null) {
            Log.e(TAG, "Error while saving trip with title " + title, e);
            Toast.makeText(getContext(), "Error saving trip.", Toast.LENGTH_SHORT).show();
            return;
        }
        // if we get here, the trip was saved successfully
        Log.i(TAG, "Trip added successfully!");

        // navigate to profile after adding trip
        NavController navController = Navigation.findNavController(getView());
        navController.navigate(R.id.action_navigation_addtrip_to_navigation_profile);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onPickPhoto(View view) {
        // create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        } else {
            Toast.makeText(getContext(), "Could not find photos on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // executing different versions of this function based on how it was triggered
        switch (requestCode) {
            // we have a cover photo that we need to convert to a ParseFile
            case PICK_PHOTO_CODE:
                if ((data != null)) {
                    Uri photoUri = data.getData();
                    binding.ivCoverPhotoAddTrip.setImageURI(photoUri);
                    Bitmap selectedImage = loadFromUri(photoUri);
                    photoFile = resizeFile(selectedImage, "trip", "tripImg.jpg");
                    coverPhoto = new ParseFile(photoFile);
                }
                break;
            // the user has typed a location into the autocomplete search
            case (AUTOCOMPLETE_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    // getting the place the user input
                    Place inputPlace = Autocomplete.getPlaceFromIntent(data);
                    AddressComponents comp = inputPlace.getAddressComponents();
                    List<AddressComponent> compList = comp.asList();
                    formattedLocation = "";
                    for (AddressComponent component : compList) {
                        if (component.getTypes().contains("locality")) {
                            formattedLocation += component.getName() + ", ";
                            cityName = component.getName();
                        }
                        if (component.getTypes().contains("administrative_area_level_1")) {
                            formattedLocation += component.getName() + ", ";
                        }
                        if (component.getTypes().contains("country")) {
                            formattedLocation += component.getShortName();
                        }
                    }

                    // getting the latitude and longitude from the place the user selected
                    LatLng latLngPair = inputPlace.getLatLng();
                    latitude = latLngPair.latitude;
                    longitude = latLngPair.longitude;

                    binding.tvLocationAdd.setText(inputPlace.getName());

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                    Toast.makeText(getContext(), "Error with autocomplete.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                super.onActivityResult(requestCode, resultCode, data);
                return;
            default:
                Log.i(TAG, "default case");
                break;
        }

    }

}
