package com.example.tripline.fragments;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tripline.R;
import com.example.tripline.adapters.GalleryAdapter;
import com.example.tripline.databinding.FragmentAddEventBinding;
import com.example.tripline.models.Event;
import com.example.tripline.models.Trip;
import com.example.tripline.viewmodels.TripViewModel;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddEventFragment extends BasePhotoFragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "AddEventFragment";
    private FragmentAddEventBinding binding;
    private String eventType;
    private Trip trip;
    private Boolean nothingSelected;

    // variables for multiple photo upload
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int RESULT_OK = -1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GalleryAdapter galleryAdapter;
    private List<Uri> imageUriList;

    private TripViewModel tripViewModel;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // creating dropdown menu for event Type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.eventTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.spEventType.setAdapter(adapter);
        binding.spEventType.setSelection(0);
        binding.spEventType.setOnItemSelectedListener(this);

        // keeping track of which trip this event is associated with
        trip = tripViewModel.getSelectedTrip();

        imageUriList = new ArrayList<>();
        binding.btnAddPhotos.setOnClickListener(v -> onAddPhotosClicked());
        binding.btnAddEvent.setOnClickListener(v -> onAddEventClicked());
    }

    private void onAddEventClicked() {
        String title = binding.etTitleEvent.getText().toString();
        String description = binding.etDescriptionEvent.getText().toString();

        if (checkUserInput(title, description)) return;
        binding.animationLoadingEvent.setVisibility(View.VISIBLE);
        binding.vAddEventCover.setVisibility(View.VISIBLE);

        JSONArray photoArray = getPhotoArray();
        if (photoArray == null) return;

        // calling function to post this new event to the database
        postEvent(title, description, eventType, trip, photoArray);
    }

    private boolean checkUserInput(String title, String description) {
        // user must include a title
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Title cannot be empty!", Toast.LENGTH_SHORT).show();
            return true;
        }

        // user must include a description
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();
            return true;
        }

        // user must select an event type
        if (nothingSelected || eventType.equals("select an event type")) {
            Toast.makeText(getContext(), "Event type cannot be empty!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Nullable
    private JSONArray getPhotoArray() {
        // constructing array that will store user photos
        JSONArray photoArray = new JSONArray();

        // populating array with images (ParseFiles)
        for (int i = 0; i < imageUriList.size(); i++) {
            // getting the URI at this index
            Uri uri = imageUriList.get(i);
            Log.i(TAG, "image URI: " + uri);

            Bitmap image = loadFromUri(uri);
            photoFile = resizeFile(image, "event", "eventImg" + i + ".jpg");
            ParseFile file = new ParseFile(photoFile);

            // constructing a new JSONObject to add to the array to save to Parse
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("image", file);
                photoArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Error adding JSON object.");
            }
        }

        // user must upload at least one image
        if (photoArray.length() == 0) {
            Toast.makeText(getContext(), "You must upload at least one image!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return photoArray;
    }

    private void onAddPhotosClicked() {
        // clearing the list so we don't add photos twice
        imageUriList.clear();

        // creating a new intent to select images from gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    // saves this new event to the Parse database
    private void postEvent(String title, String description, String eventType, Trip trip, JSONArray photoArray) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setActivityType(eventType);
        event.setTrip(trip);
        event.setPhotos(photoArray);
        event.saveInBackground(e -> onEventSaved(e, title));
        updateTripEventAttributes(trip, eventType);
    }

    private void updateTripEventAttributes(Trip trip, String eventType) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo("objectId", trip.getObjectId());
        query.findInBackground((trips, e) -> onTripFound(trips, e, eventType));
    }

    private void onTripFound(List<Trip> trips, ParseException e, String eventType) {
        if (e != null) {
            Log.e(TAG, "Issue finding trip");
            return;
        }
        for (Trip trip : trips) {
            trip.updateEventAttributes(eventType);
            trip.saveInBackground();
        }
    }

    private void onEventSaved(ParseException e, String title) {
        binding.animationLoadingEvent.setVisibility(View.INVISIBLE);
        binding.vAddEventCover.setVisibility(View.INVISIBLE);

        if (e != null) {
            Log.e(TAG, "Error while saving event with title " + title, e);
        }
        // if we get here, the event was saved successfully
        Log.i(TAG, "Event added successfully!");

        // navigate to profile after adding event
        NavController navController = Navigation.findNavController(getView());
        navController.navigate(R.id.action_navigation_addevent_to_navigation_profile);
    }

    // methods to get the value the user selects from the dropdown menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        nothingSelected = false;
        eventType = parent.getItemAtPosition(position).toString();
        Log.i(TAG, eventType + " selected");
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

    // function that gets the images selected by the user and puts them into a gridview
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // when an image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // get the Image from data
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // get the cursor
                    Cursor cursor = getContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(getContext().getApplicationContext(),mArrayUri);
                    binding.gvGallery.setAdapter(galleryAdapter);
                    binding.gvGallery.setVerticalSpacing(binding.gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) binding.gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, binding.gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);

                            // storing URIs in a list to later convert to ParseFiles
                            imageUriList.add(uri);

                            // get the cursor
                            Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                            // move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getContext().getApplicationContext(),mArrayUri);
                            binding.gvGallery.setAdapter(galleryAdapter);
                            binding.gvGallery.setVerticalSpacing(binding.gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) binding.gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, binding.gvGallery.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v(TAG, "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(getContext(), "You haven't picked an image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
