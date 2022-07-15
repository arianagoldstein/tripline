package com.example.tripline;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tripline.databinding.ActivityMainBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.example.tripline.viewmodels.SearchViewModel;
import com.example.tripline.viewmodels.UserViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;
    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel
        UserViewModel sharedViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        sharedViewModel.setUserToDisplay((User) ParseUser.getCurrentUser());
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        getAllTrips();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_stream, R.id.navigation_search, R.id.navigation_addtrip, R.id.navigation_profile)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navView.setSelectedItemId(R.id.navigation_profile); // default tab should be profile

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.places_api_key), Locale.US);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getAllTrips() {
        // specifying the type of data we want to query
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.include(Trip.KEY_AUTHOR);
        query.include(Trip.KEY_FORMATTED_LOCATION);
        query.include(Trip.KEY_TITLE);
        query.include(Trip.KEY_LOCATION);
        query.include(Trip.KEY_COVER_PHOTO);
        query.include(Trip.KEY_START_DATE);
        query.include(Trip.KEY_DESCRIPTION);
        query.include(Trip.KEY_END_DATE);

        query.findInBackground((trips, e) -> {
            // if there is an exception, e will not be null
            if (e != null) {
                Log.e(TAG, "Issue getting trips", e);
                Toast.makeText(MainActivity.this, "Issue getting trips.", Toast.LENGTH_SHORT).show();
            }
            searchViewModel.setAllTrips(trips);
        });
    }
}
