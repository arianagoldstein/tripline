package com.example.tripline.adapters;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.MainActivity;
import com.example.tripline.R;
import com.example.tripline.databinding.ItemTripStreamBinding;
import com.example.tripline.models.Trip;

import java.util.List;

public class TripStreamAdapter extends RecyclerView.Adapter<TripStreamAdapter.ViewHolder> {

    public static final String TAG = "TripStreamAdapter";
    private Context context;
    private List<Trip> trips;

    public TripStreamAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripStreamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripStreamBinding binding = ItemTripStreamBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripStreamAdapter.ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemTripStreamBinding binding;

        public ViewHolder(ItemTripStreamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Trip trip) {
            // populating the XML elements with the details of this trip
            binding.tvTripTitleStream.setText(trip.getTitle());
            binding.tvLocationStream.setText(trip.getFormattedLocation());
            binding.tvStartDateStream.setText(trip.getFormattedDate(trip.getStartDate()) + " - ");
            binding.tvEndDateStream.setText(trip.getFormattedDate(trip.getEndDate()));
            Glide.with(context).load(trip.getCoverPhoto().getUrl()).into(binding.ivCoverPhotoStream);
            binding.tvDescriptionStream.setText(trip.getDescription());
            binding.tvAuthorNameStream.setText(trip.getAuthor().getFirstName() + " " + trip.getAuthor().getLastName());

            // clicking on the author's name brings you to their profile
            binding.tvAuthorNameStream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.userToDisplay = trip.getAuthor();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isCurrentUser", false);
                    NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_navigation_stream_to_navigation_profile, bundle);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "A trip was clicked!");
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                // getting the trip that the user clicked on
                Trip trip = trips.get(position);

                // TODO: use a ViewModel here instead, this is a placeholder
                ((MainActivity) context).selectedTrip = trip;

                // now we have an instance of the navbar, so we can go anywhere
                NavController navController = Navigation.findNavController(itemView);
                navController.navigate(R.id.action_navigation_stream_to_navigation_tripdetails);

            }
        }

    }
}
