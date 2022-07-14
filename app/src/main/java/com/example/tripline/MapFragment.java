package com.example.tripline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tripline.models.Trip;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        // async map
        supportMapFragment.getMapAsync(googleMap -> onGoogleMapReady(googleMap));
        return view;
    }

    private void onGoogleMapReady(GoogleMap googleMap) {
        // when map is loaded, add pins for all trips
        for (Trip trip : MainActivity.userToDisplayTrips) {
            ParseGeoPoint point = trip.getLocation();
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(trip.getTitle())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }
}
